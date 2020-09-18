package be.ipl.pae.biz.photo;

import java.time.LocalDate;

public interface PhotoDto {

  int getId_photo();

  void setId_photo(Integer id);

  int getId_amenagement();

  void setId_amenagement(Integer id);

  int getId_devis();

  boolean isVisible();

  void setVisible(boolean visible);

  LocalDate getDate_photo();

  void setDate_photo(LocalDate datePhoto);

  void setPhoto(String image);

  String getPhoto();

  PhotoDto clone();

  void setId_devis(Integer id);
}
