package be.ipl.pae.biz.utilisateur;

import static be.ipl.pae.config.CheckUtils.checkObject;
import static be.ipl.pae.config.CheckUtils.checkString;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.client.ClientDao;
import be.ipl.pae.dal.dao.utilisateur.UtilisateurDao;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.CompteNonActiveeException;
import be.ipl.pae.exception.FatalException;

import java.util.ArrayList;
import java.util.Map;

public class UtilisateurUccImpl implements UtilisateurUcc {

  @Inject
  private UtilisateurDao utilisateurDao;
  @Inject
  private ClientDao clientDao;
  @Inject
  private BizFactory bizFactory;
  @Inject
  private DalServivesDbManager dalServices;

  public UtilisateurUccImpl() {}

  @Override
  public UtilisateurDto seConnecter(String pseudo, String motDePasse)
      throws CompteNonActiveeException {

    UtilisateurDto utilisateur;
    try {
      dalServices.startTransaction();

      utilisateur = utilisateurDao.getUtilisateur(pseudo);

    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }

    if (utilisateur == null) {
      return null;
    }

    if (UtilisateurImpl.checkMotDePasseCrypte(motDePasse, utilisateur.getMot_de_passe())) {
      if (!utilisateur.getConfirmationInscription()) {
        throw new CompteNonActiveeException(pseudo + " est un compte non active");
      }
      return utilisateur;
    } else {
      return null;
    }

  }

  @Override
  public UtilisateurDto getUtilisateur(String pseudo) {
    checkObject(pseudo);
    checkString(pseudo);

    UtilisateurDto utilisateur;
    try {
      dalServices.startTransaction();
      utilisateur = utilisateurDao.getUtilisateur(pseudo);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }

    return utilisateur;
  }

  @Override
  public UtilisateurDto getUtilisateur(int idUtilisateur) {

    UtilisateurDto utilisateur;
    try {
      dalServices.startTransaction();
      utilisateur = utilisateurDao.getUtilisateur(idUtilisateur);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }

    return utilisateur;
  }

  @Override
  public UtilisateurDto inscrireUtilisateur(UtilisateurDto utilisateurDto) {

    UtilisateurBiz utilisateur = (UtilisateurBiz) utilisateurDto;

    if (!utilisateur.verifierNom()) {
      throw new BizException("Erreur dans le nom");
    }
    if (!utilisateur.verifierPrenom()) {
      throw new BizException("Erreur dans le prenom");
    }
    if (!utilisateur.verifierPseudo()) {
      throw new BizException("Erreur dans le pseudo");
    }
    if (!utilisateur.verifierEmail()) {
      throw new BizException("Erreur dans l'email");
    }
    if (!utilisateur.verifierVille()) {
      throw new BizException("Erreur dans la ville");
    }
    if (!utilisateur.verifierMdp()) {
      throw new BizException("Erreur dans la mot de passe");
    }
    if (!utilisateur.verifierType()) {
      throw new BizException("Erreur dans le type");
    }

    try {
      dalServices.startTransaction();
      if (!utilisateurDao.pseudoEstUnique(utilisateurDto.getPseudo())) {
        System.out.println("Pseudo Deja existant : " + utilisateur.getPseudo());
        throw new BizException("Pseudo Deja existant");
      }

      if (!utilisateurDao.emailExistePas(utilisateurDto.getEmail())) {
        System.out.println("Email deja existant : " + utilisateur.getEmail());
        throw new BizException("Email deja existant");
      }

      utilisateur
          .setMot_de_passe(UtilisateurImpl.crypterMotDePasse(utilisateurDto.getMot_de_passe()));

      return utilisateurDao.inscrire(utilisateurDto);

    } catch (Exception exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }

  @Override
  public ArrayList<UtilisateurDto> getUtilisateurs(Map<String, String> critereRecherche) {

    try {
      dalServices.startTransaction();
      ArrayList<UtilisateurDto> listeUtilisateur = utilisateurDao.getUtilisateurs(critereRecherche);
      if (listeUtilisateur == null) {
        throw new BizException("Il n'y a pas d'Utilisateur");
      }
      return listeUtilisateur;
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }

  @Override
  public ArrayList<UtilisateurDto> getUtilisateursSansClient() {
    ArrayList<UtilisateurDto> listeUtilisateur;
    try {
      dalServices.startTransaction();
      listeUtilisateur = utilisateurDao.getUtilisateursSansClient();
      if (listeUtilisateur == null) {
        throw new BizException("Il n'y a pas d'Utilisateur");
      }

    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return listeUtilisateur;
  }

  @Override
  public void lierCompteClientUtilisateur(String mailClient, String mailUtilisateur) {
    try {
      dalServices.startTransaction();
      ClientDto client = bizFactory.getClient();
      client.setEmail(mailClient);
      client = clientDao.existe(client);
      UtilisateurDto utilisateur = utilisateurDao.getUtilisateurByMail(mailUtilisateur);
      if (client == null || utilisateur == null) {
        throw new BizException("aucun compte retrouve");
      }

      utilisateurDao.lierCompteClientUtilisateur(client, utilisateur);

    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }

  @Override
  public void definirCompteOuvrier(String email) {
    try {
      dalServices.startTransaction();
      UtilisateurDto utilisateur = utilisateurDao.getUtilisateurByMail(email);
      if (utilisateur == null) {
        throw new BizException("aucun compte retrouve");
      }

      utilisateurDao.definirOuvrier(utilisateur);

    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }

  }

  /**
   * Renvoie une liste d utilisateur inscrit mais pas confirme.
   */
  @Override
  public ArrayList<UtilisateurDto> getUtilisateurNonConfirme() {
    try {
      dalServices.startTransaction();
      return utilisateurDao.getUtilisateurNonConfirme();
    } catch (FatalException exception) {
      dalServices.rollback();
      throw exception;
    } finally {
      dalServices.commit();
    }
  }

  @Override
  public boolean confirmerInscription(UtilisateurDto utilisateur) {

    try {
      dalServices.startTransaction();
      return utilisateurDao.confirmerInscription(utilisateur);
    } catch (FatalException exception) {
      dalServices.rollback();
      throw exception;
    } finally {
      dalServices.commit();
    }
  }
}

