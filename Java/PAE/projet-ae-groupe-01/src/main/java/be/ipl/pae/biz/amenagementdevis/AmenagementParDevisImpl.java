package be.ipl.pae.biz.amenagementdevis;

import be.ipl.pae.biz.amenagement.TypeAmenagementDto;
import be.ipl.pae.biz.amenagement.TypeAmenagementImpl;
import be.ipl.pae.biz.devis.DevisDto;
import be.ipl.pae.biz.devis.DevisImpl;

public class AmenagementParDevisImpl implements AmenagementParDevisDto {

  private DevisImpl devis;
  private TypeAmenagementImpl amenagement;

  @Override
  public DevisDto getDevis() {
    return devis;
  }

  @Override
  public void setDevis(DevisDto devisDto) {
    this.devis = (DevisImpl) devisDto;
  }

  @Override
  public TypeAmenagementDto getTypeAmenagement() {
    return amenagement;
  }

  @Override
  public void setTypeAmenagement(TypeAmenagementDto typeAmenagement) {
    this.amenagement = (TypeAmenagementImpl) typeAmenagement;
  }


}
