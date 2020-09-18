package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.devis.DevisUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class VisualisationDevisServlet extends HttpServlet {

  @Inject
  private DevisUcc devisUcc;

  public VisualisationDevisServlet() {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    System.out.println("Appel au servlet devis visualisation ");
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
      int idDevis = Integer.valueOf(req.getHeader("parameter"));
      HashMap<String, String> visualisationDevis = new HashMap<String, String>();
      System.out.println("Id devis " + idDevis);
      visualisationDevis = devisUcc.getVisualisationDevis(idDevis);

      Utils.replyAsSuccess(resp,
          genson.serialize(visualisationDevis).getBytes(StandardCharsets.UTF_8),
          "Visualisation devis envoyée");

    } catch (FatalException exception) {
      exception.printStackTrace();
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }

  }

}
