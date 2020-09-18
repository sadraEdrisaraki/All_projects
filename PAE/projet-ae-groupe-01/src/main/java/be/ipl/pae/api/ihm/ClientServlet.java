package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.client.ClientUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;

import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClientServlet extends HttpServlet {

  @Inject
  private ClientUcc clientUcc;
  @Inject
  private BizFactory bizFactory;

  public ClientServlet() {}


  /**
   * Introduction client.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    try {
      System.out.println("Appel au servlet Introduction client");

      // Verification token
      String token = req.getHeader("authorization");
      if (Utils.verifyToken(token) == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return; // stop la requete si token pas valable
      }

      Genson genson = new Genson();
      Map<String, String> parametresRequete = genson.deserialize(req.getReader(), HashMap.class);

      // Verification parametres cote API
      String verification = checkParametres(parametresRequete);

      if (verification != null) {
        Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, verification,
            "erreur verification");
        return;
      }

      ClientDto client = bizFactory.getClient();

      client.setNom(parametresRequete.get("nom_client"));
      client.setPrenom(parametresRequete.get("prenom_client"));
      client.setRue(parametresRequete.get("rue"));
      client.setNumero(Integer.parseInt(parametresRequete.get("numero")));
      client.setCodePostal(Integer.parseInt(parametresRequete.get("code_postal")));
      client.setBoite(parametresRequete.get("boite"));
      client.setVille(parametresRequete.get("ville"));
      client.setEmail(parametresRequete.get("email"));
      client.setTelephone(parametresRequete.get("telephone"));

      System.out.println("boite = " + client.getBoite());

      System.out.println("donnees du client voulant etre inscrit : " + client.toString());

      clientUcc.inscrire(client);

      // Post inscription
      Utils.replyAsSuccess(resp, genson.serialize(client).getBytes(StandardCharsets.UTF_8),
          "inscription client réussi");

    } catch (BizException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, exception.getMessage(),
          "erreur Biz");
    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }
  }

  /**
   * Obtenir une listes des clients.
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

      ArrayList<ClientDto> listeClient = clientUcc.getClients();
      Genson genson = new Genson();
      String json = genson.serialize(listeClient);
      Utils.replyAsSuccess(resp, json.getBytes(StandardCharsets.UTF_8), json);

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }
  }

  /**
   * Methode qui verifie les parametres recu par le client. Les parametres doivent etre present et
   * non vide. Mais ne verifie pas le contenu.
   * 
   * @param parametresRequete les parametres de la requete
   * @return message d erreur si un parametre manque ou bien null dans le cas contraire
   */
  private String checkParametres(Map<String, String> parametresRequete) {

    // nom
    if (!parametresRequete.containsKey("nom_client")) {
      return "nom manquant";
    }
    if (parametresRequete.get("nom_client").length() == 0) {
      return "nom vide";
    }

    // prenom
    if (!parametresRequete.containsKey("prenom_client")) {
      return "prenom manquant";
    }
    if (parametresRequete.get("prenom_client").length() == 0) {
      return "prenom vide";
    }

    // rue
    if (!parametresRequete.containsKey("rue")) {
      return "rue manquante";
    }
    if (parametresRequete.get("rue").length() == 0) {
      return "rue vide";
    }

    // numero
    if (!parametresRequete.containsKey("numero")) {
      return "numero manquant";
    }
    if (parametresRequete.get("numero").length() == 0) {
      return "numero vide";
    }
    if (!parametresRequete.get("numero").matches("[0-9]+")) {
      return "Le numero doit contenir que des nombres";
    }

    // code_postal
    if (!parametresRequete.containsKey("code_postal")) {
      return "code postal manquant";
    }
    if (parametresRequete.get("code_postal").length() == 0) {
      return "code postal vide";
    }
    if (!parametresRequete.get("code_postal").matches("[0-9]+")) {
      return "code postal doit contenir que des nombres";
    }

    // boite
    if (!parametresRequete.containsKey("boite")) {
      return "boite manquant";
    }

    // ville
    if (!parametresRequete.containsKey("ville")) {
      return "ville manquant";
    }
    if (parametresRequete.get("ville").length() == 0) {
      return "ville vide";
    }

    // email
    if (!parametresRequete.containsKey("email")) {
      return "email manquant";
    }
    if (parametresRequete.get("email").length() == 0) {
      return "email vide";
    }

    // telephone
    if (!parametresRequete.containsKey("telephone")) {
      return "telephone manquant";
    }
    if (parametresRequete.get("telephone").length() == 0) {
      return "telephone vide";
    }

    return null;
  }


}
