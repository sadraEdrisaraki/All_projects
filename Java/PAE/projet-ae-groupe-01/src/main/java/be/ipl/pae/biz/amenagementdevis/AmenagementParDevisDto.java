package be.ipl.pae.biz.amenagementdevis;

import be.ipl.pae.biz.amenagement.TypeAmenagementDto;
import be.ipl.pae.biz.devis.DevisDto;

public interface AmenagementParDevisDto {

  DevisDto getDevis();

  void setDevis(DevisDto devisDto);

  TypeAmenagementDto getTypeAmenagement();

  void setTypeAmenagement(TypeAmenagementDto typeAmenagement);

}
