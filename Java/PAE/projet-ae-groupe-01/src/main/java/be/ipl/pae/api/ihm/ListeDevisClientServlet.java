package be.ipl.pae.api.ihm;


import be.ipl.pae.biz.devis.DevisUcc;
import be.ipl.pae.biz.utilisateur.UtilisateurUcc;
import be.ipl.pae.config.ConverteurJson;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action particuliere de devis.
 *
 */
@SuppressWarnings("serial")
public class ListeDevisClientServlet extends HttpServlet {

  @Inject
  private DevisUcc devisUcc;

  @Inject
  private UtilisateurUcc utilisateurUcc;

  public ListeDevisClientServlet() {}


  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    System.out.println("\nAppel au servlet ListeDevisClient - Mes devis");

    DecodedJWT jwt;
    try {
      // Verification token
      String token = req.getHeader("authorization");
      jwt = Utils.verifyToken(token);
      if (jwt == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
      }
      // on creer le converter pour serialis les dates

      Converter<LocalDate> converter = ConverteurJson.getConverteurLocalDateJson();

      Genson genson =
          new GensonBuilder().setSkipNull(false).withConverter(converter, LocalDate.class).create();

      Map<String, Claim> claims = jwt.getClaims();
      int idUtilisateur = claims.get("id").asInt();

      System.out.println(idUtilisateur);

      int idClient = utilisateurUcc.getUtilisateur(idUtilisateur).getId_client();

      ArrayList<HashMap<String, String>> listeDevisClient = devisUcc.getDevisClient(idClient);

      Utils.replyAsSuccess(resp,
          genson.serialize(listeDevisClient).getBytes(StandardCharsets.UTF_8),
          "envoie pour mes devis ok");
    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    }

  }
}
