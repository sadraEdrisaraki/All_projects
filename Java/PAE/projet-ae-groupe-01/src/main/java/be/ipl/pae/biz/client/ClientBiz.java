package be.ipl.pae.biz.client;

public interface ClientBiz extends ClientDto {

  boolean verifierNom();

  boolean verifierPrenom();

  boolean verifierRue();

  boolean verifierNumero();

  boolean verifierCodePostal();

  boolean verifierBoite();

  boolean verifierVille();

  boolean verifierEmail();

  boolean verifierTelephone();
}
