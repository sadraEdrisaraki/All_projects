package be.ipl.pae.exception;

public class EmailDejaExistantException extends Exception {

  private static final long serialVersionUID = 1L;

  public EmailDejaExistantException(String errorMessage) {
    super(errorMessage);
  }

}
