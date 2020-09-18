package be.ipl.pae.api.ihm;



import be.ipl.pae.biz.utilisateur.UtilisateurUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;

import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action particuliere d'utilistateur.
 *
 */
@SuppressWarnings("serial")
public class LierUtilisateurClientServlet extends HttpServlet {

  @Inject
  private UtilisateurUcc utilisateurUcc;

  public LierUtilisateurClientServlet() {}

  @SuppressWarnings({"unchecked"})
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    System.out.println("Appel au servlet LierUtilisateurClient");

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
      String emailClient = parametresRequete.get("email_client").toString();
      String emailUtilisateur = parametresRequete.get("email_utilisateur").toString();
      utilisateurUcc.lierCompteClientUtilisateur(emailClient, emailUtilisateur);

      Map<String, String> rep = new HashMap<String, String>() {
        {
          put("success", "true");
          put("message", "liaison reussi");
        }
      };
      Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
          "liaison réussi");
    } catch (BizException exc) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, exc.getMessage(), exc.getMessage());
      return;
    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }
  }


}
