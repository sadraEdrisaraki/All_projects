package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.photo.PhotoDto;
import be.ipl.pae.biz.photo.PhotoUcc;
import be.ipl.pae.config.ConverteurJson;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.FatalException;

import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class PhotosDevisServlet extends HttpServlet {

  @Inject
  private PhotoUcc photoUcc;

  public PhotosDevisServlet() {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    System.out.println("Demande de toutes les photos d'un devis");

    try {
      Genson genson = new GensonBuilder()
          .withConverter(ConverteurJson.getConverteurLocalDateJson(), LocalDate.class).create();
      int idDevis = Integer.valueOf(req.getHeader("parameter"));

      ArrayList<PhotoDto> photos = photoUcc.getPhotosDevis(idDevis);


      Utils.replyAsSuccess(resp, genson.serialize(photos).getBytes(StandardCharsets.UTF_8),
          "Photo envoyée");

    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB- Impossible de recupere les images");
    }
  }

}


