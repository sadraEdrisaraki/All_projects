package be.ipl.pae.biz.client;

public class ClientImpl implements ClientBiz, Cloneable {

  private int idClient;
  private Integer codePostal;
  private Integer numero;
  private String nom;
  private String prenom;
  private String ville;
  private String rue;
  private String boite;
  private String telephone;
  private String email;

  public ClientImpl() {}


  @Override
  public int getIdClient() {
    return idClient;
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
  public String getPrenom() {
    return prenom;
  }

  @Override
  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  @Override
  public String getVille() {
    return ville;
  }

  @Override
  public void setVille(String ville) {
    this.ville = ville;
  }

  @Override
  public String getRue() {
    return rue;
  }

  @Override
  public void setRue(String rue) {
    this.rue = rue;
  }

  @Override
  public int getCodePostal() {
    return codePostal;
  }

  @Override
  public void setCodePostal(int codePostal) {
    this.codePostal = codePostal;
  }

  @Override
  public int getNumero() {
    return numero;
  }

  @Override
  public void setNumero(int numero) {
    this.numero = numero;
  }

  @Override
  public String getBoite() {
    return boite;
  }

  @Override
  public void setBoite(String boite) {
    this.boite = boite;
  }

  @Override
  public String getTelephone() {
    return telephone;
  }

  @Override
  public void setTelephone(String telephone) {
    this.telephone = telephone;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public ClientImpl clone() {
    try {
      return (ClientImpl) super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      cloneNotSupportedException.printStackTrace();
    }
    return null;
  }
  // Methode buiseness


  @Override
  public void setIdClient(int idClient) {
    this.idClient = idClient;

  }

  @Override
  public String toString() {
    return "ClientImpl [idClient=" + idClient + ", nom=" + nom + ", prenom=" + prenom + ", rue="
        + rue + ", numero=" + numero + ", code postal=" + codePostal + ", boite=" + boite
        + ", ville=" + ville + ", telephone=" + telephone + ", email=" + email
        + ", dateInscription=" + "]";
  }

  @Override
  public boolean verifierNom() {
    return nom != null && nom.matches("^[\\D]+$") && !nom.matches("^.*[_].*$");
  }

  @Override
  public boolean verifierPrenom() {
    return prenom != null && prenom.matches("^[\\D]+$") && !prenom.matches("^.*[_].*$");
  }

  @Override
  public boolean verifierRue() {
    return rue != null && rue.matches("^[\\D]+$") && !rue.matches("^.*[_].*$");
  }

  @Override
  public boolean verifierNumero() {
    return numero != null;
  }

  @Override
  public boolean verifierCodePostal() {
    return codePostal != null;
  }

  @Override
  public boolean verifierBoite() {
    return boite != null && boite.matches("^[a-zA-Z0-9]+$");
  }

  @Override
  public boolean verifierVille() {
    return ville != null && ville.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$");
  }

  @Override
  public boolean verifierEmail() {
    return email != null && email.matches("^[a-zA-Z0-9\\._-]+@[a-zA-Z0-9\\._-]+\\.[a-z]{2,}$");
  }

  @Override
  public boolean verifierTelephone() {
    return telephone != null && telephone.matches("^[+]?[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$");
  }
}
