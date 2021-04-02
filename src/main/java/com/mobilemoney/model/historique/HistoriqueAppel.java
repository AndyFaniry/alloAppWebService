package com.mobilemoney.model.historique;

import java.util.ArrayList;

import org.bson.Document;

import com.mobilemoney.bdb.ConnectionMongo;
import com.mobilemoney.model.Client;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class HistoriqueAppel {
	String id;
	String num;
	String numAppele;
	String daty;
	int duree;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getNumAppele() {
		return numAppele;
	}
	public void setNumAppele(String numAppele) {
		this.numAppele = numAppele;
	}
	public String getDaty() {
		return daty;
	}
	public void setDaty(String daty) {
		this.daty = daty;
	}
	public int getDuree() {
		return duree;
	}
	public void setDuree(int duree) {
		this.duree = duree;
	}
	public HistoriqueAppel(String id, String num, String numAppele, String daty, int duree) {
		super();
		this.id = id;
		this.num = num;
		this.numAppele = numAppele;
		this.daty = daty;
		this.duree = duree;
	}
	public static ArrayList<HistoriqueAppel> getHistoriqueAppel() throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("histoAppel");
		ArrayList<HistoriqueAppel> cl=new ArrayList<HistoriqueAppel>();
		MongoCursor<Document> curs=coll.find().iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String num= d.getString("num");
			String numAppel= d.getString("numAppele");
			String daty=d.getString("daty");
			int duree=d.getInteger("duree");
	        HistoriqueAppel app=new HistoriqueAppel(id,num,numAppel,daty,duree);
	        cl.add(app);
	    }
		curs.close();
		return cl;
	}
	public static HistoriqueAppel findHistoriqueAppelById(int idC) throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("histoAppel");
		HistoriqueAppel c=null;
		MongoCursor<Document> curs=coll.find(new Document("_id", idC)).iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String num= d.getString("num");
			String numAppel= d.getString("numAppele");
			String daty=d.getString("daty");
			int duree=d.getInteger("duree");
	        c=new HistoriqueAppel(id,num,numAppel,daty,duree);
	    }
		curs.close();
		return c;
	}
	public static ArrayList<HistoriqueAppel> findHistoriqueAppelClient(String num) throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("histoAppel");
		ArrayList<HistoriqueAppel> h=new ArrayList<HistoriqueAppel>();
		MongoCursor<Document> curs=coll.find(new Document("num", num)).iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String nume= d.getString("num");
			String numAppel= d.getString("numAppele");
			String daty=d.getString("daty");
			int duree=d.getInteger("duree");
	        HistoriqueAppel app=new HistoriqueAppel(id,nume,numAppel,daty,duree);
	        h.add(app);
	    }
		curs.close();
		return h;
	}
	public static int countHisto()throws Exception{
		return HistoriqueAppel.getHistoriqueAppel().size()+1;
	}
	public static String insertHistoriqueAppel(String num,String numAppel,String daty,String duree)throws Exception{
		String val="";
		try {
			MongoDatabase db=new ConnectionMongo().connect();
			MongoCollection<Document> coll=db.getCollection("histoAppel");
			Document d=new Document();
			d.put("_id", HistoriqueAppel.countHisto());
			d.put("num", num);
			d.put("numAppele", numAppel);
			d.put("daty",daty);
			d.put("duree", duree);
			coll.insertOne(d);
			val="Insertion reussi";
		}catch(Exception e) {
			val=e.getMessage();
		}
		return val;
	}
}
