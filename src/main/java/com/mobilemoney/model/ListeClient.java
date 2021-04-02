package com.mobilemoney.model;

import java.util.List;


public interface ListeClient extends ClientRepository{
	public static final ClientRepository clientRepository = null;

	public static List<Client> getAllClient(){
		return   clientRepository.findAll();
	}

}
