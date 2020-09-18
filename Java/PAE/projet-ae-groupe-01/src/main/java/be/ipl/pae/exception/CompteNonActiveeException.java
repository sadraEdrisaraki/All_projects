package be.ipl.pae.exception;

public class CompteNonActiveeException extends Exception {

  private static final long serialVersionUID = 1L;

  public CompteNonActiveeException(String errorMessage) {
    super(errorMessage);
  }


}
