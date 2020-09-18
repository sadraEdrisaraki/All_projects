package be.ipl.pae.dal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;


public interface DalServices {

  PreparedStatement getPreparedStatement(String query);

  void prepare(PreparedStatement ps, String query, ArrayList<Object> parameters)
      throws SQLException;
}
