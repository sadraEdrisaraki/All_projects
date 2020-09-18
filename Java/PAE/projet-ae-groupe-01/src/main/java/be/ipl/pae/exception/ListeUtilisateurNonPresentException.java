package be.ipl.pae.exception;

public class ListeUtilisateurNonPresentException extends Exception {

  private static final long serialVersionUID = 1L;

  public ListeUtilisateurNonPresentException(String errorMessage) {
    super(errorMessage);
  }


}
