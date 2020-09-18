package be.ipl.pae.biz;

import be.ipl.pae.biz.amenagement.TypeAmenagementDto;
import be.ipl.pae.biz.amenagementdevis.AmenagementParDevisDto;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.devis.DevisDto;
import be.ipl.pae.biz.photo.PhotoDto;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;


public interface BizFactory {

  UtilisateurDto getUtilisateur();

  ClientDto getClient();

  DevisDto getDevis();

  TypeAmenagementDto getTypeAmenagement();

  PhotoDto getPhoto();

  AmenagementParDevisDto getAmenagementParDevis();

}
