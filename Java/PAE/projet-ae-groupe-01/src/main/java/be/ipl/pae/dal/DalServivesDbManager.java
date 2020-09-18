package be.ipl.pae.dal;

public interface DalServivesDbManager {

  void commit();

  void rollback();

  void startTransaction();
}
