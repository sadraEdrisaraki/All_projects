package be.ipl.pae.biz.amenagement;

public class TypeAmenagementImpl implements TypeAmenagementBiz, Cloneable {

  private int idAmenagement;
  private String nom;

  /**
   * Constructeur vide.
   */
  public TypeAmenagementImpl() {}

  /**
   * Constructeur.
   */
  public TypeAmenagementImpl(int idAmenagement, String nom) {
    super();
    this.idAmenagement = idAmenagement;
    this.nom = nom;
  }

  @Override
  public String getNom() {
    return nom;
  }

  @Override
  public void setNom(String nom) {
    this.nom = nom;
  }

  @Override
  public int getId_amenagement() {
    return idAmenagement;
  }

  @Override
  public void setId_amenagement(int idAmenagement) {
    this.idAmenagement = idAmenagement;
  }

  /**
   * Clone.
   */
  public TypeAmenagementImpl clone() {
    try {
      return (TypeAmenagementImpl) super.clone();
    } catch (CloneNotSupportedException exception) {
      exception.printStackTrace();
    }
    return null;
  }
}
