package be.ipl.pae.biz.utilisateur;

import be.ipl.pae.biz.TypeUtilisateurEnum;

import java.time.LocalDate;

public interface UtilisateurDto {
  int getId_utilisateur();

  void setId_utilisateur(int idUtilisateur);

  String getNom();

  void setNom(String nom);

  String getPrenom();

  void setPrenom(String prenom);

  String getVille();

  void setVille(String ville);

  String getEmail();

  void setEmail(String email);

  String getPseudo();

  void setPseudo(String pseudo);

  String getMot_de_passe();

  void setMot_de_passe(String motDePasse);

  TypeUtilisateurEnum getType();

  void setType(TypeUtilisateurEnum type);

  int getId_client();

  void setId_client(int idClient);

  LocalDate getDate_inscription();

  void setDate_inscription(LocalDate dateInscription);

  boolean getConfirmationInscription();

  void setConfirmationInscription(boolean newConfirmationInscription);

  UtilisateurImpl clone();

}
