package be.ipl.pae.dal.dao.devis;

import be.ipl.pae.biz.EtatAmenagements;
import be.ipl.pae.biz.devis.DevisDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DevisDao {
  ArrayList<HashMap<String, String>> getDevis(int idClient);

  ArrayList<DevisDto> getDevis();

  ArrayList<HashMap<String, String>> getDevisRealisation(Map<String, String> map);

  boolean deleteDevis(int id);

  boolean updateDevis(DevisDto utilisateur);


  DevisDto introduireDevis(DevisDto devisDto);

  boolean introduireListeAmenagement(DevisDto devis, int[] listeAmenagement);

  void setEtatDevis(int idDevis, EtatAmenagements etat);

  boolean setDateDebutTravaux(LocalDate date, int idDevis);

  void rendreVisible(int idDevis);

  void setPhotoPreferee(int idPhoto, int idDevis);

  HashMap<String, String> getVisualisationDevis(int idDevis);

  DevisDto getDevisById(int idDevis);

}
