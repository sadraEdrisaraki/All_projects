package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.devis.DevisUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DevisRealisationServlet extends HttpServlet {

  @Inject
  private DevisUcc devisUcc;

  public DevisRealisationServlet() {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    System.out.println("Appel au servlet DevisRealisation");
    DecodedJWT jwt;
    try {

      // Verification token
      String token = req.getHeader("authorization");
      jwt = Utils.verifyToken(token);
      if (jwt == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
      }

      Genson genson = new Genson();
      HashMap<String, String> rechercheVide = new HashMap<String, String>();
      rechercheVide.put("Bidon", "Bidon");
      ArrayList<HashMap<String, String>> listeAmenagement =
          devisUcc.getDevisRealisation(rechercheVide);

      if (listeAmenagement == null) {
        String msg = "erreur lors de la recuperation de la liste d'amenagement dans le servlet";
        Utils.replyAsError(resp, HttpServletResponse.SC_PRECONDITION_FAILED, msg, msg);

      } else {
        Utils.replyAsSuccess(resp,
            genson.serialize(listeAmenagement).getBytes(StandardCharsets.UTF_8),
            "success liste amengament");
      }

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }

  }

}
