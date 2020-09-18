package be.ipl.pae.dal;

import be.ipl.pae.config.InjectionService;
import be.ipl.pae.exception.FatalException;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;


public class DalServicesImpl implements DalServices, DalServivesDbManager {


  // Retient la connexion utilise dans son thread
  private static ThreadLocal<Connection> threadLocalConn = new ThreadLocal<>();

  // Retient les connexions
  private PoolingDataSource<PoolableConnection> dataSource;

  private void initProperties() {

    ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
        InjectionService.getPropsData("db_conn.properties", "url_db"),
        InjectionService.getPropsData("db_conn.properties", "pseudo_db"),
        InjectionService.getPropsData("db_conn.properties", "mot_de_passe_db"));

    PoolableConnectionFactory poolableConnectionFactory =
        new PoolableConnectionFactory(connectionFactory, null);

    ObjectPool<PoolableConnection> connectionPool =
        new GenericObjectPool<>(poolableConnectionFactory);

    poolableConnectionFactory.setPool(connectionPool);

    dataSource = new PoolingDataSource<>(connectionPool);
  }

  /**
   * Initie la connection a la db.
   */
  public DalServicesImpl() {

    try {
      Class.forName(InjectionService.getPropsData("db_conn.properties", "driver"));
    } catch (ClassNotFoundException exception) {
      System.out.println("Driver PostgreSQL not found");
      System.exit(1);
    }

    initProperties();
  }

  @Override
  public PreparedStatement getPreparedStatement(String query) {

    PreparedStatement ps;
    try {
      ps = threadLocalConn.get().prepareStatement(query);
      return ps;
    } catch (SQLException exc) {
      throw new FatalException(exc);
    }
  }


  @Override
  public void startTransaction() {
    if (threadLocalConn.get() == null) {
      try {
        threadLocalConn.set(dataSource.getConnection());
      } catch (SQLException exc) {
        throw new FatalException(exc);
      }
    }

    try {
      // desactive les commit auto qui se font apres une execution d une query
      threadLocalConn.get().setAutoCommit(false);
    } catch (SQLException exc) {
      throw new FatalException(exc);
    }
  }

  @Override
  public void commit() {
    try {
      threadLocalConn.get().commit();
      threadLocalConn.get().setAutoCommit(true);
      threadLocalConn.get().close();
      threadLocalConn.set(null);
    } catch (SQLException exc) {
      throw new FatalException(exc);
    }
  }

  /**
   * Rollback sur la connexion entiere.
   * 
   * @throws FatalException Lance une fatal exception si une erreur se produit pendant le rollback.
   */
  @Override
  public void rollback() {
    try {
      threadLocalConn.get().rollback();
    } catch (SQLException exc) {
      throw new FatalException(exc);
    }

  }

  @Override
  public void prepare(PreparedStatement ps, String query, ArrayList<Object> parameters)
      throws SQLException {

    System.out.println("PREPARATIONDESCRITERE");
    for (int i = 0; i < parameters.size(); i++) {
      System.out.println("parametre a specifie: " + parameters.get(i));
      Object parameter = parameters.get(i);
      System.out.println("TYPE DU PARAMETRE " + parameter.getClass().getSimpleName());
      if (parameter != null) {

        switch (parameter.getClass().getSimpleName()) {

          case "String":
            System.out.println(i + 1);
            System.out.println(parameter);
            ps.setString(i + 1, (String) parameter);
            System.out.println("Reussi");

            break;
          case "Double":
            ps.setDouble(i + 1, (Double) parameter);
            break;
          case "Integer":
            ps.setInt(i + 1, (int) parameter);
            break;

          case "Date":
            ps.setDate(i + 1, (Date) parameter);
            break;

          case "BigDecimal":
            ps.setBigDecimal(i + 1, (BigDecimal) parameter);
            break;

          case "Time":
            ps.setTime(i + 1, (Time) parameter);
            break;

          default:
            break;
        }
      }
    }
  }
}
