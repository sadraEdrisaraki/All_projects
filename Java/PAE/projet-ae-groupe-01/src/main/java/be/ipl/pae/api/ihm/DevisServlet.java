package be.ipl.pae.api.ihm;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.EtatAmenagements;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.client.ClientUcc;
import be.ipl.pae.biz.devis.DevisDto;
import be.ipl.pae.biz.devis.DevisUcc;
import be.ipl.pae.config.ConverteurJson;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.Utils;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class DevisServlet extends HttpServlet {
  @Inject
  private DevisUcc devisUcc;
  @Inject
  private ClientUcc clientUcc;
  @Inject
  private BizFactory bizFactory;


  public DevisServlet() {}

  /**
   * Introduction Devis.
   */
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    try {
      System.out.println("Appel au servlet IntroductionDevisServlet");
      String token = req.getHeader("authorization");

      if (Utils.verifyToken(token) == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return; // stop la requete si token pas valable
      }

      Genson genson = new Genson();

      Map<String, Object> parametresRequete = genson.deserialize(req.getReader(), HashMap.class);

      // Recuperation de la liste des types d amenagements

      ArrayList<Object> listeAmenagement =
          (ArrayList<Object>) parametresRequete.get("liste_amenagement");

      String listeSerialise = genson.serialize(listeAmenagement);

      ArrayList<String> listeIdAmenagement = genson.deserialize(listeSerialise, ArrayList.class);

      String verification = this.checkParameters(parametresRequete);
      if (verification != null) {
        Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, verification,
            "erreur : verification parametre");
        return;
      }

      // Liste d amenagement
      int[] tabIdTypeAmenagement = new int[listeIdAmenagement.size()];
      for (int i = 0; i < listeIdAmenagement.size(); i++) {
        tabIdTypeAmenagement[i] = Integer.parseInt(listeIdAmenagement.get(i).toString());
      }

      int dureeTravaux = recupererDureeTravaux(parametresRequete);
      String emailClient = parametresRequete.get("email").toString();
      String typeAjout = parametresRequete.get("ajout").toString();
      System.out.println(emailClient);

      // Le client existe deja en db mais on verifie quand meme
      ClientDto client = bizFactory.getClient();

      if (typeAjout.equals("manuel")) {
        client.setEmail(emailClient);
        if (clientUcc.existe(client) == null) {
          Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, "client non existant",
              "client non existant");
          return;
        }
      }
      LocalDate dateDevis = recupererDateDevis(parametresRequete);
      double montant = recupererMontantTotal(parametresRequete);

      DevisDto devisDto = bizFactory.getDevis();
      devisDto.setDureeTravaux(dureeTravaux);
      devisDto.setMontantTotal(montant);
      devisDto.setDateDevis(dateDevis);
      devisDto.setIdClient(client.getIdClient());

      // introduction du devis
      devisUcc.introduireDevis(devisDto, tabIdTypeAmenagement);

      Map<String, String> rep = new HashMap<String, String>() {
        {
          put("success", "true");
          put("message", "ajout reussi");
          put("id_devis", String.valueOf(devisDto.getIdDevis()));
        }
      };

      Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
          "ajout reussi");

    } catch (BizException bizException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_CONFLICT, bizException.getMessage(),
          bizException.getMessage());
    } catch (FatalException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          "erreur Serveur - DB");
    } catch (DateTimeException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
          "La date n a pas pu etre parse", "erreur - parsing data");
    } catch (ClassCastException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
          "Erreur format liste amengament", "erreur - liste amenagement");
    } catch (NumberFormatException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
          "Impossible a parse le nombre", "erreur - parsing nombre");
    }
  }

  /**
   * Obtenirs tous les devis de tous les clients.
   */
  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    try {
      System.out.println("\nAppel au servlet Devis");

      DecodedJWT jwt;

      Genson genson = new GensonBuilder().setSkipNull(false)
          .withConverter(ConverteurJson.getConverteurLocalDateJson(), LocalDate.class).create();

      // Verification token
      String token = req.getHeader("authorization");
      jwt = Utils.verifyToken(token);
      if (jwt == null) {
        Utils.replyWithWrongTokenError(resp, token);
        return;
      }


      String idClient = req.getHeader("parameter");

      if (idClient != null) {
        System.out.println("tous les devis d'un client");
        ArrayList<HashMap<String, String>> devis =
            devisUcc.getDevisClient(Integer.parseInt(idClient));

        Utils.replyAsSuccess(resp, genson.serialize(devis).getBytes(StandardCharsets.UTF_8),
            "envoie tous les devis d un client");

      } else {
        System.out.println("tous les devis de tous les clients");
        ArrayList<DevisDto> listeDevis = devisUcc.getDevisClients();

        Utils.replyAsSuccess(resp, genson.serialize(listeDevis).getBytes(StandardCharsets.UTF_8),
            "envoie devis ok");
      }
    } catch (FatalException fatalException) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
          fatalException.getMessage(), "erreur Serveur - DB");
    } catch (NumberFormatException exception) {
      Utils.replyAsError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage(),
          exception.getMessage());
    }

  }

  /**
   * Set etat devis.
   */
  @SuppressWarnings({"unchecked"})

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    DecodedJWT jwt;

    String token = req.getHeader("authorization");
    jwt = Utils.verifyToken(token);
    if (jwt == null) {
      Utils.replyWithWrongTokenError(resp, token);
      return;
    }

    Genson genson = new Genson();
    HashMap<String, String> map = genson.deserialize(req.getReader(), HashMap.class);
    System.out.println(req.getReader());

    String idDevis = map.get("id_devis");
    String etatDevis = map.get("etat_devis");
    devisUcc.setEtatDevis(Integer.valueOf(idDevis), EtatAmenagements.valueOf(etatDevis));

    Map<String, String> rep = new HashMap<String, String>() {
      {
        put("success", "true");
        put("message", "confirmation devis ok");
      }
    };

    Utils.replyAsSuccess(resp, genson.serialize(rep).getBytes(StandardCharsets.UTF_8),
        "Confirmation devis ok");
  }

  private double recupererMontantTotal(Map<String, Object> parametresRequete) {
    return Double.parseDouble(parametresRequete.get("montant_total_devis").toString());
  }

  private int recupererDureeTravaux(Map<String, Object> parametresRequete) {
    return Integer.parseInt(parametresRequete.get("duree_travaux").toString());
  }

  private LocalDate recupererDateDevis(Map<String, Object> parametresRequete) {
    String dateDevis = parametresRequete.get("date_devis").toString();
    LocalDate date = LocalDate.parse(dateDevis);
    return date;
  }

  private String checkParameters(Map<String, Object> parametresRequete) {
    // nom
    if (!parametresRequete.containsKey("nom")) {
      return "nom manquant";
    }
    if (String.valueOf(parametresRequete.get("nom")).length() == 0) {
      return "nom vide";
    }

    // email
    if (!parametresRequete.containsKey("email")) {
      return "email manquant";
    }
    if (String.valueOf(parametresRequete.get("email")).length() == 0) {
      return "email vide";
    }

    // date devis
    if (!parametresRequete.containsKey("date_devis")) {
      return "date devis manquant";
    }
    if (String.valueOf(parametresRequete.get("date_devis")).length() == 0) {
      return "date devis vide";
    }

    // montant total
    if (!parametresRequete.containsKey("montant_total_devis")) {
      return "montant total manquant";
    }
    if (String.valueOf(parametresRequete.get("montant_total_devis")).length() == 0) {
      return "montant total vide";
    }

    // duree travaux
    if (!parametresRequete.containsKey("duree_travaux")) {
      return "duree travaux manquant";
    }
    if (String.valueOf(parametresRequete.get("duree_travaux")).length() == 0) {
      return "duree travaux vide";
    }

    // amenagement
    if (!parametresRequete.containsKey("liste_amenagement")) {
      return "liste amenagement manquant";
    }
    if (((ArrayList<Object>) parametresRequete.get("liste_amenagement")).isEmpty()) {
      return "liste amenagement vide";
    }

    // ajout
    if (!parametresRequete.containsKey("ajout")) {
      return "ajout manquant";
    }
    if (String.valueOf(parametresRequete.get("ajout")).length() == 0) {
      return "ajout vide";
    }
    return null;
  }

}
