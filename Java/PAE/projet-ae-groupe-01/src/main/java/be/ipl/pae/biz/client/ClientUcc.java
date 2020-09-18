package be.ipl.pae.biz.client;

import java.util.ArrayList;
import java.util.Map;

public interface ClientUcc {

  ArrayList<ClientDto> getClients();

  ArrayList<ClientDto> getClientsCritereRecherche(Map<String, String> map);


  ClientDto inscrire(ClientDto clientDto);

  ArrayList<ClientDto> getClientsSansUtilisateurs();

  ClientDto existe(ClientDto clientDto);
}
