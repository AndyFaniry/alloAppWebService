package com.mobilemoney.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.mobilemoney.model.Client;
import com.mobilemoney.model.ClientRepository;
import com.mobilemoney.model.Response;


@CrossOrigin(origins="*",allowedHeaders="*")
@RestController
public class ClientController {
	@Autowired
	public ClientRepository clientRepository;
	
	@GetMapping("/client/findall")
	public Response getAllClient() throws Exception{
		Response reponse= new Response();
		reponse.data=Client.getListeClient();
		reponse.message= null;
		reponse.code= "200" ;
		return reponse;
	}
	@GetMapping("/client/getclient/{id}")
	public Response getClient(@PathVariable String id) throws Exception{
		Response reponse= new Response();
		reponse.data=Client.findClientById(Integer.parseInt(id));
		reponse.message= null;
		reponse.code= "200" ;
		return reponse;
	}
	@PostMapping(value="/client/create")
	public Response createClient(@RequestBody Map<String,String> nom,@RequestBody Map<String,String> email) throws Exception {
		Response reponse= new Response();
		String insert=Client.InsertClient(nom.get("nom"), email.get("email"));
		reponse.data= null;
		reponse.message= insert;
		reponse.code= "200" ;
		return reponse;
	}
} 
