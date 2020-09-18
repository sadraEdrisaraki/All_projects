package be.ipl.pae.biz;

public enum TypeUtilisateurEnum {

  C("C"), O("O"), P("P");

  private String type;

  TypeUtilisateurEnum(String type) {
    this.type = type;
  }

  public String get_type() {
    return this.type;
  }

}
