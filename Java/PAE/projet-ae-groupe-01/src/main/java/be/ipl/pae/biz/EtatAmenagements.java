package be.ipl.pae.biz;

import java.io.Serializable;

public enum EtatAmenagements implements Serializable {
  DI("DI"), CC("CC"), DDTC("DDTC"), RV("RV"), DDA("DDA"), MF("MF"), FF("FF");

  private static final long serialVersionUID = 42L;
  private String name;

  EtatAmenagements(String name) {
    this.name = name;
  }

  public String get_name() {
    return this.name;
  }

}
