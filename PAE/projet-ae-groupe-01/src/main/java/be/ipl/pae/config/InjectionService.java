package be.ipl.pae.config;

import be.ipl.pae.exception.FatalException;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class InjectionService {

  private static Map<String, Properties> mapProperties = new HashMap<String, Properties>();
  /**
   * Permet d'avoir des singletons.
   */
  private static Map<String, Object> dependencies = new HashMap<String, Object>();

  static {
    // Parcours tous les fichiers .properties et les place dans mapProperties
    try {
      Files.walk(Paths.get("./"), 1).filter(Files::isRegularFile)
          .filter(p -> p.getFileName().toString().endsWith("properties")).forEach(p -> {
            String fileName = p.toString();
            fileName = fileName.substring(2);
            // Pacours tous le fichier properties
            try (FileInputStream file = new FileInputStream(fileName)) {
              Properties prop = new Properties();
              prop.load(file);
              mapProperties.put(fileName, prop);
            } catch (Exception exception) {
              System.out.println("Injection service a cesser de fonctioner");
              exception.printStackTrace();
              throw new RuntimeException();
            }
          });
    } catch (IOException ioException) {
      throw new FatalException(ioException);
    }
  }

  private InjectionService() {}


  /**
   * Prend en prarametre un objet de type class qui est une interface et la methode renvoie un objet
   * de son implementation.
   * 
   * @param <T> Type generique
   * @param objClass est l interface
   * @param nomFichier le nom du fichier ou allez chercher objClass
   * @return une instance de l implementation de l interface objClass defini dans le fichier passe
   *         en parametre
   */
  @SuppressWarnings("unchecked")
  public static <T> T getDependency(Class<?> objClass, String nomFichier) {

    System.out.println("on tente de recuperer l impl de " + objClass.toString());

    String cls = mapProperties.get(nomFichier).getProperty(objClass.getName());
    System.out.println(cls + "  recupere");

    if (dependencies.containsKey(cls)) {
      return (T) dependencies.get(cls);
    }

    try {
      Constructor<?> constructor = Class.forName(cls).getDeclaredConstructor();
      constructor.setAccessible(true);

      Object dependency = constructor.newInstance();
      dependencies.put(cls, dependency);

      // Apres creation de l obj on injecte ses dependances
      injecter(dependency, nomFichier);

      return (T) dependency;

    } catch (Exception exception) {
      throw new FatalException("Erreur lors de la lecture du fichier de dependance");
    }
  }

  public static String getPropsData(String nomFichier, String propsNom) {
    Properties props = mapProperties.get(nomFichier);
    return props.getProperty(propsNom);
  }


  /**
   * Injecte les dependances dans les attributs avec l annotations @Inject de l objet passe en
   * paramatre. L injection se fait de maniere recursif, si la dependance injecte a aussi besoin d
   * une dependance celle ci sera aussi injecte.
   * 
   * @param obj l objet dont on veut injecter les dependances
   * @param nomFichier Nom du fichier properties qui sera utilise pour l'injection
   */

  public static void injecter(Object obj, String nomFichier) {

    Class<?> cls = obj.getClass();
    for (Field field : cls.getDeclaredFields()) {
      if (field.isAnnotationPresent(Inject.class)) {
        field.setAccessible(true);
        try {
          if (field.get(obj) != null) {
            return;
          }
        } catch (IllegalArgumentException | IllegalAccessException exception) {
          throw new FatalException(exception);
        }
        try {
          // modifie l attribut de l objet obj
          field.set(obj, getDependency(field.getType(), nomFichier));
        } catch (IllegalArgumentException | IllegalAccessException exception) {
          throw new FatalException(exception);
        }
      }

    }
  }
}
