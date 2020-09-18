package be.ipl.pae.main.ucc;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.EtatAmenagements;
import be.ipl.pae.biz.devis.DevisDto;
import be.ipl.pae.biz.devis.DevisUccImpl;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.InjectionService;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.devis.DevisDao;
import be.ipl.pae.dal.dao.photo.PhotoDao;
import be.ipl.pae.exception.BizException;
import be.ipl.pae.exception.FatalException;
import be.ipl.pae.exception.ListeDevisNonPresentException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("unchecked")
class DevisUccImplTest {


  @InjectMocks
  private DevisUccImpl devisUcc;

  @Mock
  private BizFactory bizFactory;

  @Mock
  private DevisDao devisDao;

  @Mock
  private PhotoDao photoDao;

  @Mock
  private DalServivesDbManager dalServices;

  @Inject
  private BizFactory realBizFactory;

  private DevisDto devisDto;

  {
    InjectionService.injecter(this, "prod.properties");
    MockitoAnnotations.initMocks(this);
  }

  @BeforeEach
  void setUp() throws Exception {
    devisDto = getDevisDto();
  }

  @Test
  void testGetDevis() throws ListeDevisNonPresentException {

    ArrayList<HashMap<String, String>> listeDevisRealisation =
        new ArrayList<HashMap<String, String>>();
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("test", "test");
    listeDevisRealisation.add(map);

    ArrayList<HashMap<String, String>> listeDevisAll = new ArrayList<HashMap<String, String>>();
    ArrayList<DevisDto> listeDevis = new ArrayList<DevisDto>();
    listeDevis.add(devisDto);

    when(devisDao.getDevis()).thenReturn(listeDevis);
    when(devisDao.getDevis(1)).thenReturn(listeDevisAll);
    when(devisDao.getDevis(Integer.MAX_VALUE - 1))
        .thenReturn(new ArrayList<HashMap<String, String>>());
    when(devisDao.getDevisRealisation(map)).thenReturn(listeDevisRealisation);

    ArrayList<DevisDto> array = new ArrayList<DevisDto>();

    assertAll(() -> assertNotNull(devisUcc.getDevisClients()),
        () -> assertNotNull(devisUcc.getDevisClient(1)),
        () -> assertEquals(listeDevisRealisation, devisUcc.getDevisRealisation(map)),
        () -> assertEquals(array, devisUcc.getDevisClient(Integer.MAX_VALUE - 1)));

  }


  @Test
  void testGetDevisClientsCallRollBack() {
    when(devisDao.getDevis()).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> devisUcc.getDevisClients());
    verify(dalServices).rollback();
  }

  @Test
  void testGetDevisClientCallRollBack() {
    when(devisDao.getDevis(1)).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> devisUcc.getDevisClient(1));
    verify(dalServices).rollback();
  }

  @Test
  void testGetDevisRealisationCallRollBack() {
    HashMap<String, String> map = new HashMap<String, String>();
    map.put("test", "test");
    when(devisDao.getDevisRealisation(map)).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> devisUcc.getDevisRealisation(map));
    verify(dalServices).rollback();
  }

  @Test
  void testIntroduireDevisCallRollBack() {
    when(devisDao.introduireDevis(devisDto)).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> devisUcc.introduireDevis(devisDto, null));
    verify(dalServices).rollback();
  }

  @Test
  void testSetEtatDevis() {
    devisUcc.setEtatDevis(devisDto.getIdDevis(), EtatAmenagements.RV);
    devisUcc.setEtatDevis(devisDto.getIdDevis(), EtatAmenagements.CC);
    devisDao.rendreVisible(devisDto.getIdDevis());
    photoDao.rendreVisiblePhotos(devisDto.getIdDevis());
    devisDao.setEtatDevis(devisDto.getIdDevis(), EtatAmenagements.CC);

    Mockito.doThrow(FatalException.class).when(devisDao).rendreVisible(devisDto.getIdDevis());

    assertThrows(FatalException.class,
        () -> devisUcc.setEtatDevis(devisDto.getIdDevis(), EtatAmenagements.RV));
    verify(dalServices).rollback();

  }

  @Test
  void setDateDebutTravaux() {
    devisDto.setIdDevis(1);
    when(devisDao.getDevisById(devisDto.getIdDevis())).thenReturn(devisDto);

    assertThrows(BizException.class,
        () -> devisUcc.setDateDebutTravaux(LocalDate.parse("2221-12-12"), devisDto.getIdDevis()));
    when(devisDao.setDateDebutTravaux(LocalDate.parse("2224-12-12"), devisDto.getIdDevis()))
        .thenReturn(true);
    assertTrue(devisUcc.setDateDebutTravaux(LocalDate.parse("2224-12-12"), devisDto.getIdDevis()));

    when(devisDao.getDevisById(devisDto.getIdDevis())).thenThrow(FatalException.class);
    assertThrows(FatalException.class,
        () -> devisUcc.setDateDebutTravaux(null, devisDto.getIdDevis()));
    verify(dalServices).rollback();

  }

  @Test
  void introduireDevis() {
    devisDto.setMontantTotal(-1);
    assertThrows(BizException.class, () -> devisUcc.introduireDevis(devisDto, getTabAmenagement()));
    devisDto.setMontantTotal(15);

    devisDto.setDureeTravaux(0);
    assertThrows(BizException.class, () -> devisUcc.introduireDevis(devisDto, getTabAmenagement()));
    devisDto.setDureeTravaux(5);

    when(devisDao.introduireDevis(devisDto)).thenReturn(null);
    assertThrows(BizException.class, () -> devisUcc.introduireDevis(devisDto, getTabAmenagement()));

    when(devisDao.introduireDevis(devisDto)).thenReturn(devisDto);

    when(devisDao.introduireListeAmenagement(devisDto, getTabAmenagement())).thenReturn(false);
    assertThrows(BizException.class, () -> devisUcc.introduireDevis(devisDto, getTabAmenagement()));

    when(devisDao.introduireListeAmenagement(devisDto, getTabAmenagement())).thenReturn(true);

    assertEquals(devisDto, devisUcc.introduireDevis(devisDto, getTabAmenagement()));

  }

  @Test
  void setPhotoPreferee() {
    devisUcc.setPhotoPreferee(1, devisDto.getIdDevis());
    Mockito.doThrow(FatalException.class).when(devisDao).setPhotoPreferee(1, devisDto.getIdDevis());
    assertThrows(FatalException.class, () -> devisUcc.setPhotoPreferee(1, devisDto.getIdDevis()));
    verify(dalServices).rollback();
  }

  @Test
  void getVisualisationDevis() {
    devisUcc.getVisualisationDevis(devisDto.getIdDevis());
    when(devisDao.getVisualisationDevis(devisDto.getIdDevis()))
        .thenReturn(new HashMap<String, String>());
    devisUcc.getVisualisationDevis(devisDto.getIdDevis());

    when(devisDao.getVisualisationDevis(devisDto.getIdDevis())).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> devisUcc.getVisualisationDevis(devisDto.getIdDevis()));
  }


  private DevisDto getDevisDto() {
    DevisDto devis = realBizFactory.getDevis();
    devis.setDateDevis(LocalDate.parse("2222-12-12"));
    devis.setMontantTotal(155);
    devis.setDureeTravaux(15);
    return devis;
  }

  private int[] getTabAmenagement() {
    int[] array = {1, 2, 3};
    return array;
  }


}
