package be.ipl.pae.biz.photo;

import com.owlike.genson.annotation.JsonDateFormat;

import java.time.LocalDate;

public class PhotoImpl implements PhotoBiz, Cloneable {

  private int idPhoto;
  private int idAmenagement;
  private int idDevis;

  @JsonDateFormat
  private LocalDate datePhoto;

  private boolean visible;
  private String photo;

  /**
   * Constructeur.
   */

  public PhotoImpl(int idPhoto, int idAmenagement, int idDevis, boolean visible,
      LocalDate datePhoto) {


    super();
    this.idPhoto = idPhoto;
    this.idAmenagement = idAmenagement;
    this.idDevis = idDevis;
    this.visible = visible;
    this.datePhoto = datePhoto;
  }


  /**
   * Constructeur vide.
   */
  public PhotoImpl() {}



  @Override
  public int getId_photo() {
    return idPhoto;
  }

  @Override
  public int getId_amenagement() {
    return idAmenagement;
  }

  @Override
  public int getId_devis() {
    return idDevis;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public void setVisible(boolean visible) {
    this.visible = visible;
  }

  @Override
  public LocalDate getDate_photo() {
    return datePhoto;
  }

  /**
   * Clone .
   */
  public PhotoImpl clone() {
    try {
      return (PhotoImpl) super.clone();
    } catch (CloneNotSupportedException clonException) {
      clonException.printStackTrace();
    }
    return null;
  }



  @Override
  public void setId_photo(Integer id) {
    this.idPhoto = id;
  }



  @Override
  public void setId_amenagement(Integer id) {
    this.idAmenagement = id;

  }



  @Override
  public void setId_devis(Integer id) {
    this.idDevis = id;
  }


  @Override
  public void setDate_photo(LocalDate datePhoto) {
    this.datePhoto = datePhoto;
  }

  @Override
  public void setPhoto(String image) {
    this.photo = image;
  }



  @Override
  public String getPhoto() {
    return this.photo;
  }



}
