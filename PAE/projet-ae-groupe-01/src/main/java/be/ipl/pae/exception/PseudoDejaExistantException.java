package be.ipl.pae.exception;

public class PseudoDejaExistantException extends Exception {

  private static final long serialVersionUID = 1L;

  public PseudoDejaExistantException(String errorMessage) {
    super(errorMessage);
  }

}
