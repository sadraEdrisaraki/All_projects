package be.ipl.pae.biz.amenagement;

import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.amenagement.TypeAmenagementDao;
import be.ipl.pae.exception.FatalException;

import java.util.ArrayList;

public class TypeAmenagementUccImpl implements TypeAmenagementUcc {

  @Inject
  private TypeAmenagementDao typeAmenagementDao;

  @Inject
  private DalServivesDbManager dalServices;


  public TypeAmenagementUccImpl() {}

  @Override
  public ArrayList<TypeAmenagementDto> getListeAmenagement() {

    ArrayList<TypeAmenagementDto> listeAmenagement;

    try {
      dalServices.startTransaction();
      listeAmenagement = typeAmenagementDao.getListeTypeAmenagement();
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }

    return listeAmenagement;
  }



  @Override
  public boolean introduireAmenagement(String nomAmenagement) {
    try {
      dalServices.startTransaction();

      return typeAmenagementDao.introduireAmenagement(nomAmenagement);

    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }


}
