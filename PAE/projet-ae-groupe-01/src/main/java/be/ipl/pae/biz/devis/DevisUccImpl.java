package be.ipl.pae.biz.devis;

import be.ipl.pae.biz.EtatAmenagements;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.devis.DevisDao;
import be.ipl.pae.dal.dao.photo.PhotoDao;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DevisUccImpl implements DevisUcc {

  @Inject
  private DevisDao devisDao;

  @Inject
  private DalServivesDbManager dalServices;

  @Inject
  private PhotoDao photoDao;

  public DevisUccImpl() {}

  @Override
  public ArrayList<HashMap<String, String>> getDevisClient(int idClient) {
    ArrayList<HashMap<String, String>> listeDevisClient;

    try {
      dalServices.startTransaction();
      listeDevisClient = devisDao.getDevis(idClient);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return listeDevisClient;
  }

  @Override
  public ArrayList<DevisDto> getDevisClients() {
    ArrayList<DevisDto> listeDevisClient;

    try {
      dalServices.startTransaction();
      listeDevisClient = devisDao.getDevis();
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return listeDevisClient;
  }

  @Override
  public ArrayList<HashMap<String, String>> getDevisRealisation(
      Map<String, String> critereRecherche) {
    ArrayList<HashMap<String, String>> listeDevisClient;

    try {
      dalServices.startTransaction();
      listeDevisClient = devisDao.getDevisRealisation(critereRecherche);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return listeDevisClient;
  }

  /**
   * Change l'etat d'un devis.
   * 
   */
  @Override
  public void setEtatDevis(int idDevis, EtatAmenagements etat) {
    try {
      dalServices.startTransaction();
      if (etat.equals(EtatAmenagements.RV)) {
        devisDao.rendreVisible(idDevis);
        photoDao.rendreVisiblePhotos(idDevis);
      } else {
        devisDao.setEtatDevis(idDevis, etat);
      }
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }

  @Override
  public boolean setDateDebutTravaux(LocalDate date, int idDevis) {
    try {
      dalServices.startTransaction();
      DevisDto dto = devisDao.getDevisById(idDevis);

      if (dto.getDateDevis().isAfter(date)) {
        throw new BizException(
            "La date de debut des travaux doit etre posterieur a la date du devis");
      }
      return devisDao.setDateDebutTravaux(date, idDevis);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }



  @Override
  public DevisDto introduireDevis(DevisDto devisDto, int[] tabTypeAmenagement) {
    DevisBiz devisBiz = (DevisBiz) devisDto;
    if (!devisBiz.verifierMontantTotal()) {
      throw new BizException("Le montant doit etre plus grand que 0");
    }
    if (!devisBiz.verifierDureeTravaux()) {
      throw new BizException("La duree des travaux doit etre plus grand que 0");
    }
    try {
      dalServices.startTransaction();
      if (devisDao.introduireDevis(devisDto) == null) {
        throw new BizException("introduction du devis echoue");
      }
      if (!devisDao.introduireListeAmenagement(devisDto, tabTypeAmenagement)) {
        throw new BizException("introduction des listes d amenagements echoue");
      }
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return devisDto;
  }

  @Override
  public void setPhotoPreferee(int idPhoto, int idDevis) {
    try {
      dalServices.startTransaction();
      devisDao.setPhotoPreferee(idPhoto, idDevis);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }

  @Override
  public HashMap<String, String> getVisualisationDevis(int idDevis) {
    try {
      dalServices.startTransaction();
      return devisDao.getVisualisationDevis(idDevis);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
  }

}


