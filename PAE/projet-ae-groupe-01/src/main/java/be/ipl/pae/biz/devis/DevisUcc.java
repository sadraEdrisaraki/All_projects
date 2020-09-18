package be.ipl.pae.biz.devis;

import be.ipl.pae.biz.EtatAmenagements;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface DevisUcc {
  ArrayList<HashMap<String, String>> getDevisClient(int idClient);

  ArrayList<DevisDto> getDevisClients();

  ArrayList<HashMap<String, String>> getDevisRealisation(Map<String, String> critereRecherche);

  DevisDto introduireDevis(DevisDto devisDto, int[] tabIdTypeAmenagement);

  void setEtatDevis(int idDevis, EtatAmenagements etat);

  boolean setDateDebutTravaux(LocalDate date, int idDevis);

  void setPhotoPreferee(int idPhoto, int idDevis);

  HashMap<String, String> getVisualisationDevis(int idDevis);

}
