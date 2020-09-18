package be.ipl.pae.dal.dao.devis;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.EtatAmenagements;
import be.ipl.pae.biz.devis.DevisDto;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServices;
import be.ipl.pae.exception.FatalException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class DevisDaoImpl implements DevisDao {

  @Inject
  private DalServices dalS;
  @Inject
  private BizFactory bizFactory;


  /**
   * Constructeur de devisDaoImpl.
   */
  public DevisDaoImpl() {}

  private DevisDto setDevisDto(int idDevis, int idClient, LocalDate dateDevis, double montantTotal,
      int dureeTravaux, EtatAmenagements etat, LocalDate dateDebutTravaux, boolean visibilite) {
    DevisDto devisDto = bizFactory.getDevis();
    devisDto.setIdDevis(idDevis);
    devisDto.setIdClient(idClient);
    devisDto.setDateDevis(dateDevis);
    devisDto.setMontantTotal(montantTotal);
    devisDto.setDureeTravaux(dureeTravaux);
    devisDto.setEtat(etat);
    devisDto.setDateDebutTravaux(dateDebutTravaux);
    devisDto.setVisibiliteAmenagement(visibilite);
    return devisDto.clone();
  }

  private DevisDto setDevisDto(int idDevis, int idClient, LocalDate dateDevis, double montantTotal,
      int dureeTravaux, EtatAmenagements etat, LocalDate dateDebutTravaux, boolean visibilite,
      int photoPref) {
    DevisDto devisDto = bizFactory.getDevis();
    devisDto.setIdDevis(idDevis);
    devisDto.setIdClient(idClient);
    devisDto.setDateDevis(dateDevis);
    devisDto.setMontantTotal(montantTotal);
    devisDto.setDureeTravaux(dureeTravaux);
    devisDto.setEtat(etat);
    devisDto.setDateDebutTravaux(dateDebutTravaux);
    devisDto.setVisibiliteAmenagement(visibilite);
    devisDto.setPhotoPrefere(photoPref);
    return devisDto.clone();
  }

  // on recupere tout les devis d un client pour les ajouter dans une arraylist
  @Override
  public ArrayList<HashMap<String, String>> getDevis(int idClient) {
    ArrayList<HashMap<String, String>> listeDevisClient = new ArrayList<HashMap<String, String>>();

    PreparedStatement ps = dalS.getPreparedStatement("SELECT DISTINCT c.prenom , \r\n"
        + "(SELECT string_agg(ta.nom , ', ')as type_amenagement \r\n"
        + "FROM types_amenagements ta , clients c1,\r\n" + "devis d1 ,amenagement_par_devis ad\r\n"
        + "WHERE d1.id_devis = d.id_devis AND d1.id_client = c1.id_client\r\n"
        + "AND d1.id_devis = ad.id_devis AND ad.id_amenagement = ta.id_amenagement)\r\n" + ",\r\n"
        + "d.date_devis,d.id_devis, d.duree_travaux , d.montant_total , "
        + "d.etat_des_amenagements\r\n" + ", (SELECT  COUNT(p.id_photo)  FROM photos p\r\n"
        + "WHERE p.id_devis = d.id_devis AND (p.date_photo <= d.date_debut_des_travaux "
        + "OR d.date_debut_des_travaux IS NULL))\r\n" + "as photos_avant,\r\n"
        + "(SELECT  COUNT(p.id_photo)FROM photos p\r\n"
        + "WHERE p.id_devis = d.id_devis AND p.date_photo > d.date_debut_des_travaux)\r\n"
        + "as photos_apres\r\n" + ",  (SELECT COUNT(p.id_photo)  FROM photos p\r\n"
        + "WHERE p.id_devis = d.id_devis AND p.visibilite = TRUE) \r\n" + "as photo_visible,\r\n"
        + "(SELECT p1.url_photo FROM photos p1\r\n"
        + "WHERE p1.id_devis=d.id_devis and p1.id_photo=d.photo_prefere)"
        + "AS photo_prefere,d.date_debut_des_travaux\r\n"
        + "FROM clients c , devis d WHERE d.id_client =? AND c.id_client=? GROUP BY 1,2,3,4;");

    try {
      ps.setInt(1, idClient);
      ps.setInt(2, idClient);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      while (res.next()) {
        HashMap<String, String> devisClient = new HashMap<String, String>();

        /* ajout des strings des realisations dans la list */
        devisClient.put("prenom", res.getString(1));
        devisClient.put("typeAmenagement", res.getString(2));
        devisClient.put("dateDevis", res.getDate(3).toString());
        devisClient.put("idDevis", String.valueOf(res.getInt(4)));
        devisClient.put("dureeTravaux", String.valueOf(res.getInt(5)));
        devisClient.put("montantTotal", String.valueOf(res.getDouble(6)));
        devisClient.put("etatDesAmenagements", res.getString(7));
        devisClient.put("nombrePhotosAvant", String.valueOf(res.getInt(8)));
        devisClient.put("nombrePhotosApres", String.valueOf(res.getInt(9)));
        devisClient.put("nombrePhotosVisible", String.valueOf(res.getString(10)));
        devisClient.put("photoPrefere", res.getString(11));
        devisClient.put("dateDebutTravaux", String.valueOf(res.getDate(12)));

        listeDevisClient.add(devisClient);
      }

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    return listeDevisClient;
  }

  @Override
  public ArrayList<DevisDto> getDevis() {
    ArrayList<DevisDto> listeDevis = new ArrayList<DevisDto>();
    PreparedStatement ps = dalS.getPreparedStatement("SELECT * FROM devis");
    try (ResultSet res = ps.executeQuery()) {
      while (res.next()) {
        LocalDate date = (res.getDate(7) != null) ? res.getDate(7).toLocalDate() : null;
        listeDevis.add(setDevisDto(res.getInt(1), res.getInt(2), res.getDate(3).toLocalDate(),
            res.getDouble(4), res.getInt(5), EtatAmenagements.valueOf(res.getString(6)), date,
            res.getBoolean(8)));
      }
      if (listeDevis.size() == 0) {
        return null;
      }
      return listeDevis;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }


  @Override
  public ArrayList<HashMap<String, String>> getDevisRealisation(
      Map<String, String> critereRecherche) {

    ArrayList<HashMap<String, String>> listeDevisRealisation =
        new ArrayList<HashMap<String, String>>();

    ArrayList<Object> tabCritereRecherche = new ArrayList<Object>();
    PreparedStatement ps = null;
    String query = "SELECT * FROM (SELECT DISTINCT c.prenom , (SELECT string_agg(ta.nom , ', ') "
        + "as type_amenagement FROM types_amenagements ta , clients c1,\r\n"
        + "devis d1 ,amenagement_par_devis ad\r\n"
        + "WHERE d1.id_devis = d.id_devis AND d1.id_client = c1.id_client\r\n"
        + "AND d1.id_devis = ad.id_devis AND ad.id_amenagement = ta.id_amenagement),\r\n"
        + "d.date_devis,d.id_devis, d.duree_travaux , d.montant_total , d.etat_des_amenagements\r\n"
        + ",(SELECT  COUNT(p.id_photo) FROM photos p\r\n" + "WHERE p.id_devis = d.id_devis AND "
        + "(p.date_photo <= d.date_debut_des_travaux OR d.date_debut_des_travaux IS NULL))"
        + "as photos_avant,\r\n" + "(SELECT  COUNT(p.id_photo) FROM photos p\r\n"
        + "WHERE p.id_devis = d.id_devis AND p.date_photo > d.date_debut_des_travaux)"
        + "as photos_apres,\r\n" + "(SELECT COUNT(p.id_photo) FROM photos p\r\n"
        + "WHERE p.id_devis = d.id_devis AND p.visibilite = TRUE)as photo_visible,\r\n"
        + "(SELECT p1.url_photo FROM photos p1\r\n"
        + "WHERE p1.id_devis=d.id_devis and p1.id_photo=d.photo_prefere) "
        + "AS photo_prefere,d.date_debut_des_travaux,\r\n"
        + "(SELECT string_agg(CAST(ta.id_amenagement as varchar(10)),',' "
        + "ORDER BY ta.id_amenagement) " + " as id_amenagement\r\n"
        + "FROM types_amenagements ta , clients c1, devis d1 ,amenagement_par_devis ad\r\n"
        + "WHERE d1.id_devis = d.id_devis AND d1.id_client = c1.id_client\r\n"
        + "AND d1.id_devis = ad.id_devis AND ad.id_amenagement = ta.id_amenagement ),"
        + "(SELECT c1.nom  as nom\r\n" + "FROM clients c1, devis d1 \r\n"
        + "WHERE d1.id_devis = d.id_devis AND d1.id_client = c1.id_client)\r\n"
        + "FROM clients c , devis d WHERE d.id_client = c.id_client GROUP BY 1,2,3,4) AS requete";

    String nomClientDevis = critereRecherche.get("nomClientDevis");
    String montantTotal1 = critereRecherche.get("montantTotal1");
    String montantTotal2 = critereRecherche.get("montantTotal2");
    String date = critereRecherche.get("date");
    String listeAmenagement = critereRecherche.get("listeAmenagement");
    /*
     * ON trie la liste d id d amenagement recu pour que celle ci convienne a la selection pour la
     * db
     */


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
    System.out.println("TailleDICTIONNAIRE: " + tailleDictionnaire);
    if (tailleDictionnaire == 1) {
      ps = dalS.getPreparedStatement(query);
    } else {
      /* probleme a gerer avec le where on va utiliser la list */
      if (!nomClientDevis.equals("")) {
        tabCritereRecherche.add(nomClientDevis);
        query += " WHERE nom=? ";
      }
      if (!montantTotal1.equals("")) {

        tabCritereRecherche.add(Double.parseDouble(montantTotal1));
        if (tabCritereRecherche.size() == 1) {
          query += " WHERE ? < montant_total ";
        } else {
          query += "AND ? < montant_total ";
        }

      }
      if (!montantTotal2.equals("")) {
        tabCritereRecherche.add(Double.parseDouble(montantTotal2));
        if (tabCritereRecherche.size() == 1) {
          query += " WHERE montant_total < ? ";
        } else {
          query += "AND montant_total < ? ";
        }

      }
      if (!date.equals("")) {
        tabCritereRecherche.add(Date.valueOf(date));
        if (tabCritereRecherche.size() == 1) {
          query += " WHERE date_devis=? ";
        } else {
          query += "AND date_devis=? ";
        }

      }
      if (!listeAmenagement.equals("")) {

        String[] listAmenagement = listeAmenagement.split(",");
        String listAmenagementTrie = "";
        ArrayList<Integer> listAmenagementEntier = new ArrayList<Integer>();
        for (int i = 0; i < listAmenagement.length; i++) {
          listAmenagementEntier.add(Integer.valueOf(listAmenagement[i]));

        }
        Collections.sort(listAmenagementEntier);
        for (int i = 0; i < listAmenagement.length; i++) {
          listAmenagementTrie += listAmenagementEntier.get(i) + ",";

        }

        listAmenagementTrie = listAmenagementTrie.substring(0, listAmenagementTrie.length() - 1);
        listeAmenagement = listAmenagementTrie;
        tabCritereRecherche.add(listeAmenagement);
        if (tabCritereRecherche.size() == 1) {
          query += " WHERE id_amenagement=? ";
        } else {
          query += "AND id_amenagement=? ";
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

        HashMap<String, String> devisRealisation = new HashMap<String, String>();

        /* ajout des strings des realisations dans la list */
        devisRealisation.put("prenom", res.getString(1));
        devisRealisation.put("typeAmenagement", res.getString(2));
        devisRealisation.put("dateDevis", res.getDate(3).toString());
        devisRealisation.put("idDevis", String.valueOf(res.getInt(4)));
        devisRealisation.put("dureeTravaux", String.valueOf(res.getInt(5)));
        devisRealisation.put("montantTotal", String.valueOf(res.getDouble(6)));
        devisRealisation.put("etatDesAmenagements", res.getString(7));
        devisRealisation.put("nombrePhotosAvant", String.valueOf(res.getInt(8)));
        devisRealisation.put("nombrePhotosApres", String.valueOf(res.getInt(9)));
        devisRealisation.put("nombrePhotosVisible", String.valueOf(res.getString(10)));
        devisRealisation.put("photoPrefere", res.getString(11));
        devisRealisation.put("dateDebutTravaux", String.valueOf(res.getDate(12)));
        listeDevisRealisation.add(devisRealisation);
      }
      if (listeDevisRealisation.size() == 0) {
        return null;
      }
      return listeDevisRealisation;
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public boolean deleteDevis(int idClient) {
    // TODO Auto-generated method stub
    return false;
  }


  @Override
  public boolean updateDevis(DevisDto utilisateur) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void setEtatDevis(int idDevis, EtatAmenagements etat) {
    PreparedStatement ps =
        dalS.getPreparedStatement("UPDATE devis SET etat_des_amenagements=? WHERE id_devis= ?");
    try {
      ps.setString(1, etat.get_name());
      ps.setInt(2, idDevis);
      ps.executeUpdate();
    } catch (SQLException sqlException) {
      throw new FatalException(sqlException);
    }
  }

  /**
   * Introduit un nouveau devis dans la Db.
   */
  public DevisDto introduireDevis(DevisDto devisDto) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "INSERT INTO devis" + "(id_client,date_devis,montant_total,duree_travaux,"
            + "etat_des_amenagements,visibilite_amenagement)"
            + "VALUES(?,?,?,?,'DI',FALSE) RETURNING id_devis;");
    try {
      ps.setInt(1, devisDto.getIdClient());
      ps.setDate(2, Date.valueOf(devisDto.getDateDevis()));
      ps.setDouble(3, devisDto.getMontantTotal());
      ps.setInt(4, devisDto.getDureeTravaux());
      ResultSet res = ps.executeQuery();
      if (res.next()) {
        devisDto.setIdDevis(res.getInt(1));
        return devisDto;
      }
      return null;
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public boolean introduireListeAmenagement(DevisDto devis, int[] listeAmenagement) {
    PreparedStatement ps =
        dalS.getPreparedStatement("INSERT INTO amenagement_par_devis(id_devis , id_amenagement)"
            + " VALUES(?,?) RETURNING id_devis");
    try {
      ps.setInt(1, devis.getIdDevis());
      for (int i = 0; i < listeAmenagement.length; i++) {
        int idAmenagement = listeAmenagement[i];
        ps.setInt(2, idAmenagement);
        ResultSet res = ps.executeQuery();

        if (!res.next()) {
          return false;
        }
      }
      return true;

    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public boolean setDateDebutTravaux(LocalDate date, int idDevis) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "UPDATE devis SET date_debut_des_travaux=? WHERE devis.id_devis=? RETURNING id_devis");
    try {
      ps.setDate(1, Date.valueOf(date));
      ps.setInt(2, idDevis);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      return res.next();
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }

  @Override
  public void rendreVisible(int idDevis) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "UPDATE devis SET etat_des_amenagements=?, visibilite_amenagement=true WHERE id_devis= ?");
    try {
      ps.setString(1, "RV");
      ps.setInt(2, idDevis);
      ps.executeUpdate();
    } catch (SQLException sqlException) {
      throw new FatalException(sqlException);
    }
  }

  @Override
  public void setPhotoPreferee(int idPhoto, int idDevis) {
    PreparedStatement ps =
        dalS.getPreparedStatement("UPDATE devis SET photo_prefere=? WHERE id_devis= ?");
    try {
      ps.setInt(1, idPhoto);
      ps.setInt(2, idDevis);
      ps.executeUpdate();
    } catch (SQLException sqlException) {
      throw new FatalException(sqlException);
    }
  }



  @Override
  public HashMap<String, String> getVisualisationDevis(int idDevis) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "SELECT c.*,d.*, string_agg(ta.nom , ', ') FROM devis d, clients c, \r\n"
            + "amenagement_par_devis ad, types_amenagements ta"
            + " WHERE c.id_client = d.id_client AND d.id_devis = ? \r\n"
            + "AND ta.id_amenagement = ad.id_amenagement AND ad.id_devis = d.id_devis"
            + " GROUP BY d.id_devis, c.id_client");
    try {
      ps.setInt(1, idDevis);
      System.out.println(ps.toString());
    } catch (Exception sqlException) {
      throw new FatalException(sqlException);
    }

    try (ResultSet res = ps.executeQuery()) {
      if (res.next()) {
        HashMap<String, String> visualisationDevis = new HashMap<String, String>();
        visualisationDevis.put("id_client", String.valueOf(res.getInt(1)));
        visualisationDevis.put("nom", res.getString(2));
        visualisationDevis.put("prenom", res.getString(3));
        visualisationDevis.put("ville", res.getString(4));
        visualisationDevis.put("code_postal", String.valueOf(res.getInt(5)));
        visualisationDevis.put("rue", res.getString(6));
        visualisationDevis.put("numero", String.valueOf(res.getInt(7)));
        visualisationDevis.put("boite", res.getString(8));
        visualisationDevis.put("telephone", res.getString(9));
        visualisationDevis.put("email", res.getString(10));
        visualisationDevis.put("id_devis", String.valueOf(res.getInt(11)));
        visualisationDevis.put("date_devis", String.valueOf(res.getDate(13)));
        visualisationDevis.put("montant_total", String.valueOf(res.getDouble(14)));
        visualisationDevis.put("duree_travaux", String.valueOf(res.getInt(15)));
        visualisationDevis.put("etat_des_amenagements", res.getString(16));
        visualisationDevis.put("date_debut_travaux", res.getString(17));
        visualisationDevis.put("type_amenagement", res.getString(20));
        visualisationDevis.put("photo_preferee", res.getString(19));
        return visualisationDevis;
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return null;
  }

  @Override
  public DevisDto getDevisById(int idDevis) {
    PreparedStatement ps =
        dalS.getPreparedStatement("SELECT d.* FROM devis d WHERE d.id_devis = ?");
    DevisDto devisDto = null;

    try {
      ps.setInt(1, idDevis);
    } catch (SQLException exc) {
      throw new FatalException(exc);
    }

    try (ResultSet res = ps.executeQuery()) {

      if (res.next()) {
        LocalDate date = (res.getDate(7) != null) ? res.getDate(7).toLocalDate() : null;
        devisDto = setDevisDto(res.getInt(1), res.getInt(2), res.getDate(3).toLocalDate(),
            res.getDouble(4), res.getInt(5), EtatAmenagements.valueOf(res.getString(6)), date,
            res.getBoolean(8), res.getInt(9));
      }
    } catch (SQLException exc) {
      throw new FatalException(exc);
    }
    return devisDto;
  }


}
