package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.biz.utilisateur.UtilisateurUcc;
import be.ipl.pae.config.ConverteurJson;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.VisibilityFilter;

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
public class ConfirmationUtilisateurServlet extends HttpServlet {

  @Inject
  private UtilisateurUcc utilisateurUcc;
  @Inject
  private BizFactory bizFactory;

  /**
   * Renvoie tous les utilisateurs non confirmé.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    try {
      // Verifie l authorisation du patron
      String token = req.getHeader("authorization");
      if (Utils.verifyToken(token) == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
        // stop la requete si token pas valable
      }

      ArrayList<UtilisateurDto> listeUtilisateur = utilisateurUcc.getUtilisateurNonConfirme();

      Genson genson = new GensonBuilder().setSkipNull(true).useMethods(false)
          .useFields(true, VisibilityFilter.PRIVATE)
          .withConverter(ConverteurJson.getConverteurLocalDateJson(), LocalDate.class).create();

      String json = genson.serialize(listeUtilisateur);
      Utils.replyAsSuccess(resp, json.getBytes(StandardCharsets.UTF_8), json);

    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    }
  }

  /**
   * Confirme l'inscription d'un utilisateur.
   */
  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    try {
      // Verifie l authorisation du patron
      String token = req.getHeader("authorization");
      if (Utils.verifyToken(token) == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return; // stop la requete si token pas valable
      }

      Genson genson = new Genson();
      Map<String, String> parametresRequete = genson.deserialize(req.getReader(), HashMap.class);

      // Pas de parametre
      if (!parametresRequete.containsKey("id_utilisateur")) {
        Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, "donnee manquante",
            "erreur post");
        return;
      }

      UtilisateurDto utilisateur = bizFactory.getUtilisateur();
      utilisateur.setId_utilisateur(Integer.parseInt(parametresRequete.get("id_utilisateur")));

      Map<String, String> rep = new HashMap<String, String>() {
        {
          put("success", "true");
          put("message", "Confirmation reussi");
        }
      };

      if (utilisateurUcc.confirmerInscription(utilisateur)) {
        Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
            "confirmation reussi");
      } else {
        // Quand l'id ne correspond pas avec un id de la db; donc aucun confirmation n a ete
        // faite->cas rare
        Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, "Confirmation impossible",
            "Confimation impossible");
      }

    } catch (NumberFormatException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, "le parametre n est pas un entier",
          "erreur parametre string");
    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }
  }
}
