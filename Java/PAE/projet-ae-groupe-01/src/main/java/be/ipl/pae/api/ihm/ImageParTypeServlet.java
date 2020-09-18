package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.photo.PhotoDto;
import be.ipl.pae.biz.photo.PhotoUcc;
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

public class ImageParTypeServlet extends HttpServlet {

  @Inject
  private PhotoUcc photoUcc;

  public ImageParTypeServlet() {}

  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    System.out.println("Appel au servlet image");

    Genson genson = new Genson();
    Map<String, Object> parametresRequete = genson.deserialize(req.getReader(), HashMap.class);

    try {

      int idAmenagement = Integer.parseInt(parametresRequete.get("id").toString());
      ArrayList<PhotoDto> listePhoto = photoUcc.getPhotosParAmenagement(idAmenagement);

      Utils.replyAsSuccess(resp, genson.serialize(listePhoto).getBytes(StandardCharsets.UTF_8),
          "envoie photos avec succes");


    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    }


  }

  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    System.out.println("Appel au servlet image");
  }

}
