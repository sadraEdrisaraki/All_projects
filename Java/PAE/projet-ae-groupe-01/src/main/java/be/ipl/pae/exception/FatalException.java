package be.ipl.pae.exception;

import java.sql.SQLException;

/**
 * Exception dans le cas où une erreur avec la base de donnee apparait. On ne sait rien faire, le
 * serveur doit etre coupe.
 *
 */
@SuppressWarnings("serial")
public class FatalException extends RuntimeException {

  public FatalException(Throwable exception) {
    super(exception);
  }

  public FatalException(SQLException exception) {
    super(exception);
  }

  public FatalException(String errorMsg) {
    super(errorMsg);
  }
}
