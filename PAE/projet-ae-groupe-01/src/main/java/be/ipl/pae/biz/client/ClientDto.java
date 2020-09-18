package be.ipl.pae.biz.client;

public interface ClientDto {

  void setIdClient(int idClient);

  int getIdClient();

  String getNom();

  void setNom(String nom);

  String getPrenom();

  void setPrenom(String prenom);

  String getVille();

  void setVille(String ville);

  String getRue();

  void setRue(String rue);

  int getCodePostal();

  void setCodePostal(int codePostal);

  int getNumero();

  void setNumero(int numero);

  String getBoite();

  void setBoite(String boite);

  String getTelephone();

  void setTelephone(String telephone);

  String getEmail();

  void setEmail(String email);

  ClientDto clone();
}
