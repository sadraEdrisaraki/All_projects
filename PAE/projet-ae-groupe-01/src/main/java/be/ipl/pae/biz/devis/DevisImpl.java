package be.ipl.pae.biz.devis;

import be.ipl.pae.biz.EtatAmenagements;

import java.time.LocalDate;

public class DevisImpl implements DevisBiz, Cloneable {
  private int idDevis;
  private int idClient;
  private LocalDate dateDevis;
  private double montantTotal;
  private int dureeTravaux;
  private int photoPrefere;
  private EtatAmenagements etat;
  private LocalDate dateDebutTravaux;
  private boolean visibiliteAmenagement;

  /**
   * Constructeur vide.
   */
  public DevisImpl() {}

  /**
   * Constructeur.
   */
  public DevisImpl(int idDevis, int idClient, LocalDate dateDevis, double montantTotal,
      int dureeTravaux, boolean visibiliteAmenagement) {

    this.idDevis = idDevis;
    this.idClient = idClient;
    this.dateDevis = dateDevis;
    this.montantTotal = montantTotal;
    this.dureeTravaux = dureeTravaux;
    this.visibiliteAmenagement = visibiliteAmenagement;
  }

  /**
   * Constructeur.
   */
  public DevisImpl(int idDevis, int idClient, LocalDate dateDevis, double montantTotal,
      int dureeTravaux, int photoPrefere, EtatAmenagements etat, LocalDate dateDebutTravaux,
      boolean visibiliteAmenagement) {


    this.idDevis = idDevis;
    this.idClient = idClient;
    this.dateDevis = dateDevis;
    this.montantTotal = montantTotal;
    this.dureeTravaux = dureeTravaux;
    this.photoPrefere = photoPrefere;
    this.etat = etat;
    this.dateDebutTravaux = dateDebutTravaux;
    this.visibiliteAmenagement = visibiliteAmenagement;
  }

  @Override
  public int getIdDevis() {
    return idDevis;
  }

  @Override
  public int getIdClient() {
    return idClient;
  }

  @Override
  public LocalDate getDateDevis() {
    return dateDevis;
  }

  @Override
  public void setDateDevis(LocalDate dateDevis) {
    this.dateDevis = dateDevis;
  }

  @Override
  public double getMontantTotal() {
    return montantTotal;
  }

  @Override
  public void setMontantTotal(double montantTotal) {
    this.montantTotal = montantTotal;
  }

  @Override
  public int getDureeTravaux() {
    return dureeTravaux;
  }

  @Override
  public void setDureeTravaux(int dureeTravaux) {
    this.dureeTravaux = dureeTravaux;
  }

  @Override
  public int getPhotoPrefere() {
    return photoPrefere;
  }

  @Override
  public void setPhotoPrefere(int photoPrefere) {
    this.photoPrefere = photoPrefere;
  }

  @Override
  public EtatAmenagements getEtat() {
    return etat;
  }

  @Override
  public void setEtat(EtatAmenagements etat) {
    this.etat = etat;
  }

  @Override
  public LocalDate getDateDebutTravaux() {
    return dateDebutTravaux;
  }

  @Override
  public void setDateDebutTravaux(LocalDate dateDebutTravaux) {
    this.dateDebutTravaux = dateDebutTravaux;
  }

  @Override
  public boolean isVisibiliteAmenagement() {
    return visibiliteAmenagement;
  }

  @Override
  public void setVisibiliteAmenagement(boolean visibiliteAmenagement) {
    this.visibiliteAmenagement = visibiliteAmenagement;
  }

  @Override
  public void setIdDevis(int idDevis) {
    this.idDevis = idDevis;
  }

  @Override
  public void setIdClient(int idClient) {
    this.idClient = idClient;
  }

  @Override
  public DevisImpl clone() {
    try {
      return (DevisImpl) super.clone();
    } catch (CloneNotSupportedException exception) {
      exception.printStackTrace();
    }
    return null;
  }

  @Override
  public boolean verifierDateDebutTravaux() {
    return dateDevis.isBefore(dateDebutTravaux);
  }

  @Override
  public boolean verifierMontantTotal() {
    return montantTotal > 0;
  }

  @Override
  public boolean verifierDureeTravaux() {
    return dureeTravaux > 0;
  }
}
