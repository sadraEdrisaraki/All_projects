package be.ipl.pae.main.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.client.ClientUccImpl;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.InjectionService;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.client.ClientDao;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
class ClientUccImplTest {

  @InjectMocks
  private ClientUccImpl clientUccImpl;

  @Mock
  private ClientDao clientDao;

  @Mock
  private DalServivesDbManager dalServices;

  @Mock
  private BizFactory bizFactory;

  @Inject
  private BizFactory realBizFactory;

  private ClientDto clientDto;

  ArrayList<ClientDto> listeClient;

  {
    InjectionService.injecter(this, "prod.properties");
    MockitoAnnotations.initMocks(this);
  }

  @BeforeEach
  void setUp() throws Exception {

    clientDto = bizFactory.getClient();

    listeClient = new ArrayList<ClientDto>();
    listeClient.add(clientDto);
  }


  @Nested
  @DisplayName("Test de getClient")
  class TestGetClient {
    @Test
    void testGetClientClientVide() {
      when(clientDao.getClients(clientDto)).thenReturn(null);
      assertNull(clientUccImpl.getClients());
    }

    @Test
    void testGetClientReturnListeClient() {
      when(clientDao.getClients(clientDto)).thenReturn(listeClient);
      assertEquals(listeClient, clientUccImpl.getClients());
    }


    @Test
    void testGetClientCallRollBack() {
      when(clientDao.getClients(Mockito.any(ClientDto.class))).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> clientUccImpl.getClients());
      verify(dalServices).rollback();
    }
  }

  @Nested
  @DisplayName("Test de getClientSansUtilisateurs")
  class TestGetClientSansUtilisateurs {

    @Test
    void testGetClientSansUtilisateursClientVide() {
      when(clientDao.getClientsSansUtilisateurs(clientDto)).thenReturn(null);
      assertNull(clientUccImpl.getClientsSansUtilisateurs());
    }

    @Test
    void testGetClientSansUtilisateursReturnListeClient() {
      when(clientDao.getClientsSansUtilisateurs(clientDto)).thenReturn(listeClient);
      assertEquals(listeClient, clientUccImpl.getClientsSansUtilisateurs());
    }

    @DisplayName("Verifie le rollback est bien appellé quand on lance une FatalException")
    @Test
    void testGetClientSansUtilisateursCallRollback() {
      when(clientDao.getClientsSansUtilisateurs(Mockito.any(ClientDto.class)))
          .thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> clientUccImpl.getClientsSansUtilisateurs());
      verify(dalServices).rollback();
    }
  }

  @Nested
  class TestgetClientsCritereRecherche {
    @Test
    void testGetClientsCritereRecherche() {
      Map<String, String> critereRecherche = new HashMap<String, String>();
      critereRecherche.put("nomClient", "Ile");
      when(clientDao.getClientsCritereRecherche(clientDto, critereRecherche))
          .thenReturn(listeClient);
      assertEquals(listeClient, clientUccImpl.getClientsCritereRecherche(critereRecherche));
    }

    @Test
    void testGetClientsCritereRechercheCallsRollback() {
      Map<String, String> critereRecherche = new HashMap<String, String>();
      critereRecherche.put("nom", "Ile");
      when(clientDao.getClientsCritereRecherche(clientDto, critereRecherche))
          .thenThrow(FatalException.class);
      assertThrows(FatalException.class,
          () -> clientUccImpl.getClientsCritereRecherche(critereRecherche));
    }
  }

  @Nested
  @DisplayName("Test de inscrire")
  class TestInscrire {
    @Test
    void testInscrireSansBoite() {
      ClientDto clientDto = getClientSansBoite();
      clientUccImpl.inscrire(clientDto);
      verify(clientDao).inscrireWithoutBoite(clientDto);
      verify(dalServices).commit();
    }

    @Test
    void testInscrireBoiteVide() {
      ClientDto clientDto = getClientSansBoite();
      clientDto.setBoite("");
      clientUccImpl.inscrire(clientDto);
      verify(clientDao).inscrireWithoutBoite(clientDto);
      verify(dalServices).commit();
    }

    @Test
    void testInscrireAvecBoite() {
      ClientDto clientDto = getClientAvecBoite();
      clientUccImpl.inscrire(clientDto);
      verify(clientDao).inscrire(clientDto);
      verify(dalServices).commit();
    }

    @Test
    void testInscrireReturnNotNull() {
      ClientDto clientDto = getClientSansBoite();
      when(clientDao.inscrireWithoutBoite(clientDto)).thenReturn(clientDto);
      assertEquals(clientDto, clientUccImpl.inscrire(clientDto));
    }

    @DisplayName("Verifie que rollback est bien appellé quand on lance une FatalException")
    @Test
    void testInscrireCallRollback() {
      ClientDto clientDto = getClientAvecBoite();
      when(clientDao.inscrire(clientDto)).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> clientUccImpl.inscrire(clientDto));
      verify(dalServices).rollback();
    }

    @Test
    void testInscrireNomNull() {
      ClientDto clientDto = getClientAvecBoite();
      clientDto.setNom(null);
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }

    @Test
    void testInscrirePrenomNull() {
      ClientDto clientDto = getClientAvecBoite();
      clientDto.setPrenom(null);
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }

    @Test
    void testInscrireRueNull() {
      ClientDto clientDto = getClientAvecBoite();
      clientDto.setRue(null);
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }

    @Test
    void testInscrireNumeroNull() {
      ClientDto clientDto = getClientSansNumero();
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }

    @Test
    void testInscrireCodePostalNull() {
      ClientDto clientDto = getClientSansCodePostal();
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }

    @Test
    void testInscrireEmailNull() {
      ClientDto clientDto = getClientAvecBoite();
      clientDto.setEmail(null);
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }

    @Test
    void testInscrireTelephoneNull() {
      ClientDto clientDto = getClientAvecBoite();
      clientDto.setTelephone(null);
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }

    @Test
    void testInscrireVilleNull() {
      ClientDto clientDto = getClientAvecBoite();
      clientDto.setVille(null);
      assertThrows(BizException.class, () -> clientUccImpl.inscrire(clientDto));
    }
  }

  @Nested
  @DisplayName("Test de existe")
  class TestExiste {
    @Test
    void testExisteReturnNull() {
      ClientDto clientDto = getClientAvecBoite();
      when(clientDao.existe(clientDto)).thenReturn(null);
      assertNull(clientUccImpl.existe(clientDto));
    }

    @Test
    void testExisteCallCommit() {
      ClientDto clientDto = getClientAvecBoite();
      clientUccImpl.existe(clientDto);
      verify(dalServices).commit();
    }

    @DisplayName("Verifie que rollback est bien appellé quand on lance une FatalException")
    @Test
    void testExisteCallRollback() {
      ClientDto clientDto = getClientAvecBoite();
      when(clientDao.existe(clientDto)).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> clientUccImpl.existe(clientDto));
      verify(dalServices).rollback();
    }

  }


  private ClientDto getClientAvecBoite() {
    ClientDto clientDto = realBizFactory.getClient();
    clientDto.setBoite("1");
    clientDto.setNom("Robin");
    clientDto.setPrenom("Robin");
    clientDto.setRue("La rue");
    clientDto.setCodePostal(1480);
    clientDto.setNumero(45);
    clientDto.setVille("Bizetu");
    clientDto.setEmail("rf@qsc.com");
    clientDto.setTelephone("0478019869");
    return clientDto;
  }

  private ClientDto getClientSansBoite() {
    ClientDto clientDto = realBizFactory.getClient();
    clientDto.setNom("Robin");
    clientDto.setPrenom("Robin");
    clientDto.setRue("La rue");
    clientDto.setCodePostal(1480);
    clientDto.setNumero(45);
    clientDto.setVille("Bizetu");
    clientDto.setEmail("rf@qsc.com");
    clientDto.setTelephone("0478019869");
    return clientDto;
  }

  private ClientDto getClientSansNumero() {
    ClientDto clientDto = realBizFactory.getClient();
    clientDto.setBoite("1");
    clientDto.setNom("Robin");
    clientDto.setPrenom("Robin");
    clientDto.setRue("La rue");
    clientDto.setCodePostal(1480);
    clientDto.setVille("Bizetu");
    clientDto.setEmail("rf@qsc.com");
    clientDto.setTelephone("0478019869");
    return clientDto;
  }

  private ClientDto getClientSansCodePostal() {
    ClientDto clientDto = realBizFactory.getClient();
    clientDto.setBoite("1");
    clientDto.setNom("Robin");
    clientDto.setPrenom("Robin");
    clientDto.setRue("La rue");
    clientDto.setVille("Bizetu");
    clientDto.setNumero(45);
    clientDto.setEmail("rf@qsc.com");
    clientDto.setTelephone("0478019869");
    return clientDto;
  }

}
