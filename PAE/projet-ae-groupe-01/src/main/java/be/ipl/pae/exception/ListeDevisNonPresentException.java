package be.ipl.pae.exception;

public class ListeDevisNonPresentException extends Exception {


  private static final long serialVersionUID = 1L;

  public ListeDevisNonPresentException(String errorMessage) {
    super(errorMessage);
  }
}
