package be.ipl.pae.config;

public interface CheckUtils {

  /**
   * Check si object est null.
   */
  static void checkObject(Object object) {
    if (object == null) {
      throw new IllegalArgumentException("L'objet ne peut pas etre null");
    }
  }

  /**
   * Check si le string est vide.
   */
  static void checkString(String string) {
    checkObject(string);
    if (string.matches("\\s*")) {
      throw new IllegalArgumentException("La chaine ne peut pas etre vide");
    }

  }

  /**
   * Check si le string est un chiffre.
   */
  static void checkNumerique(String string) {
    checkString(string);
    try {
      Long.parseLong(string);
    } catch (NumberFormatException exception) {
      throw new IllegalArgumentException("La chaine doit etre un nombre valide");
    }
  }

  /**
   * Check si le nombre est positif.
   */
  static void checkPositive(double nombre) {
    if (nombre <= 0) {
      throw new IllegalArgumentException("La valeur ne peut pas etre negative ou nulle");
    }

  }

  /**
   * Check si le nombre est positif ou egal a zero.
   */
  static void checkPositiveOrZero(double nombre) {
    if (nombre < 0) {
      throw new IllegalArgumentException();
    }
  }
}
