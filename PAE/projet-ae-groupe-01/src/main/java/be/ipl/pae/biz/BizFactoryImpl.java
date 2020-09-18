package be.ipl.pae.biz;

import be.ipl.pae.biz.amenagement.TypeAmenagementDto;
import be.ipl.pae.biz.amenagement.TypeAmenagementImpl;
import be.ipl.pae.biz.amenagementdevis.AmenagementParDevisDto;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.client.ClientImpl;
import be.ipl.pae.biz.devis.DevisDto;
import be.ipl.pae.biz.devis.DevisImpl;
import be.ipl.pae.biz.photo.PhotoDto;
import be.ipl.pae.biz.photo.PhotoImpl;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.biz.utilisateur.UtilisateurImpl;

public class BizFactoryImpl implements BizFactory {

  @Override
  public UtilisateurDto getUtilisateur() {
    return new UtilisateurImpl();
  }

  @Override
  public ClientDto getClient() {
    return new ClientImpl();
  }

  @Override
  public DevisDto getDevis() {
    return new DevisImpl();
  }

  @Override
  public TypeAmenagementDto getTypeAmenagement() {
    return new TypeAmenagementImpl();
  }

  @Override
  public PhotoDto getPhoto() {
    return new PhotoImpl();
  }

  @Override
  public AmenagementParDevisDto getAmenagementParDevis() {
    return null;
  }

}
