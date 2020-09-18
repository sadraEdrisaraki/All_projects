package be.ipl.pae.main.ucc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.photo.PhotoDto;
import be.ipl.pae.biz.photo.PhotoUccImpl;
import be.ipl.pae.config.Inject;
import be.ipl.pae.config.InjectionService;
import be.ipl.pae.dal.DalServivesDbManager;
import be.ipl.pae.dal.dao.photo.PhotoDao;
import be.ipl.pae.exception.FatalException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

class PhotoUccImplTest {

  @InjectMocks
  private PhotoUccImpl photoUccImpl;

  @Mock
  private PhotoDao photoDao;

  @Mock
  private DalServivesDbManager dalServices;

  @Inject
  private BizFactory realBizFactory;

  private PhotoDto photoDto;

  private static final int ID_DEVIS = 1;
  private static final int ID_AMENAGEMENT = 1;

  {
    InjectionService.injecter(this, "prod.properties");
    MockitoAnnotations.initMocks(this);
  }


  @BeforeEach
  void setUp() throws Exception {
    photoDto = realBizFactory.getPhoto();
  }

  @Test
  void testEnregistrerPhoto() {
    photoUccImpl.enregistrerPhoto(photoDto);
    verify(photoDao).enregistrerPhoto(photoDto);
    verify(dalServices).commit();

  }

  @Test
  void testEnregistrerPhotoCallRollback() {
    Mockito.doThrow(FatalException.class).when(photoDao).enregistrerPhoto(photoDto);
    assertThrows(FatalException.class, () -> photoUccImpl.enregistrerPhoto(photoDto));
    verify(dalServices).rollback();
  }

  @Test
  void testGetPhotosVisible() {
    ArrayList<PhotoDto> photos = new ArrayList<PhotoDto>();
    when(photoDao.getPhotosVisible()).thenReturn(photos);
    assertEquals(photos, photoUccImpl.getPhotosVisible());
  }

  @Test
  void testGetPhotoVisibleRollback() {
    when(photoDao.getPhotosVisible()).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> photoUccImpl.getPhotosVisible());
    verify(dalServices).rollback();
  }

  @Test
  void testGetPhotosDevis() {
    ArrayList<PhotoDto> photos = new ArrayList<PhotoDto>();
    when(photoDao.getPhotosDevis(ID_DEVIS)).thenReturn(photos);
    assertEquals(photos, photoUccImpl.getPhotosDevis(ID_DEVIS));
  }

  @Test
  void testGetPhotosDevisRollback() {
    when(photoDao.getPhotosDevis(ID_DEVIS)).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> photoUccImpl.getPhotosDevis(ID_DEVIS));
    verify(dalServices).rollback();
  }

  @Test
  void testgetPhotosParAmenagement() {
    ArrayList<PhotoDto> photos = new ArrayList<PhotoDto>();
    when(photoDao.getPhotoParAmenagement(ID_AMENAGEMENT)).thenReturn(photos);
    assertEquals(photos, photoUccImpl.getPhotosParAmenagement(ID_AMENAGEMENT));
  }

  @Test
  void testgetPhotosParAmenagementRollback() {
    when(photoDao.getPhotoParAmenagement(ID_AMENAGEMENT)).thenThrow(FatalException.class);
    assertThrows(FatalException.class, () -> photoUccImpl.getPhotosParAmenagement(ID_AMENAGEMENT));
    verify(dalServices).rollback();
  }

}
