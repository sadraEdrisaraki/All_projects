package be.ipl.pae.dal.dao.client;


import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServices;
import be.ipl.pae.exception.FatalException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;


public class ClientDaoImpl implements ClientDao {

  @Inject
  private DalServices dalS;



  public ClientDaoImpl() {}

  @Override
  public ArrayList<ClientDto> getClients(ClientDto client) {

    ArrayList<ClientDto> listeClients = new ArrayList<ClientDto>();

    PreparedStatement ps = dalS.getPreparedStatement("SELECT * FROM clients");

    try (ResultSet res = ps.executeQuery()) {
      while (res.next()) {
        ClientDto clientDtoPourListe = client.clone();
        clientDtoPourListe.setIdClient(res.getInt(1));
        clientDtoPourListe.setNom(res.getString(2));
        clientDtoPourListe.setPrenom(res.getString(3));
        clientDtoPourListe.setVille(res.getString(4));
        clientDtoPourListe.setCodePostal(res.getInt(5));
        clientDtoPourListe.setRue(res.getString(6));
        clientDtoPourListe.setNumero(res.getInt(7));
        clientDtoPourListe.setBoite(res.getString(8));
        clientDtoPourListe.setTelephone(res.getString(9));
        clientDtoPourListe.setEmail(res.getString(10));

        listeClients.add(clientDtoPourListe.clone());
      }
      return listeClients;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

  }

  @Override
  public ClientDto inscrire(ClientDto clientDto) {
    PreparedStatement ps = dalS.getPreparedStatement("INSERT INTO clients "
        + "(nom, prenom, ville, code_postal, rue, numero, boite, telephone, email)"
        + "VALUES(?,?,?,?,?,?,?,?,?) RETURNING id_client");

    System.out.println(clientDto.getTelephone());
    try {
      ps.setString(1, clientDto.getNom());
      ps.setString(2, clientDto.getPrenom());
      ps.setString(3, clientDto.getVille());
      ps.setInt(4, clientDto.getCodePostal());
      ps.setString(5, clientDto.getRue());
      ps.setInt(6, clientDto.getNumero());
      ps.setString(7, clientDto.getBoite());
      ps.setString(8, clientDto.getTelephone());
      ps.setString(9, clientDto.getEmail());

    } catch (SQLException excpetion) {
      throw new FatalException(excpetion);
    }

    // Execute la requete
    try (ResultSet result = ps.executeQuery()) {
      if (result.next()) {
        clientDto.setIdClient(result.getInt(1));
      }
    } catch (SQLException excpetion) {
      throw new FatalException(excpetion);
    }

    return clientDto;
  }

  @Override
  public ClientDto inscrireWithoutBoite(ClientDto clientDto) throws FatalException {
    PreparedStatement ps = dalS.getPreparedStatement(
        "INSERT INTO clients " + "(nom, prenom, ville, code_postal, rue, numero, telephone, email)"
            + "VALUES(?,?,?,?,?,?,?,?) RETURNING id_client");

    try {
      ps.setString(1, clientDto.getNom());
      ps.setString(2, clientDto.getPrenom());
      ps.setString(3, clientDto.getVille());
      ps.setInt(4, clientDto.getCodePostal());
      ps.setString(5, clientDto.getRue());
      ps.setInt(6, clientDto.getNumero());
      ps.setString(7, clientDto.getTelephone());
      ps.setString(8, clientDto.getEmail());


    } catch (SQLException excpetion) {
      throw new FatalException("Preparation de la requete impossible");
    }

    // Execute la requete
    try (ResultSet result = ps.executeQuery()) {
      if (result.next()) {
        clientDto.setIdClient(result.getInt(1));
      }
    } catch (SQLException excpetion) {
      throw new FatalException("Execution de la requete impossible");
    }

    return clientDto;
  }

  @Override
  public ClientDto existe(ClientDto clientDtoExistant) {
    PreparedStatement ps =
        dalS.getPreparedStatement("SELECT c.* FROM clients c WHERE c.email like ? ");

    try {
      ps.setString(1, clientDtoExistant.getEmail());
      ResultSet res = ps.executeQuery();
      if (res.next()) {
        clientDtoExistant.setIdClient(res.getInt(1));
        clientDtoExistant.setNom(res.getString(2));
        clientDtoExistant.setPrenom(res.getString(3));
        clientDtoExistant.setVille(res.getString(4));
        clientDtoExistant.setCodePostal(res.getInt(5));
        clientDtoExistant.setRue(res.getString(6));
        clientDtoExistant.setNumero(res.getInt(7));
        clientDtoExistant.setBoite(res.getString(8));
        clientDtoExistant.setTelephone(res.getString(9));
        clientDtoExistant.setEmail(res.getString(10));
        return clientDtoExistant;
      } else {
        return null;
      }

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

  }

  @Override
  public ArrayList<ClientDto> getClientsSansUtilisateurs(ClientDto clientSansUtilisateur) {
    ArrayList<ClientDto> listeClients = new ArrayList<ClientDto>();

    PreparedStatement ps = dalS.getPreparedStatement("SELECT DISTINCT c.* FROM clients c\n"
        + "WHERE c.id_client NOT IN (SELECT u.id_client from utilisateurs u "
        + "WHERE u.type != 'O' AND u.id_client IS NOT NULL)");

    try (ResultSet res = ps.executeQuery()) {
      while (res.next()) {
        ClientDto clientDtoSansUtilisateur = clientSansUtilisateur.clone();
        clientDtoSansUtilisateur.setIdClient(res.getInt(1));
        clientDtoSansUtilisateur.setNom(res.getString(2));
        clientDtoSansUtilisateur.setPrenom(res.getString(3));
        clientDtoSansUtilisateur.setVille(res.getString(4));
        clientDtoSansUtilisateur.setCodePostal(res.getInt(5));
        clientDtoSansUtilisateur.setRue(res.getString(6));
        clientDtoSansUtilisateur.setNumero(res.getInt(7));
        clientDtoSansUtilisateur.setBoite(res.getString(8));
        clientDtoSansUtilisateur.setTelephone(res.getString(9));
        clientDtoSansUtilisateur.setEmail(res.getString(10));

        listeClients.add(clientDtoSansUtilisateur.clone());
      }
      return listeClients;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public ArrayList<ClientDto> getClientsCritereRecherche(ClientDto client,
      Map<String, String> critereRecherche) {
    ArrayList<Object> tabCritereRecherche = new ArrayList<Object>();

    ArrayList<ClientDto> listeClients = new ArrayList<ClientDto>();

    String nomClient = critereRecherche.get("nomClient");
    String villeClient = critereRecherche.get("villeClient");
    String codePostal = critereRecherche.get("codePostalClient");
    String query = "SELECT * FROM clients";

    PreparedStatement ps = null;

    int tailleDictionnaire = 0;
    /* on regarde que le dictionnaire contient au moins un critere de recherche */
    for (Entry<String, String> entry : critereRecherche.entrySet()) {
      String valeur = entry.getValue();
      String valeur1 = entry.getKey();
      System.out.println(valeur1 + ": " + valeur);
      if (!valeur.equals("")) {
        tailleDictionnaire++;
      }
      if (tailleDictionnaire == 2) {
        break;
      }
    }

    if (tailleDictionnaire == 1) {
      ps = dalS.getPreparedStatement(query);
    } else {
      if (!nomClient.equals("")) {
        tabCritereRecherche.add(nomClient);
        query += " WHERE nom=? ";

      }
      if (!villeClient.equals("")) {
        tabCritereRecherche.add("%" + villeClient + "%");
        if (tabCritereRecherche.size() == 1) {
          query += " WHERE ville LIKE ? ";
        } else {
          query += "AND ville LIKE ? ";
        }
      }
      if (!codePostal.equals("")) {
        tabCritereRecherche.add(Integer.valueOf(codePostal));
        if (tabCritereRecherche.size() == 1) {
          query += " WHERE  code_postal=? ";
        } else {
          query += "AND  code_postal=? ";
        }

      }
      ps = dalS.getPreparedStatement(query);
      try {

        dalS.prepare(ps, query, tabCritereRecherche);
      } catch (SQLException exception) {
        throw new FatalException(exception);
      }
    }

    try (ResultSet res = ps.executeQuery()) {
      while (res.next()) {
        ClientDto clientDtoRecherche = client.clone();
        clientDtoRecherche.setIdClient(res.getInt(1));
        clientDtoRecherche.setNom(res.getString(2));
        clientDtoRecherche.setPrenom(res.getString(3));
        clientDtoRecherche.setVille(res.getString(4));
        clientDtoRecherche.setCodePostal(res.getInt(5));
        clientDtoRecherche.setRue(res.getString(6));
        clientDtoRecherche.setNumero(res.getInt(7));
        clientDtoRecherche.setBoite(res.getString(8));
        clientDtoRecherche.setTelephone(res.getString(9));
        clientDtoRecherche.setEmail(res.getString(10));

        listeClients.add(clientDtoRecherche.clone());
      }
      return listeClients;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

  }



}
