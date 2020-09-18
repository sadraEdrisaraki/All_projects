package be.ipl.pae.main.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.amenagement.TypeAmenagementDto;
import be.ipl.pae.biz.amenagement.TypeAmenagementUccImpl;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.InjectionService;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.amenagement.TypeAmenagementDao;
import be.ipl.pae.exception.FatalException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

public class TypeAmenagementUccTest {


  @Inject
  private BizFactory bizFactory;

  @InjectMocks
  private TypeAmenagementUccImpl typeAmenagementUcc;

  @Mock
  private TypeAmenagementDao typeAmenagementDao;

  @Mock
  private DalServivesDbManager dalServices;

  private TypeAmenagementDto typeAmenagementDto;

  private ArrayList<TypeAmenagementDto> listeAmenagements;

  private static final String AMENAGEMENT_NOM = "testAmenagement";

  {
    InjectionService.injecter(this, "prod.properties");
    MockitoAnnotations.initMocks(this);
  }

  @BeforeEach
  void setUp() throws Exception {

    typeAmenagementDto = bizFactory.getTypeAmenagement();

    listeAmenagements = new ArrayList<TypeAmenagementDto>();
    listeAmenagements.add(typeAmenagementDto);

    when(typeAmenagementDao.getListeTypeAmenagement()).thenReturn(listeAmenagements);

  }

  @Test
  void testGetAmenagements() {
    assertEquals(listeAmenagements, typeAmenagementUcc.getListeAmenagement());
  }

  @Test
  void testGetAmenagementsCommitCalled() {
    typeAmenagementUcc.getListeAmenagement();
    verify(dalServices).commit();
  }

  @Test
  void testGetAmenagementsRollbackCalled() {
    when(typeAmenagementDao.getListeTypeAmenagement()).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> typeAmenagementUcc.getListeAmenagement());
    verify(dalServices).rollback();
  }

  @Test
  void testGetAmenagementsThrowFatalException() {
    when(typeAmenagementUcc.getListeAmenagement()).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> typeAmenagementUcc.getListeAmenagement());
  }

  @Test
  void testIntroduireAmenagement() {
    when(typeAmenagementDao.introduireAmenagement(AMENAGEMENT_NOM)).thenReturn(true);
    assertEquals(true, typeAmenagementUcc.introduireAmenagement(AMENAGEMENT_NOM));
  }

  @Test
  void testIntroduireAmenagementThrowFatalException() {
    when(typeAmenagementDao.introduireAmenagement(AMENAGEMENT_NOM)).thenThrow(FatalException.class);
    assertThrows(FatalException.class,
        () -> typeAmenagementUcc.introduireAmenagement(AMENAGEMENT_NOM));
  }

  @Test
  void testIntroduireAmenagementRollbackCalled() {
    when(typeAmenagementDao.introduireAmenagement(AMENAGEMENT_NOM)).thenThrow(FatalException.class);
    assertThrows(FatalException.class,
        () -> typeAmenagementUcc.introduireAmenagement(AMENAGEMENT_NOM));
    verify(dalServices).rollback();
  }

}
