
package be.ipl.pae.dal.dao.photo;

import be.ipl.pae.biz.photo.PhotoDto;

import java.util.ArrayList;


public interface PhotoDao {

  PhotoDto enregistrerPhoto(PhotoDto photo);

  ArrayList<PhotoDto> getPhotosVisible();

  ArrayList<PhotoDto> getPhotoParAmenagement(int idAmenagement);

  void rendreVisiblePhotos(int idDevis);

  ArrayList<PhotoDto> getPhotosDevis(int idDevis);

}
