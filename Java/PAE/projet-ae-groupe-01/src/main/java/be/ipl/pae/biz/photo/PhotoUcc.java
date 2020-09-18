package be.ipl.pae.biz.photo;

import java.util.ArrayList;

public interface PhotoUcc {

  PhotoDto enregistrerPhoto(PhotoDto photo);

  /**
   * Retourne toutes les photos visible pour la page d accueil.
   */
  ArrayList<PhotoDto> getPhotosVisible();

  ArrayList<PhotoDto> getPhotosDevis(int idDevis);

  ArrayList<PhotoDto> getPhotosParAmenagement(int idAmenagement);

}
