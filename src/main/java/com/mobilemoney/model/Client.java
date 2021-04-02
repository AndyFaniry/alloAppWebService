package com.mobilemoney.model;

import java.util.ArrayList;

import org.bson.Document;
import org.springframework.data.annotation.Id;

import com.mobilemoney.bdb.ConnectionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


public class Client {
	String id;
	String nom;
	String email;
	public Client(String id, String nom, String email) {
		super();
		this.id = id;
		this.nom = nom;
		this.email = email;
	}
	public Client() {}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	/*public Boolean checkClient(int idC) {
    	List<Client> client=this.clientRepository.findAll();
		int size=client.size();
		int i=0;
		boolean val=false;
		while(i<=size) {
			if(Integer.parseInt(client.get(i).id)==idClient) {
				val=true;
				break;
				}
			i++;
			if(i==size) {val=false;}
		}
		return val;
    }*/
	public static ArrayList<Client> getListeClient() throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("client");
		ArrayList<Client> cl=new ArrayList<Client>();
		MongoCursor<Document> curs=coll.find().iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String nom= d.getString("nom");
			String email= d.getString("email");
	        Client c=new Client(id,nom,email);
	        cl.add(c);
	    }
		curs.close();
		return cl;
	}
	public static Client findClientById(int idC) throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("client");
		Client c=null;
		MongoCursor<Document> curs=coll.find(new Document("_id", idC)).iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String nom= d.getString("nom");
			String email= d.getString("email");
	         c=new Client(id,nom,email);
	    }
		curs.close();
		return c;
	}
	public static Client findClient(String nom,String email) throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("client");
		Client c=null;
		MongoCursor<Document> curs=coll.find(new Document("nom", nom).append("email", email)).iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String n= d.getString("nom");
			String em= d.getString("email");
	         c=new Client(id,n,em);
	    }
		curs.close();
		return c;
	}
	public static int countClient()throws Exception{
		return Client.getListeClient().size()+1;
	}
	public static String InsertClient(String nom,String email)throws Exception{
		String val="";
		try {
			MongoDatabase db=new ConnectionMongo().connect();
			MongoCollection<Document> coll=db.getCollection("client");
			Document d=new Document();
			d.put("_id", Client.countClient());
			d.put("nom", nom);
			d.put("email", email);
			coll.insertOne(d);
			val="Insertion reussi";
		}catch(Exception e) {
			val=e.getMessage();
		}
		return val;
	}
}
