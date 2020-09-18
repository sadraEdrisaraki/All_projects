package be.ipl.pae.api.serveur;

import be.ipl.pae.api.ihm.AmenagementServlet;
import be.ipl.pae.api.ihm.ClientServlet;
import be.ipl.pae.api.ihm.ClientsSansUtilisateurServlet;
import be.ipl.pae.api.ihm.ConfirmationUtilisateurServlet;
import be.ipl.pae.api.ihm.DateTravauxDevisServlet;
import be.ipl.pae.api.ihm.DevisRealisationServlet;
import be.ipl.pae.api.ihm.DevisServlet;
import be.ipl.pae.api.ihm.ImageParTypeServlet;
import be.ipl.pae.api.ihm.LierUtilisateurClientServlet;
import be.ipl.pae.api.ihm.ListeDevisClientServlet;
import be.ipl.pae.api.ihm.LoginServlet;
import be.ipl.pae.api.ihm.PhotosDevisServlet;
import be.ipl.pae.api.ihm.RechercheServlet;
import be.ipl.pae.api.ihm.SendImageServlet;
import be.ipl.pae.api.ihm.UtilisateurServlet;
import be.ipl.pae.api.ihm.VerificationServlet;
import be.ipl.pae.api.ihm.VisualisationDevisServlet;
import be.ipl.pae.config.InjectionService;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.http.HttpServlet;

public class Serveur {

  public Serveur() {}


  /**
   * Demarre tout les servlet necessaire au serveur et demarre le serveur.
   */
  public void start() {

    WebAppContext context = new WebAppContext();
    context.setContextPath("/");

    // Login
    HttpServlet apiLogin = new LoginServlet();
    context.addServlet(new ServletHolder(apiLogin), "/utilisateur/login");
    InjectionService.injecter(apiLogin, "prod.properties");

    // Utilisateur CRUD
    HttpServlet apiUtilisateur = new UtilisateurServlet();
    context.addServlet(new ServletHolder(apiUtilisateur), "/utilisateur");
    InjectionService.injecter(apiUtilisateur, "prod.properties");

    // utilisateur action particulier
    HttpServlet lierUtilisateurClient = new LierUtilisateurClientServlet();
    context.addServlet(new ServletHolder(lierUtilisateurClient), "/utilisateur/lier");
    InjectionService.injecter(lierUtilisateurClient, "prod.properties");

    // Utilisateur action particulier
    HttpServlet apiVerification = new VerificationServlet();
    context.addServlet(new ServletHolder(apiVerification), "/utilisateur/verification");
    InjectionService.injecter(apiVerification, "prod.properties");

    // Devis CRUD
    HttpServlet apiIntroductionDevisServlet = new DevisServlet();
    context.addServlet(new ServletHolder(apiIntroductionDevisServlet), "/devis");
    InjectionService.injecter(apiIntroductionDevisServlet, "prod.properties");

    // Devis action particulier
    HttpServlet apiDateTravauxServlet = new DateTravauxDevisServlet();
    context.addServlet(new ServletHolder(apiDateTravauxServlet), "/devis/setdate");
    InjectionService.injecter(apiDateTravauxServlet, "prod.properties");

    HttpServlet listeDevisClientServlet = new ListeDevisClientServlet();
    context.addServlet(new ServletHolder(listeDevisClientServlet), "/listeDevis");
    InjectionService.injecter(listeDevisClientServlet, "prod.properties");

    // Client CRUD
    HttpServlet apiClientServlet = new ClientServlet();
    context.addServlet(new ServletHolder(apiClientServlet), "/client");
    InjectionService.injecter(apiClientServlet, "prod.properties");

    // Client get tout clients sans user
    HttpServlet apiToutClientServlet = new ClientsSansUtilisateurServlet();
    context.addServlet(new ServletHolder(apiToutClientServlet), "/client/clientSansUser");
    InjectionService.injecter(apiToutClientServlet, "prod.properties");


    // Amenagement CRUD
    HttpServlet apiListeAmenagement = new AmenagementServlet();
    context.addServlet(new ServletHolder(apiListeAmenagement), "/amenagement");
    InjectionService.injecter(apiListeAmenagement, "prod.properties");

    // DevisRealisation
    HttpServlet apiDevisRealisation = new DevisRealisationServlet();
    context.addServlet(new ServletHolder(apiDevisRealisation), "/devis/realisation");
    InjectionService.injecter(apiDevisRealisation, "prod.properties");


    // Visualisation devis
    HttpServlet apiVisualisationDevis = new VisualisationDevisServlet();
    context.addServlet(new ServletHolder(apiVisualisationDevis), "/devis/visualisation");
    InjectionService.injecter(apiVisualisationDevis, "prod.properties");

    // Recherche CRUD
    HttpServlet apiRecherche = new RechercheServlet();
    context.addServlet(new ServletHolder(apiRecherche), "/recherche");
    InjectionService.injecter(apiRecherche, "prod.properties");

    // Carousel CRUD
    HttpServlet apiCarousel = new ImageParTypeServlet();
    context.addServlet(new ServletHolder(apiCarousel), "/carousel");
    InjectionService.injecter(apiCarousel, "prod.properties");

    // Enregistrer Photo
    HttpServlet apiEnregistrerPhoto = new SendImageServlet();
    context.addServlet(new ServletHolder(apiEnregistrerPhoto), "/image");
    InjectionService.injecter(apiEnregistrerPhoto, "prod.properties");

    // Enregistrer Photo
    HttpServlet apiPhotosDevis = new PhotosDevisServlet();
    context.addServlet(new ServletHolder(apiPhotosDevis), "/image/devis");
    InjectionService.injecter(apiPhotosDevis, "prod.properties");

    // Utilisateur action particulier
    HttpServlet apiConfirmationUtilisateur = new ConfirmationUtilisateurServlet();
    context.addServlet(new ServletHolder(apiConfirmationUtilisateur), "/utilisateur/confirmation");
    InjectionService.injecter(apiConfirmationUtilisateur, "prod.properties");


    // Default Servlet
    HttpServlet defaultServlet = new DefaultServlet();
    context.addServlet(new ServletHolder(defaultServlet), "/");

    Server server =
        new Server(Integer.parseInt(InjectionService.getPropsData("server.properties", "port")));
    context.setResourceBase("public");
    server.setHandler(context);

    try {
      server.start();
      System.out.println("Server Started !");
    } catch (Exception exception) {
      System.out.println("Server Fatal Error !!");
      exception.printStackTrace();
    }

  }
}
