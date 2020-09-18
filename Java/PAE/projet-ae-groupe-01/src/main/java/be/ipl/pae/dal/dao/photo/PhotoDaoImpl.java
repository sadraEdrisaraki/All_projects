package be.ipl.pae.dal.dao.photo;

import be.ipl.pae.biz.BizFactory;
import be.ipl.pae.biz.photo.PhotoDto;
import be.ipl.pae.config.Inject;
import be.ipl.pae.dal.DalServices;
import be.ipl.pae.exception.FatalException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class PhotoDaoImpl implements PhotoDao {

  @Inject
  private DalServices dalS;
  @Inject
  private BizFactory bizFactory;

  public PhotoDaoImpl() {}


  @Override
  public PhotoDto enregistrerPhoto(PhotoDto photo) {
    PreparedStatement ps = dalS.getPreparedStatement(
        "INSERT INTO photos (id_amenagement,id_devis,visibilite,date_photo,url_photo)"
            + " VALUES(?,?,?,?,?) RETURNING id_photo");
    try {
      System.out.println("Date photo DAO " + photo.getDate_photo());
      ps.setInt(1, photo.getId_amenagement());
      ps.setInt(2, photo.getId_devis());
      ps.setBoolean(3, photo.isVisible());
      ps.setDate(4, java.sql.Date.valueOf(photo.getDate_photo()));
      ps.setString(5, photo.getPhoto());
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      if (res.next()) {
        photo.setId_photo(res.getInt(1));
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return photo;
  }

  @Override
  public ArrayList<PhotoDto> getPhotosVisible() {
    PreparedStatement ps =
        dalS.getPreparedStatement("SELECT * FROM photos p WHERE p.visibilite = true");
    ArrayList<PhotoDto> photos = new ArrayList<PhotoDto>();
    try (ResultSet res = ps.executeQuery()) {
      while (res.next()) {
        PhotoDto photoDto = getPhotoFromRes(res);
        photos.add(photoDto);
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return photos;
  }


  @Override
  public void rendreVisiblePhotos(int idDevis) {
    PreparedStatement ps =
        dalS.getPreparedStatement("UPDATE photos p SET visibilite = true WHERE id_devis = ?");
    try {
      ps.setInt(1, idDevis);
      ps.executeUpdate();
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
  }


  @Override
  public ArrayList<PhotoDto> getPhotosDevis(int idDevis) {
    PreparedStatement ps = dalS.getPreparedStatement("SELECT * FROM photos p WHERE p.id_devis = ?");
    ArrayList<PhotoDto> photos = new ArrayList<PhotoDto>();

    try {
      ps.setInt(1, idDevis);
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }

    try (ResultSet res = ps.executeQuery()) {
      while (res.next()) {
        PhotoDto photoDto = getPhotoFromRes(res);
        photos.add(photoDto);
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return photos;
  }


  private PhotoDto getPhotoFromRes(ResultSet res) throws SQLException {
    PhotoDto photoDto = bizFactory.getPhoto();
    photoDto.setId_photo(res.getInt(1));
    photoDto.setId_amenagement(res.getInt(2));
    photoDto.setId_devis(res.getInt(3));
    photoDto.setVisible(res.getBoolean(4));
    photoDto.setDate_photo(res.getDate(5).toLocalDate());
    photoDto.setPhoto(res.getString(6));
    return photoDto;
  }

  @Override
  public ArrayList<PhotoDto> getPhotoParAmenagement(int idAmenagement) {

    PreparedStatement ps = dalS.getPreparedStatement(
        "SELECT * FROM photos p WHERE p.visibilite = true AND id_amenagement = ?");
    ArrayList<PhotoDto> photos = new ArrayList<PhotoDto>();
    try {
      ps.setInt(1, idAmenagement);
      ResultSet res = ps.executeQuery();
      while (res.next()) {
        PhotoDto photoDto = bizFactory.getPhoto();
        photoDto.setId_photo(res.getInt(1));
        photoDto.setId_amenagement(res.getInt(2));
        photoDto.setId_photo(res.getInt(3));
        photoDto.setVisible(res.getBoolean(4));
        photoDto.setDate_photo(res.getDate(5).toLocalDate());
        photoDto.setPhoto(res.getString(6));
        photos.add(photoDto);
      }
    } catch (SQLException exception) {
      throw new FatalException(exception);
    }
    return photos;
  }
}
