package be.ipl.pae.dal.dao.client;

import be.ipl.pae.biz.client.ClientDto;

import java.util.ArrayList;
import java.util.Map;

public interface ClientDao {

  ArrayList<ClientDto> getClients(ClientDto clientDto);

  ClientDto inscrire(ClientDto clientDto);

  ArrayList<ClientDto> getClientsCritereRecherche(ClientDto clientDto, Map<String, String> map);

  ClientDto inscrireWithoutBoite(ClientDto clientDto);

  ClientDto existe(ClientDto clientDto);

  ArrayList<ClientDto> getClientsSansUtilisateurs(ClientDto client);



}
