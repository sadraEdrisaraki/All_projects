package be.ipl.pae.dal.dao.utilisateur;

import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.exception.FatalException;

import java.util.ArrayList;
import java.util.Map;

public interface UtilisateurDao {

  String getUtilisateurMotDePasse(String pseudo);

  UtilisateurDto getUtilisateur(String pseudo);

  UtilisateurDto getUtilisateur(int idUtilisateur) throws FatalException;

  boolean deleteUtilisateur(int id);

  boolean pseudoEstUnique(String pseudo);

  boolean emailExistePas(String email);

  UtilisateurDto inscrire(UtilisateurDto utilisateurDto);

  ArrayList<UtilisateurDto> getUtilisateurs(Map<String, String> critereRecherche);

  boolean estCompteActivee(String pseudo);

  ArrayList<UtilisateurDto> getUtilisateursSansClient();

  ArrayList<UtilisateurDto> getUtilisateurNonConfirme();

  boolean lierCompteClientUtilisateur(ClientDto client, UtilisateurDto utilisateur);

  UtilisateurDto getUtilisateurByMail(String mailUtilisateur);

  boolean definirOuvrier(UtilisateurDto utilisateur);

  boolean confirmerInscription(UtilisateurDto utilisateur);

}
