package be.ipl.pae.biz.amenagement;

import java.util.ArrayList;

public interface TypeAmenagementUcc {

  ArrayList<TypeAmenagementDto> getListeAmenagement();

  boolean introduireAmenagement(String nomAmenagement);
}
