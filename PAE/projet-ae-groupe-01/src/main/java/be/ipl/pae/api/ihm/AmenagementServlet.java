package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.amenagement.TypeAmenagementDto;
import be.ipl.pae.biz.amenagement.TypeAmenagementUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AmenagementServlet extends HttpServlet {

  @Inject
  private TypeAmenagementUcc amenagementUcc;

  public AmenagementServlet() {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    try {
      System.out.println("Appel au servlet ListeAmenagement");

      Genson genson = new Genson();
      ArrayList<TypeAmenagementDto> listeAmenagement = amenagementUcc.getListeAmenagement();
      if (listeAmenagement == null) {
        String msg = "erreur lors de la recuperation de la liste d'amenagement dans le servlet";
        Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg, msg);
      } else {
        Utils.replyAsSuccess(resp,
            genson.serialize(listeAmenagement).getBytes(StandardCharsets.UTF_8),
            "envoie liste aménagment ok");
      }

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }

  }

  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    try {
      System.out.println("Appel au servlet AjoutAmenagementServlet");
      String token = req.getHeader("authorization");

      if (Utils.verifyToken(token) == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
        // stop la requete si token pas valable
      }
      Genson genson = new Genson();
      Map<String, Object> parametresRequete = genson.deserialize(req.getReader(), HashMap.class);
      // System.out.println(parametresRequete);
      String nouvelleAmenagement = parametresRequete.get("nom").toString();

      Map<String, String> rep = new HashMap<String, String>() {
        {
          put("success", "true");
          put("message", "insertion amenagement reussi");
        }
      };

      if (amenagementUcc.introduireAmenagement(nouvelleAmenagement)) {
        Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
            "ajout reussi");
      } else {
        Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, "Ajout amenagement impossible",
            "Ajout amenagement impossible");
      }

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }

  }

}
