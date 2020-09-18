package be.ipl.pae.biz.client;


import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.client.ClientDao;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;

import java.util.ArrayList;
import java.util.Map;

public class ClientUccImpl implements ClientUcc {

  @Inject
  private ClientDao clientDao;
  @Inject
  private BizFactory bizFactory;
  @Inject
  private DalServivesDbManager dalServices;

  public ClientUccImpl() {}

  @Override
  public ArrayList<ClientDto> getClients() {
    ArrayList<ClientDto> listeClient;
    ClientDto client = bizFactory.getClient();
    try {
      dalServices.startTransaction();
      listeClient = clientDao.getClients(client);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return listeClient;
  }

  @Override
  public ArrayList<ClientDto> getClientsSansUtilisateurs() {
    ArrayList<ClientDto> listeClient;
    ClientDto client = bizFactory.getClient();
    try {
      dalServices.startTransaction();
      listeClient = clientDao.getClientsSansUtilisateurs(client);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return listeClient;
  }

  @Override
  public ClientDto inscrire(ClientDto clientDto) {
    ClientBiz clientBiz = (ClientBiz) clientDto;

    if (!clientBiz.verifierNom()) {
      System.out.println(clientBiz.getNom());
      throw new BizException("Erreur dans le nom");
    }
    if (!clientBiz.verifierPrenom()) {
      throw new BizException("Erreur dans le prenom");
    }
    if (!clientBiz.verifierRue()) {
      throw new BizException("Erreur dans la rue");
    }
    if (!clientBiz.verifierNumero()) {
      throw new BizException("Erreur dans le numero");
    }
    if (!clientBiz.verifierCodePostal()) {
      throw new BizException("Erreur dans le code postal");
    }
    if (!clientBiz.verifierEmail()) {
      throw new BizException("Erreur dans l email");
    }
    if (!clientBiz.verifierTelephone()) {
      throw new BizException("Erreur dans le telephone");
    }
    if (!clientBiz.verifierVille()) {
      throw new BizException("Erreur dans la ville");
    }

    ClientDto client;

    try {
      dalServices.startTransaction();
      if (clientDto.getBoite() == null || clientDto.getBoite().isEmpty()) {
        client = clientDao.inscrireWithoutBoite(clientDto);
      } else {
        client = clientDao.inscrire(clientDto);
      }
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc; // Propage l exception au servlet
    } finally {
      dalServices.commit();
    }

    return client;
  }


  @Override
  public ClientDto existe(ClientDto client) {
    try {
      dalServices.startTransaction();
      client = clientDao.existe(client);
    } catch (FatalException fatalException) {
      dalServices.rollback();
      throw fatalException; // Propage l exception au servlet
    } finally {
      dalServices.commit();
    }
    return client;
  }

  @Override
  public ArrayList<ClientDto> getClientsCritereRecherche(Map<String, String> critereRecherche) {
    ArrayList<ClientDto> listeClient;
    ClientDto client = bizFactory.getClient();
    try {
      dalServices.startTransaction();
      listeClient = clientDao.getClientsCritereRecherche(client, critereRecherche);
    } catch (FatalException exc) {
      dalServices.rollback();
      throw exc;
    } finally {
      dalServices.commit();
    }
    return listeClient;

  }

}

