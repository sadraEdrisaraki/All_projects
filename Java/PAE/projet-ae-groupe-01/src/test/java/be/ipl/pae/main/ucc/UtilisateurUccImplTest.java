package be.ipl.pae.main.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.TypeUtilisateurEnum;
import be.ipl.pae.biz.client.ClientDto;
import be.ipl.pae.biz.utilisateur.UtilisateurDto;
import be.ipl.pae.biz.utilisateur.UtilisateurUccImpl;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.InjectionService;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.client.ClientDao;
import be.ipl.pae.dal.dao.utilisateur.UtilisateurDao;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.CompteNonActiveeException;
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

@SuppressWarnings("unchecked")
class UtilisateurUccImplTest {



  @InjectMocks
  private UtilisateurUccImpl utilisateurUcc;

  @Mock
  private UtilisateurDao utilisateurDao;

  @Mock
  private ClientDao clientDao;

  @Mock
  private DalServivesDbManager dalServices;

  @Mock
  private BizFactory bizFactory;

  @Inject
  private BizFactory realBizFactory;


  private UtilisateurDto utilisateurDto;


  private static final String PASSWORD = "1";
  private static final String USERNAME = "fr";

  {
    InjectionService.injecter(this, "prod.properties");
    MockitoAnnotations.initMocks(this);
  }

  @BeforeEach
  void setUp() throws Exception {
    utilisateurDto = getUtilisateurDto();

    when(utilisateurDao.pseudoEstUnique(utilisateurDto.getPseudo())).thenReturn(false);
    when(utilisateurDao.emailExistePas(utilisateurDto.getEmail())).thenReturn(true);

  }

  private UtilisateurDto getUtilisateurDto() {
    UtilisateurDto userDto = realBizFactory.getUtilisateur();
    userDto.setNom("george");
    userDto.setPrenom("Paul");
    userDto.setEmail("georgePaul@gmail.com");
    userDto.setPseudo("GP");
    userDto.setVille("Bruxelles");
    userDto.setType(TypeUtilisateurEnum.C);
    userDto.setMot_de_passe("georgePaul1");
    userDto.setConfirmationInscription(false);
    return userDto;
  }



  private ClientDto getClientDto() {
    ClientDto clientDto = realBizFactory.getClient();
    clientDto.setIdClient(Integer.MAX_VALUE - 1);
    return clientDto;
  }


  @Nested
  class TestSeConnecter {
    @Test
    void testCheckCompteMdpCorrect() throws CompteNonActiveeException {
      UtilisateurDto userDto = getUtilisateurDto();
      userDto.setType(TypeUtilisateurEnum.C);
      userDto.setMot_de_passe("$2a$10$TQlTatRoPb8JW2buIS.yc.jeuJ2SCRUH.x7emfQG0fJ6e8F.fzgBG");
      userDto.setConfirmationInscription(true);
      when(utilisateurDao.getUtilisateur(USERNAME)).thenReturn(userDto);
      assertNotNull(utilisateurUcc.seConnecter(USERNAME, PASSWORD));
    }

    @Test
    void testSeConnecterThrowCompteNonActiveException() {
      UtilisateurDto userDto = getUtilisateurDto();
      userDto.setType(TypeUtilisateurEnum.C);
      userDto.setMot_de_passe("$2a$10$TQlTatRoPb8JW2buIS.yc.jeuJ2SCRUH.x7emfQG0fJ6e8F.fzgBG");
      when(utilisateurDao.getUtilisateur(USERNAME)).thenReturn(userDto);
      assertThrows(CompteNonActiveeException.class,
          () -> utilisateurUcc.seConnecter(USERNAME, PASSWORD));
    }

    @Test
    void testSeConnecterCallRollback() {
      when(utilisateurDao.getUtilisateur(Mockito.anyString())).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.seConnecter(USERNAME, PASSWORD));
      verify(dalServices).rollback();
    }

    @Test
    void testSeConnecterCompteOuvrier() throws CompteNonActiveeException {
      UtilisateurDto userDto = getUtilisateurDto();
      userDto.setType(TypeUtilisateurEnum.O);
      userDto.setMot_de_passe("$2a$10$TQlTatRoPb8JW2buIS.yc.jeuJ2SCRUH.x7emfQG0fJ6e8F.fzgBG");
      userDto.setConfirmationInscription(true);
      when(utilisateurDao.getUtilisateur(USERNAME)).thenReturn(userDto);
      when(utilisateurDao.estCompteActivee(USERNAME)).thenReturn(true);
      assertEquals(userDto, utilisateurUcc.seConnecter(USERNAME, PASSWORD));
    }

    @Test
    void testSeConnecterReturnNullMauvaisMotDePasse() throws CompteNonActiveeException {
      UtilisateurDto userDto = getUtilisateurDto();
      userDto.setMot_de_passe("$2a$10$TQlTatRoPb8JW2buIS.yc.jeuJ2SCRUH.x7emfQG0fJ6e8F.fzgBG");

      when(utilisateurDao.getUtilisateur(USERNAME)).thenReturn(userDto);
      assertNull(utilisateurUcc.seConnecter(USERNAME, "motdepasse"));
    }

    /*
     * @Test void testCheckCompteParametreNull() { assertAll( () ->
     * assertThrows(IllegalArgumentException.class, () -> utilisateurUcc.seConnecter(USERNAME,
     * null)), () -> assertThrows(IllegalArgumentException.class, () ->
     * utilisateurUcc.seConnecter(null, PASSWORD))); }
     * 
     * @Test void testCheckCompteParametreVide() { assertAll( () ->
     * assertThrows(IllegalArgumentException.class, () -> utilisateurUcc.seConnecter("", PASSWORD)),
     * () -> assertThrows(IllegalArgumentException.class, () -> utilisateurUcc.seConnecter(USERNAME,
     * ""))); }
     * 
     */
    @Test
    void testCheckCompteMdpPseudoInexistant() throws CompteNonActiveeException {
      assertNull(utilisateurUcc.seConnecter("212122122", "bonjour"));
    }

  }

  @Nested
  class TestInscire {
    @Test
    void testInscrireUtilisateur() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      when(utilisateurDao.pseudoEstUnique(utilisateurDto.getPseudo())).thenReturn(true);
      when(utilisateurDao.emailExistePas(utilisateurDto.getEmail())).thenReturn(true);
      when(utilisateurDao.inscrire(utilisateurDto)).thenReturn(utilisateurDto);
      utilisateurUcc.inscrireUtilisateur(utilisateurDto);
      verify(dalServices).startTransaction();
      verify(dalServices).commit();
    }


    @Test
    void testInscrireNomNull() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      utilisateurDto.setNom(null);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrirePrenomNull() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      utilisateurDto.setPrenom(null);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrirePseudoNull() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      utilisateurDto.setPseudo(null);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrireEmailNull() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      utilisateurDto.setEmail(null);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrireVilleNull() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      utilisateurDto.setVille(null);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrireMdpNull() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      utilisateurDto.setMot_de_passe(null);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrireTypeNull() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      utilisateurDto.setType(null);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrireThrowBizExceptionPseudoPasUnique() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      when(utilisateurDao.pseudoEstUnique(Mockito.anyString())).thenReturn(false);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrireThrowBizExceptionEmailExiste() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      when(utilisateurDao.pseudoEstUnique(Mockito.anyString())).thenReturn(true);
      when(utilisateurDao.emailExistePas(Mockito.anyString())).thenReturn(false);
      assertThrows(BizException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
    }

    @Test
    void testInscrireThrowFatalException() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      when(utilisateurDao.pseudoEstUnique(Mockito.anyString())).thenReturn(true);
      when(utilisateurDao.emailExistePas(Mockito.anyString())).thenReturn(true);
      when(utilisateurDao.inscrire(utilisateurDto)).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.inscrireUtilisateur(utilisateurDto));
      verify(dalServices).rollback();
    }
  }

  @Nested
  @DisplayName("Test de la fonction lier compte")
  class TestLierCompte {
    @Test
    void testLierCompteClientUtilisateur() {
      ClientDto clientDto = getClientDto();
      UtilisateurDto utilisateurDto = getUtilisateurDto();

      when(bizFactory.getClient()).thenReturn(clientDto);
      when(clientDao.existe(clientDto)).thenReturn(clientDto);
      when(utilisateurDao.getUtilisateurByMail("mail")).thenReturn(utilisateurDto);
      utilisateurUcc.lierCompteClientUtilisateur("mail", "mail");
      verify(dalServices).startTransaction();
      verify(utilisateurDao).lierCompteClientUtilisateur(clientDto, utilisateurDto);
      verify(dalServices).commit();

    }

    @Test
    void testLierCompteUtilisateurUtilisateurNull() {
      ClientDto clientDto = getClientDto();
      when(bizFactory.getClient()).thenReturn(clientDto);
      when(utilisateurDao.getUtilisateurByMail(Mockito.anyString())).thenReturn(null);
      assertThrows(BizException.class,
          () -> utilisateurUcc.lierCompteClientUtilisateur("test", "test"));
    }

    @Test
    void testLierCompteUtilisateurThrowBizExceptionClientNull() {
      ClientDto clientDto = getClientDto();
      when(bizFactory.getClient()).thenReturn(clientDto);
      when(clientDao.existe(clientDto)).thenReturn(null);
      assertThrows(BizException.class,
          () -> utilisateurUcc.lierCompteClientUtilisateur("test", "test"));
    }

    @Test
    void testLierCompteUtilisateurCallRollback() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      ClientDto clientDto = getClientDto();
      when(bizFactory.getClient()).thenReturn(clientDto);
      when(utilisateurDao.getUtilisateurByMail(Mockito.anyString())).thenReturn(utilisateurDto);
      when(clientDao.existe(clientDto)).thenReturn(clientDto);
      when(utilisateurDao.lierCompteClientUtilisateur(Mockito.any(), Mockito.any()))
          .thenThrow(FatalException.class);
      assertThrows(FatalException.class,
          () -> utilisateurUcc.lierCompteClientUtilisateur("test", "test"));
      verify(dalServices).rollback();
    }

    @Test
    void testLierCompteUtilisateurThrowBizExceptionUtilisateurNull() {
      ClientDto clientDto = getClientDto();
      when(bizFactory.getClient()).thenReturn(clientDto);
      when(clientDao.existe(clientDto)).thenReturn(clientDto);
      when(utilisateurDao.getUtilisateurByMail(Mockito.anyString())).thenReturn(null);
      assertThrows(BizException.class,
          () -> utilisateurUcc.lierCompteClientUtilisateur("test", "test"));
    }
  }

  @Nested
  class TestDefinirCompteOuvrier {
    @Test
    void testDefinirCompteOuvrierCallDefinirOuvrier() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      when(utilisateurDao.getUtilisateurByMail("mail")).thenReturn(utilisateurDto);
      utilisateurUcc.definirCompteOuvrier("mail");
      verify(dalServices).startTransaction();
      verify(utilisateurDao).definirOuvrier(utilisateurDto);
      verify(dalServices).commit();

    }

    @Test
    void testDefinirCompteOuvrierThrowBizException() {
      when(utilisateurDao.getUtilisateurByMail(Mockito.anyString())).thenReturn(null);
      assertThrows(BizException.class, () -> utilisateurUcc.definirCompteOuvrier("test"));
    }

    @Test
    void testDefinirCompteOuvrierCallRollback() {
      UtilisateurDto utilisateurDto = getUtilisateurDto();
      when(utilisateurDao.definirOuvrier(utilisateurDto)).thenThrow(FatalException.class);
      when(utilisateurDao.getUtilisateurByMail(Mockito.anyString())).thenReturn(utilisateurDto);
      assertThrows(FatalException.class, () -> utilisateurUcc.definirCompteOuvrier("test"));
      verify(dalServices).rollback();
    }
  }

  @Nested
  class TestGetUtilisateur {
    @Test
    void testCheckGetUtilisateurPseudo() {
      UtilisateurDto userDto = getUtilisateurDto();

      when(utilisateurDao.getUtilisateur("pseudoIncorrecte")).thenReturn(null);
      when(utilisateurDao.getUtilisateur(USERNAME)).thenReturn(userDto);
      assertNull(utilisateurUcc.getUtilisateur("pseudoIncorrecte"));
      utilisateurUcc.getUtilisateur(USERNAME);
      verify(utilisateurDao).getUtilisateur(USERNAME);
    }

    @Test
    void testCheckGetUtilisateurId() {
      when(utilisateurDao.getUtilisateur(500)).thenReturn(null);
      when(utilisateurDao.getUtilisateur(2)).thenReturn(utilisateurDto);
      assertAll(() -> assertNull(utilisateurUcc.getUtilisateur(500)),
          () -> assertNotNull(utilisateurUcc.getUtilisateur(2)));
    }

    @Test
    void testGetUtilisateurStringCallRollback() {
      when(utilisateurDao.getUtilisateur(Mockito.anyString())).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.getUtilisateur("test"));
      verify(dalServices).rollback();
    }


    @Test
    void testGetUtilisateursReturnListeUtilisateur() {
      ArrayList<UtilisateurDto> utilisateurListe = new ArrayList<UtilisateurDto>();
      HashMap<String, String> map = new HashMap<String, String>();
      when(utilisateurDao.getUtilisateurs(map)).thenReturn(utilisateurListe);
      assertEquals(utilisateurListe, utilisateurUcc.getUtilisateurs(map));
    }

    @Test
    void testGetUtilisateursSansClientReturnListeUtilisateur() {
      ArrayList<UtilisateurDto> utilisateurListe = new ArrayList<UtilisateurDto>();
      when(utilisateurDao.getUtilisateursSansClient()).thenReturn(utilisateurListe);
      assertEquals(utilisateurListe, utilisateurUcc.getUtilisateursSansClient());
    }


    @Test
    void testGetUtilisateurIntCallRollback() {
      when(utilisateurDao.getUtilisateur(Mockito.anyInt())).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.getUtilisateur(1));
      verify(dalServices).rollback();
    }

    @Test
    void testGetUtilisateursCallRollback() {
      HashMap<String, String> map = new HashMap<String, String>();
      when(utilisateurDao.getUtilisateurs(map)).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.getUtilisateurs(map));
      verify(dalServices).rollback();
    }

    @Test
    void testGetUtilisateursThrowBizException() {
      HashMap<String, String> map = new HashMap<String, String>();
      when(utilisateurDao.getUtilisateurs(map)).thenReturn(null);
      assertThrows(BizException.class, () -> utilisateurUcc.getUtilisateurs(map));
    }

    @Test
    void testGetUtilisateursSansClientThrowBizException() {
      when(utilisateurDao.getUtilisateursSansClient()).thenReturn(null);
      assertThrows(BizException.class, () -> utilisateurUcc.getUtilisateursSansClient());
    }

    @Test
    void testGetUtilisateursSansClientCallRollback() {
      when(utilisateurDao.getUtilisateursSansClient()).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.getUtilisateursSansClient());
      verify(dalServices).rollback();
    }

    @Test
    void testGetUtilisateurNonConfirme() {
      ArrayList<UtilisateurDto> utilisateurListe = new ArrayList<UtilisateurDto>();
      when(utilisateurDao.getUtilisateurNonConfirme()).thenReturn(utilisateurListe);
      assertEquals(utilisateurListe, utilisateurUcc.getUtilisateurNonConfirme());
    }

    @Test
    void testGetUtilisateurNonConfirmeCallRollback() {
      when(utilisateurDao.getUtilisateurNonConfirme()).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.getUtilisateurNonConfirme());
      verify(dalServices).rollback();
    }

  }

  @Nested
  @DisplayName("Test de la fonction confirmer compte")
  class TestConfirmerCompte {
    @Test
    void testConfirmerInscription() {
      UtilisateurDto userDto = getUtilisateurDto();
      when(utilisateurDao.confirmerInscription(userDto)).thenReturn(true);
      userDto.setConfirmationInscription(true);
      assertEquals(true, utilisateurUcc.confirmerInscription(userDto));
    }

    @Test
    void testConfirmerInscriptionCallRollback() {
      UtilisateurDto userDto = getUtilisateurDto();
      when(utilisateurDao.confirmerInscription(userDto)).thenThrow(FatalException.class);
      assertThrows(FatalException.class, () -> utilisateurUcc.confirmerInscription(userDto));
      verify(dalServices).rollback();
    }
  }
}
