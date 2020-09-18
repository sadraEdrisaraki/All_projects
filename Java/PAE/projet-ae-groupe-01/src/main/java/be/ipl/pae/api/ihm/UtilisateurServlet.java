package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.TypeUtilisateurEnum;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.biz.utilisateur.UtilisateurUcc;
import be.ipl.pae.config.ConverteurJson;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.VisibilityFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class UtilisateurServlet extends HttpServlet {

  @Inject
  private UtilisateurUcc utilisateurUcc;
  @Inject
  private BizFactory bizFactory;

  public UtilisateurServlet() {}

  /**
   * Inscription d'un utilisateur.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    System.out.println("appel au servlet inscription");

    // Recuperation des donnees de la requete
    StringBuffer stringBuffer = new StringBuffer();
    String line = null;
    try {
      BufferedReader reader = req.getReader();
      while ((line = reader.readLine()) != null) {
        stringBuffer.append(line);
      }
      System.out.println("POST DATA : " + stringBuffer.toString());
    } catch (Exception exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, "erreur buffer reader",
          "erreur parametre");
      return;
    }

    Genson genson = new Genson();
    Map<String, Object> map = genson.deserialize(stringBuffer.toString(), Map.class);

    // Verification cote serveur
    String messageErreurVerification = chercherDonneeManquanteOuIncorrecte(map);
    if (messageErreurVerification != null) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, messageErreurVerification,
          "erreur parametre");
      return;
    }

    UtilisateurDto utilisateurTemp = bizFactory.getUtilisateur();

    // Remplit le DTO
    utilisateurTemp.setPseudo(map.get("pseudo").toString());
    utilisateurTemp.setMot_de_passe(map.get("mot_de_passe").toString());
    utilisateurTemp.setNom(map.get("nom").toString());
    utilisateurTemp.setPrenom(map.get("prenom").toString());
    utilisateurTemp.setVille(map.get("ville").toString());
    utilisateurTemp.setEmail(map.get("email").toString());
    utilisateurTemp.setType(TypeUtilisateurEnum.C);
    utilisateurTemp.setConfirmationInscription(false);

    System.out.println(
        "Utilisateur souhaite s inscrire avec les donnees = " + utilisateurTemp.toString());

    try {
      utilisateurTemp = utilisateurUcc.inscrireUtilisateur(utilisateurTemp);

      String json = "success = true";

      Utils.replyAsSuccess(resp, genson.serialize(json).getBytes(StandardCharsets.UTF_8),
          "inscription reussi");

    } catch (BizException bizException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, bizException.getMessage(),
          "erreur Biz");
    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    }
  }

  /**
   * Définir ouvrier.
   */
  @SuppressWarnings("unchecked")
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    System.out.println("appel au servlet DefinirOuvrierServlet");


    // Verifie l authorisation du client
    String token = req.getHeader("authorization");
    if (Utils.verifyToken(token) == null) {
      Utils.replyWithWrongTokenError(resp, token);
      return;
      // stop la requete si token pas valable
    }

    try {
      Genson genson = new Genson();
      Map<String, Object> parametresRequete = genson.deserialize(req.getReader(), HashMap.class);
      String emailUtilisateur = parametresRequete.get("email_utilisateur").toString();
      utilisateurUcc.definirCompteOuvrier(emailUtilisateur);

      Map<String, String> rep = new HashMap<String, String>() {
        {
          put("success", "true");
          put("message", "modification reussi");
        }
      };

      Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
          "modification réussi");
    } catch (BizException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, exception.getMessage(),
          exception.getMessage());

    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    }

  }

  /**
   * Obtenir la liste des utilisateurs qui sont lier à aucun client.
   */
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      // Verifie l authorisation du client
      String token = req.getHeader("authorization");
      if (Utils.verifyToken(token) == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
        // stop la requete si token pas valable
      }

      ArrayList<UtilisateurDto> listeUtilisateur = utilisateurUcc.getUtilisateursSansClient();
      Genson genson =
          new GensonBuilder().useMethods(false).useFields(true, VisibilityFilter.PRIVATE)
              .withConverter(ConverteurJson.getConverteurLocalDateJson(), LocalDate.class).create();

      String json = genson.serialize(listeUtilisateur);
      Utils.replyAsSuccess(resp, json.getBytes(StandardCharsets.UTF_8), json);

    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    }
  }

  private String chercherDonneeManquanteOuIncorrecte(Map<String, Object> map) {

    // pseudo
    if (!map.containsKey("pseudo")) {
      return "pseudo manquant";
    }
    if (map.get("pseudo").toString().length() == 0) {
      return "pseudo vide";
    }

    // mot de passe
    if (!map.containsKey("mot_de_passe")) {
      return "mot de passe manquant";
    }
    if (map.get("mot_de_passe").toString().length() == 0) {
      return "mot de passe vide";
    }

    // nom
    if (!map.containsKey("nom")) {
      return "nom manquant";
    }
    if (map.get("mot_de_passe").toString().length() == 0) {
      return "nom vide";
    }

    // prenom
    if (!map.containsKey("prenom")) {
      return "prenom manquant";
    }
    if (map.get("prenom").toString().length() == 0) {
      return "prenom vide";
    }

    // ville
    if (!map.containsKey("ville")) {
      return "ville manquant";
    }
    if (map.get("ville").toString().length() == 0) {
      return "ville vide";
    }

    // email
    if (!map.containsKey("email")) {
      return "email manquant";
    }
    if (map.get("email").toString().length() == 0) {
      return "email vide";
    }

    return null;
  }

}
