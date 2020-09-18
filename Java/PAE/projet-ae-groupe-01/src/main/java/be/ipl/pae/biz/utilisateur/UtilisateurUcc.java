package be.ipl.pae.biz.utilisateur;

import be.ipl.pae.exception.CompteNonActiveeException;

import java.util.ArrayList;
import java.util.Map;

public interface UtilisateurUcc {

  UtilisateurDto seConnecter(String pseudo, String motDePasse) throws CompteNonActiveeException;

  UtilisateurDto getUtilisateur(String pseudo);

  UtilisateurDto getUtilisateur(int idUtilisateur);

  UtilisateurDto inscrireUtilisateur(UtilisateurDto utilisateurDto);

  ArrayList<UtilisateurDto> getUtilisateurs(Map<String, String> map);

  ArrayList<UtilisateurDto> getUtilisateursSansClient();

  ArrayList<UtilisateurDto> getUtilisateurNonConfirme();

  void lierCompteClientUtilisateur(String mailClient, String mailUtilisateur);

  void definirCompteOuvrier(String email);

  boolean confirmerInscription(UtilisateurDto utilisateur);

}
