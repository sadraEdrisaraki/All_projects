package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.devis.DevisUcc;
import be.ipl.pae.biz.photo.PhotoDto;
import be.ipl.pae.biz.photo.PhotoUcc;
import be.ipl.pae.config.ConverteurJson;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class SendImageServlet extends HttpServlet {

  @Inject
  private PhotoUcc photoUcc;

  @Inject
  private DevisUcc devisUcc;

  @Inject
  private BizFactory bizFactory;

  public SendImageServlet() {}


  /**
   * Inserer une image.
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    DecodedJWT jwt;
    System.out.println("Appel servlet ajout photo");
    String token = req.getHeader("authorization");
    jwt = Utils.verifyToken(token);
    if (jwt == null) {
      Utils.replyWithWrongTokenError(resp, token);
      return;
    }


    Genson genson = new Genson();
    HashMap<String, Object> map = genson.deserialize(req.getReader(), HashMap.class);
    System.out.println(req.getReader());

    String image = map.get("image").toString();
    LocalDate date = recupererDateDevis(map);

    PhotoDto photoDto = bizFactory.getPhoto();

    System.out.println("DATE : " + date);
    photoDto.setPhoto(image);
    photoDto.setDate_photo(date);

    String stringidDevis = map.get("id_devis").toString();
    photoDto.setId_devis(Integer.valueOf(stringidDevis));

    String stringidTypeAmenagement = map.get("id_amenagement").toString();
    photoDto.setId_amenagement(Integer.valueOf(stringidTypeAmenagement));

    Object photoPreferee = map.get("preferee");
    boolean isPref = false;
    if (photoPreferee != null) {
      isPref = Boolean.valueOf(photoPreferee.toString());
    }

    Object visible = map.get("visible");
    boolean isVisible = false;
    if (visible != null) {
      isVisible = Boolean.valueOf(visible.toString());
    }
    if (isVisible) {
      photoDto.setVisible(true);
    }

    try {

      photoDto = photoUcc.enregistrerPhoto(photoDto);
      if (isPref) {
        devisUcc.setPhotoPreferee(photoDto.getId_photo(), photoDto.getId_devis());
      }
      Map<String, String> rep = new HashMap<String, String>() {
        {
          put("success", "true");
          put("message", "ajout photo reussi");
        }
      };

      Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
          "ajout photo ok");
    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    }
  }

  /*
   * Renvoie toutes les photos Visible.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    System.out.println("Demande de toutes les photos visible pour la page");

    try {
      ArrayList<PhotoDto> photos = photoUcc.getPhotosVisible();

      Genson genson = new GensonBuilder()
          .withConverter(ConverteurJson.getConverteurLocalDateJson(), LocalDate.class).create();

      Utils.replyAsSuccess(resp, genson.serialize(photos).getBytes(StandardCharsets.UTF_8),
          "envoie photos avec succes");

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB- Impossible de recupere les images");
    }
  }

  /**
   * Si il n y a pas de date, la methode renvoie la date courante.
   * 
   * @param parametresRequete parametre de la requete
   * @return la date
   */
  private LocalDate recupererDateDevis(Map<String, Object> parametresRequete) {
    LocalDate date;
    if (parametresRequete.get("date_image") != null) {
      String dateDevis = parametresRequete.get("date_image").toString();
      date = LocalDate.parse(dateDevis);
    } else {
      date = LocalDate.now();
    }
    return date;
  }

}


