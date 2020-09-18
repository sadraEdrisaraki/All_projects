package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.client.ClientUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClientsSansUtilisateurServlet extends HttpServlet {

  @Inject
  private ClientUcc clientUcc;

  public ClientsSansUtilisateurServlet() {}

  /**
   * Obtenir une liste de client lier a aucun utilisateur.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    try {
      // Verifie l authorisation du client
      String token = req.getHeader("authorization");
      if (Utils.verifyToken(token) == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return; // stop la requete si token pas valable
      }

      ArrayList<ClientDto> listeClient = clientUcc.getClientsSansUtilisateurs();
      Genson genson = new Genson();
      String json = genson.serialize(listeClient);
      Utils.replyAsSuccess(resp, json.getBytes(StandardCharsets.UTF_8), json);

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }
  }


}
