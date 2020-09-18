package be.ipl.pae.biz.utilisateur;

import be.ipl.pae.biz.TypeUtilisateurEnum;

import com.owlike.genson.annotation.JsonIgnore;

import org.mindrot.bcrypt.BCrypt;

import java.time.LocalDate;

public class UtilisateurImpl implements UtilisateurBiz, Cloneable {

  private int idUtilisateur;
  private String nom;
  private String prenom;
  private String ville;
  private String email;
  private String pseudo;
  @JsonIgnore
  private String motDePasse;
  private TypeUtilisateurEnum type;
  private int idClient;
  private LocalDate dateInscription;
  private boolean confirmationInscription;


  /**
   * Constructeur vide.
   */
  public UtilisateurImpl() {}

  /**
   * Constructeur sans les champs null.
   */
  public UtilisateurImpl(int idUtilisateur, String nom, String prenom, String ville, String email,
      String pseudo, String motDePasse, LocalDate dateInscription,
      boolean confirmationInscription) {
    super();
    this.idUtilisateur = idUtilisateur;
    this.nom = nom;
    this.prenom = prenom;
    this.ville = ville;
    this.email = email;
    this.pseudo = pseudo;
    this.motDePasse = motDePasse;
    this.dateInscription = dateInscription;
    this.confirmationInscription = confirmationInscription;
  }

  /**
   * Constructeur avec tout les champs.
   */
  public UtilisateurImpl(int idUtilisateur, String nom, String prenom, String ville, String email,
      String pseudo, String motDePasse, TypeUtilisateurEnum type, int idClient,
      LocalDate dateInscription, boolean confirmationInscription) {
    super();
    this.idUtilisateur = idUtilisateur;
    this.nom = nom;
    this.prenom = prenom;
    this.ville = ville;
    this.email = email;
    this.pseudo = pseudo;
    this.motDePasse = motDePasse;
    this.type = type;
    this.idClient = idClient;
    this.dateInscription = dateInscription;
    this.confirmationInscription = confirmationInscription;
  }

  /**
   * Crypte un mot de passe.
   */
  public static String crypterMotDePasse(String motDePasse) {
    String passHashed;
    String salt = BCrypt.gensalt();
    passHashed = BCrypt.hashpw(motDePasse, salt);
    return passHashed;
  }

  /*
   * Verifie un mote de passe crypte avec un mot de passe non crypte
   */

  public static boolean checkMotDePasseCrypte(String motDePasse, String motDePasseHash) {
    return BCrypt.checkpw(motDePasse, motDePasseHash);
  }


  // DTO

  @Override
  public int getId_utilisateur() {
    return idUtilisateur;
  }

  @Override
  public void setId_utilisateur(int idUtilisateur) {
    this.idUtilisateur = idUtilisateur;
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
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public String getPseudo() {
    return pseudo;
  }

  @Override
  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }

  @Override
  public String getMot_de_passe() {
    return motDePasse;
  }

  @Override
  public void setMot_de_passe(String motDePasse) {
    this.motDePasse = motDePasse;
  }

  @Override
  public TypeUtilisateurEnum getType() {
    return type;
  }

  @Override
  public void setType(TypeUtilisateurEnum type) {
    this.type = type;
  }

  @Override
  public int getId_client() {
    return idClient;
  }

  @Override
  public void setId_client(int idClient) {
    this.idClient = idClient;
  }

  @Override
  public LocalDate getDate_inscription() {
    return dateInscription;
  }

  @Override
  public void setDate_inscription(LocalDate dateInscription) {
    this.dateInscription = dateInscription;
  }

  @Override
  public boolean getConfirmationInscription() {
    return confirmationInscription;
  }

  @Override
  public void setConfirmationInscription(boolean newConfirmationInscription) {
    this.confirmationInscription = newConfirmationInscription;
  }

  /**
   * Clone l'uitlisateur.
   */
  public UtilisateurImpl clone() {
    try {
      return (UtilisateurImpl) super.clone();
    } catch (CloneNotSupportedException clonException) {
      clonException.printStackTrace();
    }
    return null;
  }

  @Override
  public String toString() {
    return "UtilisateurImpl [idUtilisateur=" + idUtilisateur + ", nom=" + nom + ", prenom=" + prenom
        + ", ville=" + ville + ", email=" + email + ", pseudo=" + pseudo + ", motDePasse="
        + motDePasse + ", type=" + type + ", idClient=" + idClient + ", dateInscription="
        + dateInscription + "]";
  }

  @Override
  public boolean verifierPseudo() {
    return pseudo != null && pseudo.matches("^[a-zA-Z0-9_-]+$");
  }

  @Override
  public boolean verifierEmail() {
    return email != null && email.matches("^[a-zA-Z0-9\\._-]+@[a-zA-Z0-9\\._-]+\\.[a-z]{2,}$");
  }

  @Override
  public boolean verifierMdp() {
    return motDePasse != null && !motDePasse.matches("");
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
  public boolean verifierVille() {
    return ville != null && ville.matches("^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$");
  }

  @Override
  public boolean verifierType() {
    return type != null;
  }



}
