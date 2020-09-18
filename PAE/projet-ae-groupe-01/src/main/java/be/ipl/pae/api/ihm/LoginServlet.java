package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.biz.utilisateur.UtilisateurUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.CompteNonActiveeException;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.JWTCreator.Builder;
import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet {

  @Inject
  private UtilisateurUcc utilisateurUcc;

  public LoginServlet() {}


  @SuppressWarnings("unchecked")
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    try {

      System.out.println("Appel au servlet Login");
      Genson genson = new Genson();
      Map<String, String> map = genson.deserialize(req.getReader(), HashMap.class);
      System.out.println(map.get("pseudo") + " souhaite se connecter");

      UtilisateurDto utilisateur = null;

      utilisateur = utilisateurUcc.seConnecter(map.get("pseudo"), map.get("mot_de_passe"));

      // Verification qu il existe un utilisateur
      if (utilisateur != null) {
        System.out.println(utilisateur.getPseudo());

        Builder tokenBuilder = Utils.createToken();
        // Rajoute dans le token l id de l utilisateur
        tokenBuilder.withClaim("id", utilisateur.getId_utilisateur());

        String token = tokenBuilder.sign(Utils.algorithm);
        System.out.println("token generer :" + token);

        utilisateur.setMot_de_passe(""); // ne pas envoyer le mot de passe
        Map<String, Object> responseData = new HashMap<String, Object>();
        responseData.put("token", token);
        responseData.put("user", utilisateur);
        Map<String, Object> response = new HashMap<String, Object>();
        response.put("success", "true");
        response.put("data", responseData);

        Utils.replyAsSuccess(resp, genson.serialize(response).getBytes(StandardCharsets.UTF_8),
            "login success");

      } else {
        Utils.replyAsError(resp, HttpServletResponse.SC_UNAUTHORIZED,
            "Pas d utilisateur correspondant", "Pas d utilisateur correspondant");
      }

    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    } catch (CompteNonActiveeException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, "Compte non activé",
          "Compte non activé");
    }
  }
}
