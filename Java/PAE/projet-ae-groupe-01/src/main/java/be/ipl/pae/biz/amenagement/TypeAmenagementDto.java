package be.ipl.pae.biz.amenagement;



public interface TypeAmenagementDto {

  String getNom();

  void setNom(String nom);

  int getId_amenagement();

  void setId_amenagement(int idAmenagement);

  TypeAmenagementDto clone();

}
