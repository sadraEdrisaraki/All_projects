package be.ipl.pae.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class Utils {

  private static final String JWTSECRET =
      InjectionService.getPropsData("server.properties", "JWTSECRET");
  private static final String EMETTEUR =
      InjectionService.getPropsData("server.properties", "EMETTEUR");
  public static final Algorithm algorithm = Algorithm.HMAC256(JWTSECRET);

  /**
   * Cree le token.
   * 
   * @return le builder du token
   */
  public static Builder createToken() {
    return JWT.create().withIssuer(EMETTEUR);
  }

  /**
   * Verifie le token donnee en parametre, Si le token founi n est pas bon une
   * JWTVerificationException sera attraper et on retournera null.
   * 
   * @param token Le token a verifier
   * @return un DecodeJWT dans le cas contraire null
   */
  public static DecodedJWT verifyToken(String token) {
    if (token == null) {
      return null;
    }
    try {
      JWTVerifier verifier = JWT.require(algorithm).withIssuer(EMETTEUR).build();

      return verifier.verify(token);
    } catch (JWTVerificationException exception) {
      return null;
    }
  }

  /**
   * Reponse quand une erreur c'est produite.
   */
  public static void replyAsError(HttpServletResponse resp, int errorCode, String respErrorMsg,
      String terminalErrorMsg) throws IOException {
    resp.setStatus(errorCode);
    resp.setCharacterEncoding("utf-8");
    resp.setContentType("application/json");
    String returnJson = "{\"success\":\"false\", \"message\":\"" + respErrorMsg + "\"}";
    resp.getWriter().write(returnJson);
    System.out.println(terminalErrorMsg);
  }

  /**
   * Reponse quand la requete c'est bien passe.
   */
  public static void replyAsSuccess(HttpServletResponse resp, byte[] reponse, String terminalMsg)
      throws IOException {
    System.out.println(terminalMsg);
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.setStatus(HttpServletResponse.SC_OK);
    resp.getOutputStream().write(reponse);
  }

  public static void replyWithWrongTokenError(HttpServletResponse resp, String token)
      throws IOException {
    replyAsError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Mauvais token", "Acces refuse");
  }

}
