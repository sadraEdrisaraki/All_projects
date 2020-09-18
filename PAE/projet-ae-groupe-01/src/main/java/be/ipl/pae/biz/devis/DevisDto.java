package be.ipl.pae.biz.devis;

import be.ipl.pae.biz.EtatAmenagements;

import java.time.LocalDate;

public interface DevisDto {
  int getIdDevis();

  void setIdDevis(int idDevis);

  int getIdClient();

  void setIdClient(int idClient);

  LocalDate getDateDevis();

  void setDateDevis(LocalDate dateDevis);

  double getMontantTotal();

  void setMontantTotal(double montantTotal);

  int getDureeTravaux();

  void setDureeTravaux(int dureeTravaux);

  int getPhotoPrefere();

  void setPhotoPrefere(int photoPrefere);

  EtatAmenagements getEtat();

  void setEtat(EtatAmenagements etat);

  LocalDate getDateDebutTravaux();

  void setDateDebutTravaux(LocalDate dateDebutTravaux);

  boolean isVisibiliteAmenagement();

  void setVisibiliteAmenagement(boolean visibiliteAmenagement);

  DevisDto clone();
}
