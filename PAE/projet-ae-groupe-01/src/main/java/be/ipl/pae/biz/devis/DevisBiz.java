package be.ipl.pae.biz.devis;

public interface DevisBiz extends DevisDto {
  boolean verifierDateDebutTravaux();

  boolean verifierMontantTotal();

  boolean verifierDureeTravaux();
}
