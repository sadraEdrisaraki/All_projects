package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.biz.utilisateur.UtilisateurUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class VerificationServlet extends HttpServlet {

  @Inject
  private UtilisateurUcc utilisateurUcc;
  @Inject
  private BizFactory bizFactory;

  public VerificationServlet() {}

  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    try {
      System.out.println("Appel au servlet Verification");

      String token = req.getHeader("authorization");
      DecodedJWT jwt = Utils.verifyToken(token);
      if (jwt == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return; // stop la requete si token pas valable
      }

      Map<String, Claim> claims = jwt.getClaims();
      Claim idUtilisateur = claims.get("id");
      int id = idUtilisateur.asInt();

      UtilisateurDto utilisateurDtoTemp = bizFactory.getUtilisateur();
      utilisateurDtoTemp = utilisateurUcc.getUtilisateur(id);

      Map<String, Object> rep = new HashMap<String, Object>();
      rep.put("autorisation", utilisateurDtoTemp.getType().name());

      Genson genson = new Genson();
      Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
          "verification reussi, envoie des donnees");

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }
  }
}
