package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.client.ClientUcc;
import be.ipl.pae.biz.devis.DevisUcc;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.biz.utilisateur.UtilisateurUcc;
import be.ipl.pae.config.ConverteurJson;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.reflect.VisibilityFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RechercheServlet extends HttpServlet {

  @Inject
  private UtilisateurUcc utilisateurUcc;
  @Inject
  private ClientUcc clientUcc;
  @Inject
  private DevisUcc devisUcc;

  /**
   * Construit la servlet de recherche d'utilisateur.
   */
  public RechercheServlet() {}

  @SuppressWarnings("unchecked")
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    try {

      System.out.println("\nAppel au servlet Recherche\n");

      Genson genson = new GensonBuilder().setSkipNull(true).useMethods(false)
          .useFields(true, VisibilityFilter.PRIVATE)
          .withConverter(ConverteurJson.getConverteurLocalDateJson(), LocalDate.class).create();

      Map<String, String> map = genson.deserialize(req.getReader(), HashMap.class);
      String selectRechercheValue = map.get("selectRecherche");
      // Verification token

      String token = req.getHeader("authorization");
      DecodedJWT jwt = Utils.verifyToken(token);
      if (jwt == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
      }

      // switch case pour savoir quel recherche retourner
      switch (selectRechercheValue) {
        case "1":
          ArrayList<HashMap<String, String>> listeDevisArrayList =
              devisUcc.getDevisRealisation(map);
          Utils.replyAsSuccess(resp,
              genson.serialize(listeDevisArrayList).getBytes(StandardCharsets.UTF_8),
              "envoie recherche devis ok");
          break;
        case "2":

          ArrayList<UtilisateurDto> listeUtilisateur = utilisateurUcc.getUtilisateurs(map);

          Utils.replyAsSuccess(resp,
              genson.serialize(listeUtilisateur).getBytes(StandardCharsets.UTF_8),
              "envoie recherche utilisateur ok");
          break;

        case "3":
          ArrayList<ClientDto> listeClient = clientUcc.getClientsCritereRecherche(map);
          Utils.replyAsSuccess(resp, genson.serialize(listeClient).getBytes(StandardCharsets.UTF_8),
              "envoie recherche client ok");
          break;

        default:
          Utils.replyAsError(resp, HttpServletResponse.SC_BAD_REQUEST, "pas de recherche dispo",
              "pas de recherche dispo");
          break;
      }
    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    }

  }
}
