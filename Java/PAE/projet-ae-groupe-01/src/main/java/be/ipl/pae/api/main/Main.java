package be.ipl.pae.api.main;

import be.ipl.pae.api.serveur.Serveur;
import be.ipl.pae.config.InjectionService;

public class Main {

  /**
   * Lance le serveur et injecte les dependances.
   */
  public static void main(String[] args) {

    Serveur serveur = new Serveur();
    InjectionService.injecter(serveur, "prod.properties");
    serveur.start();
  }

}
