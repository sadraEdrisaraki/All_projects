package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.devis.DevisUcc;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.BizException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Genson;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DateTravauxDevisServlet extends HttpServlet {
  @Inject
  private DevisUcc devisUcc;


  public DateTravauxDevisServlet() {}


  /**
   * Set date devis.
   */
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    try {

      DecodedJWT jwt;

      String token = req.getHeader("authorization");
      jwt = Utils.verifyToken(token);
      if (jwt == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
      }


      Genson genson = new Genson();
      System.out.println(req.getReader().toString());
      HashMap<String, Object> map = genson.deserialize(req.getReader(), HashMap.class);

      LocalDate date = null;
      date = recupererDateDevis(map);

      String idDevis = map.get("id_devis").toString();
      devisUcc.setDateDebutTravaux(date, Integer.valueOf(idDevis));

      Map<String, String> rep = new HashMap<String, String>() {
        {
          put("success", "true");
          put("message", "modification date travaux devis reussi");
        }
      };

      Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
          "modification date travaux devis reussi");

    } catch (BizException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur biz",
          exception.getMessage());
    } catch (DateTimeException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
          "La date n a pas pu etre parse", "erreur - parsing data");
    }
  }

  private LocalDate recupererDateDevis(Map<String, Object> parametresRequete) {
    String dateDevis = parametresRequete.get("date_devis").toString();
    LocalDate date = LocalDate.parse(dateDevis);
    return date;
  }

}
