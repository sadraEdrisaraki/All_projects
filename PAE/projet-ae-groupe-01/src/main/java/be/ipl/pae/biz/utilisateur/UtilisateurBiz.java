package be.ipl.pae.biz.utilisateur;

public interface UtilisateurBiz extends UtilisateurDto {

  boolean verifierPseudo();

  boolean verifierEmail();

  boolean verifierMdp();

  boolean verifierNom();

  boolean verifierPrenom();

  boolean verifierVille();

  boolean verifierType();

}
