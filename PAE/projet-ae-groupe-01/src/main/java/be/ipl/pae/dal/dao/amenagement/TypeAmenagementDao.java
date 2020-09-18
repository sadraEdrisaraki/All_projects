package be.ipl.pae.dal.dao.amenagement;

import be.ipl.pae.biz.amenagement.TypeAmenagementDto;

import java.util.ArrayList;


public interface TypeAmenagementDao {

  ArrayList<TypeAmenagementDto> getListeTypeAmenagement();

  boolean introduireAmenagement(String nomAmenagement);

}
