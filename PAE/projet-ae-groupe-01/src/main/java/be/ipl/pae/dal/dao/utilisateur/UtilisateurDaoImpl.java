package be.ipl.pae.dal.dao.utilisateur;

import static be.ipl.pae.config.CheckUtils.checkObject;
import static be.ipl.pae.config.CheckUtils.checkString;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.TypeUtilisateurEnum;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServices;
import be.ipl.pae.exception.FatalException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;


public class UtilisateurDaoImpl implements UtilisateurDao {

  @Inject
  private DalServices dalS;
  @Inject
  private BizFactory bizFactory;

  /**
   * creer et recupere la connection et la factory.
   */
  public UtilisateurDaoImpl() {}

  private UtilisateurDto setUtilisateurDto(int idUtilisateur, String nom, String prenom,
      String ville, String email, String pseudo, String motDePasse, TypeUtilisateurEnum type,
      LocalDate date, int idClient, boolean confirmationInscription) {

    UtilisateurDto utilisateur = bizFactory.getUtilisateur();
    utilisateur.setId_utilisateur(idUtilisateur);
    utilisateur.setNom(nom);
    utilisateur.setPrenom(prenom);
    utilisateur.setVille(ville);
    utilisateur.setEmail(email);
    utilisateur.setPseudo(pseudo);
    utilisateur.setMot_de_passe(motDePasse);
    utilisateur.setType(type);
    utilisateur.setDate_inscription(date);
    utilisateur.setId_client(idClient);
    utilisateur.setConfirmationInscription(confirmationInscription);

    return utilisateur.clone();
  }

  @Override
  public ArrayList<UtilisateurDto> getUtilisateurs(Map<String, String> critereRecherche) {

    ArrayList<Object> tabCritereRecherche = new ArrayList<Object>();

    ArrayList<UtilisateurDto> listeUtilisateur = new ArrayList<UtilisateurDto>();

    PreparedStatement ps = null;

    String query = "SELECT * FROM utilisateurs";
    String nomUtilisateur = critereRecherche.get("nomUtilisateur");
    String villeUtilisateur = critereRecherche.get("villeUtilisateur");

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
      if (!nomUtilisateur.equals("")) {
        tabCritereRecherche.add(nomUtilisateur);
        query += " WHERE nom=? ";
        System.out.println(query);
      }
      if (!villeUtilisateur.equals("")) {
        tabCritereRecherche.add("%" + villeUtilisateur + "%");
        if (tabCritereRecherche.size() == 1) {
          query += " WHERE ville LIKE ? ";
        } else {
          query += "AND ville LIKE ? ";
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
        LocalDate date = res.getTimestamp(11).toLocalDateTime().toLocalDate();
        TypeUtilisateurEnum type = TypeUtilisateurEnum.valueOf(res.getString(8));

        listeUtilisateur.add(setUtilisateurDto(res.getInt(1), res.getString(2), res.getString(3),
            res.getString(4), res.getString(5), res.getString(6), res.getString(7), type, date,
            res.getInt(9), res.getBoolean(10)));

      }

      return listeUtilisateur;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public String getUtilisateurMotDePasse(String pseudo) {
    PreparedStatement ps =
        dalS.getPreparedStatement("SELECT u.mot_de_passe FROM utilisateurs u WHERE u.pseudo=?");

    try {
      ps.setString(1, pseudo);
      try (ResultSet res = ps.executeQuery()) {
        if (res.next()) {
          return res.getString(1);
        }
      } catch (SQLException exception) {
        throw new FatalException(exception);
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return null;
  }

  @Override
  public UtilisateurDto getUtilisateur(int idUtilisateur) throws FatalException {

    PreparedStatement ps =
        dalS.getPreparedStatement("SELECT * FROM utilisateurs WHERE id_utilisateur=?");

    try {
      ps.setInt(1, idUtilisateur);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet resultat = ps.executeQuery()) {
      if (resultat.next()) {
        LocalDate date = resultat.getTimestamp(11).toLocalDateTime().toLocalDate();
        TypeUtilisateurEnum type = TypeUtilisateurEnum.valueOf(resultat.getString(8));
        return setUtilisateurDto(resultat.getInt(1), resultat.getString(2), resultat.getString(3),
            resultat.getString(4), resultat.getString(5), resultat.getString(6),
            resultat.getString(7), type, date, resultat.getInt(9), resultat.getBoolean(10));
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return null;
  }

  @Override
  public UtilisateurDto getUtilisateur(String pseudo) {
    PreparedStatement ps = dalS.getPreparedStatement("SELECT * FROM utilisateurs WHERE pseudo = ?");

    try {
      ps.setString(1, pseudo);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    try (ResultSet res = ps.executeQuery()) {
      if (res.next()) {
        LocalDate date = res.getTimestamp(11).toLocalDateTime().toLocalDate();
        TypeUtilisateurEnum type = TypeUtilisateurEnum.valueOf(res.getString(8));
        return setUtilisateurDto(res.getInt(1), res.getString(2), res.getString(3),
            res.getString(4), res.getString(5), res.getString(6), res.getString(7), type, date,
            res.getInt(9), res.getBoolean(10));
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return null;
  }


  @Override
  public boolean estCompteActivee(String pseudo) {
    checkString(pseudo);
    PreparedStatement ps = dalS.getPreparedStatement("SELECT u.id_utilisateur FROM utilisateurs u "
        + "WHERE u.pseudo like ? AND u.id_client IS NOT NULL AND u.type NOT LIKE 'O'");

    try {
      ps.setString(1, pseudo);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      if (res.next()) {
        return true;
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    return false;
  }

  @Override
  public boolean deleteUtilisateur(int id) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean pseudoEstUnique(String pseudo) {
    checkString(pseudo);
    PreparedStatement ps = dalS.getPreparedStatement(
        "SELECT u.pseudo FROM utilisateurs u WHERE LOWER(u.pseudo) LIKE LOWER(?)");
    try {
      ps.setString(1, pseudo);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      if (res.next()) {
        return false;
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    return true;
  }

  @Override
  public boolean emailExistePas(String email) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "SELECT u.email FROM utilisateurs u WHERE LOWER(u.email) LIKE LOWER(?)");
    try {
      ps.setString(1, email);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      if (res.next()) {
        return false;
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    return true;
  }

  @Override
  public UtilisateurDto inscrire(UtilisateurDto utilisateurDto) {
    checkObject(utilisateurDto);
    PreparedStatement ps = dalS.getPreparedStatement(
        "INSERT INTO utilisateurs " + "(nom, prenom, ville, email, pseudo ,mot_de_passe ,type ,"
            + " confirmation_inscription, date_inscription)"
            + " VALUES(?,?,?,?,?,?,?,?,NOW()) RETURNING id_utilisateur");
    try {
      ps.setString(1, utilisateurDto.getNom());
      ps.setString(2, utilisateurDto.getPrenom());
      ps.setString(3, utilisateurDto.getVille());
      ps.setString(4, utilisateurDto.getEmail());
      ps.setString(5, utilisateurDto.getPseudo());
      ps.setString(6, utilisateurDto.getMot_de_passe());
      ps.setString(7, utilisateurDto.getType().get_type());
      ps.setBoolean(8, utilisateurDto.getConfirmationInscription());

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      // Rcupere l id lors de la creation et le renvoie
      if (res.next()) {
        utilisateurDto.setId_utilisateur(res.getInt(1));
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return utilisateurDto;
  }

  @Override
  public ArrayList<UtilisateurDto> getUtilisateursSansClient() {
    ArrayList<UtilisateurDto> listeUtilisateurs = new ArrayList<UtilisateurDto>();

    PreparedStatement ps = dalS
        .getPreparedStatement("SELECT * FROM utilisateurs WHERE id_client IS NULL AND TYPE != 'O'");

    try (ResultSet res = ps.executeQuery()) {

      while (res.next()) {
        LocalDate date = res.getTimestamp(11).toLocalDateTime().toLocalDate();

        TypeUtilisateurEnum typeUtilisateur = TypeUtilisateurEnum.valueOf(res.getString(8));
        listeUtilisateurs.add(setUtilisateurDto(res.getInt(1), res.getString(2), res.getString(3),
            res.getString(4), res.getString(5), res.getString(6), res.getString(7), typeUtilisateur,
            date, res.getInt(9), res.getBoolean(10)));

      }

      return listeUtilisateurs;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public boolean lierCompteClientUtilisateur(ClientDto client, UtilisateurDto utilisateur) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "UPDATE utilisateurs SET id_client = ? WHERE id_utilisateur = ? RETURNING id_utilisateur");

    try {
      ps.setInt(1, client.getIdClient());
      ps.setInt(2, utilisateur.getId_utilisateur());
      ResultSet res = ps.executeQuery();
      if (res.next()) {
        return true;
      }

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    return false;
  }

  @Override
  public UtilisateurDto getUtilisateurByMail(String mailUtilisateur) {

    PreparedStatement ps =
        dalS.getPreparedStatement("SELECT u.id_utilisateur, u.nom,u.prenom,u.ville,"
            + "u.email,u.pseudo,u.mot_de_passe,u.type,u.id_client,u.date_inscription,"
            + " u.confirmation_inscription " + "FROM utilisateurs u WHERE u.email=? ");

    try {
      ps.setString(1, mailUtilisateur);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    try (ResultSet res = ps.executeQuery()) {
      if (res.next()) {
        System.out.println("azeae");
        LocalDate date = res.getTimestamp(10).toLocalDateTime().toLocalDate();

        TypeUtilisateurEnum type = TypeUtilisateurEnum.valueOf(res.getString(8));
        return setUtilisateurDto(res.getInt(1), res.getString(2), res.getString(3),
            res.getString(4), res.getString(5), res.getString(6), res.getString(7), type, date,
            res.getInt(9), res.getBoolean(11));
      }
    } catch (SQLException exception) {

      throw new FatalException(exception);
    }
    return null;
  }

  @Override
  public boolean definirOuvrier(UtilisateurDto utilisateur) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "UPDATE utilisateurs SET type = 'O' WHERE email = ? RETURNING id_utilisateur");

    try {
      ps.setString(1, utilisateur.getEmail());
      ResultSet res = ps.executeQuery();
      if (res.next()) {
        return true;
      }

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    return false;
  }

  @Override
  public ArrayList<UtilisateurDto> getUtilisateurNonConfirme() {
    PreparedStatement ps =
        dalS.getPreparedStatement("SELECT * from utilisateurs where NOT confirmation_inscription");

    ArrayList<UtilisateurDto> utilisateurs = new ArrayList<UtilisateurDto>();

    try (ResultSet res = ps.executeQuery()) {

      while (res.next()) {
        LocalDate date = res.getTimestamp(11).toLocalDateTime().toLocalDate();

        TypeUtilisateurEnum type = TypeUtilisateurEnum.valueOf(res.getString(8));
        utilisateurs.add(setUtilisateurDto(res.getInt(1), res.getString(2), res.getString(3),
            res.getString(4), res.getString(5), res.getString(6), res.getString(7), type, date,
            res.getInt(9), res.getBoolean(10)));
      }

      return utilisateurs;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public boolean confirmerInscription(UtilisateurDto utilisateur) {
    PreparedStatement ps =
        dalS.getPreparedStatement("UPDATE utilisateurs SET confirmation_inscription = true"
            + " WHERE id_utilisateur = ? RETURNING id_utilisateur");

    // Prepare la query
    try {
      ps.setInt(1, utilisateur.getId_utilisateur());
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    // Execute celle-ci
    try (ResultSet res = ps.executeQuery()) {
      // s'il y a une ligne qui est update, return true
      if (res.next()) {
        return true;
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return false;
  }
}
