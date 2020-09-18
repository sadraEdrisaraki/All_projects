package be.ipl.pae.dal.dao.amenagement;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.amenagement.TypeAmenagementDto;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServices;
import be.ipl.pae.exception.FatalException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class TypeAmenagementDaoImpl implements TypeAmenagementDao {

  @Inject
  private DalServices dalServices;
  @Inject
  private BizFactory bizFactory;

  public TypeAmenagementDaoImpl() {}

  @Override
  public ArrayList<TypeAmenagementDto> getListeTypeAmenagement() {

    ArrayList<TypeAmenagementDto> listeAmen = new ArrayList<TypeAmenagementDto>();

    PreparedStatement ps = dalServices
        .getPreparedStatement("SELECT t.id_amenagement, t.nom FROM types_amenagements t");

    try (ResultSet res = ps.executeQuery()) {
      TypeAmenagementDto typeAmenagement = bizFactory.getTypeAmenagement();
      while (res.next()) {

        typeAmenagement.setId_amenagement(res.getInt(1));
        typeAmenagement.setNom(res.getString(2));
        listeAmen.add(typeAmenagement.clone());
      }

      if (listeAmen.size() == 0) {
        return null;
      }
      return listeAmen;
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public boolean introduireAmenagement(String nomAmenagement) {
    PreparedStatement ps = dalServices.getPreparedStatement(
        "INSERT INTO types_amenagements(nom)" + "VALUES(?) RETURNING id_amenagement");
    try {
      ps.setString(1, nomAmenagement);
      ResultSet res = ps.executeQuery();

      if (!res.next()) {
        return false;
      }

      return true;
    } catch (Exception exception) {
      throw new FatalException(exception);
    }

    // TODO Auto-generated method stub

  }
}
