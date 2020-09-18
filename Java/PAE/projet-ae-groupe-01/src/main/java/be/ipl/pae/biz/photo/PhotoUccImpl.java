package be.ipl.pae.biz.photo;

import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.photo.PhotoDao;
import be.ipl.pae.exception.FatalException;

import java.util.ArrayList;

public class PhotoUccImpl implements PhotoUcc {

  @Inject
  private PhotoDao photoDao;

  @Inject
  private DalServivesDbManager dalServices;


  @Override
  public PhotoDto enregistrerPhoto(PhotoDto photo) {
    try {
      dalServices.startTransaction();
      photo = photoDao.enregistrerPhoto(photo);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return photo;
  }


  @Override
  public ArrayList<PhotoDto> getPhotosVisible() {
    try {
      dalServices.startTransaction();
      return photoDao.getPhotosVisible();
    } catch (FatalException exception) {
      dalServices.rollback();
      throw exception;
    } finally {
      dalServices.commit();
    }
  }


  @Override
  public ArrayList<PhotoDto> getPhotosDevis(int idDevis) {
    try {
      dalServices.startTransaction();
      return photoDao.getPhotosDevis(idDevis);
    } catch (FatalException exception) {
      dalServices.rollback();
      throw exception;
    } finally {
      dalServices.commit();
    }
  }


  @Override
  public ArrayList<PhotoDto> getPhotosParAmenagement(int idAmenagement) {
    try {
      dalServices.startTransaction();
      return photoDao.getPhotoParAmenagement(idAmenagement);
    } catch (FatalException exception) {
      dalServices.rollback();
      throw exception;
    } finally {
      dalServices.commit();
    }
  }


}
