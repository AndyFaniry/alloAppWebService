package com.mobilemoney.model.historique;

import java.util.ArrayList;

import org.bson.Document;

import com.mobilemoney.bdb.ConnectionMongo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class HistoriqueSms {
	String id;
	String num;
	String numVers;
	String daty;
	String msg ;
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
	public String getNumVers() {
		return numVers;
	}
	public void setNumVers(String numvers) {
		this.numVers = numVers;
	}
	public String getDaty() {
		return daty;
	}
	public void setDaty(String daty) {
		this.daty = daty;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public HistoriqueSms(String id, String num, String numvers, String daty, String msg) {
		super();
		this.id = id;
		this.num = num;
		this.numVers = numvers;
		this.daty = daty;
		this.msg = msg;
	}
	public static ArrayList<HistoriqueSms> getHistoriqueSms() throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("histoSms");
		ArrayList<HistoriqueSms> cl=new ArrayList<HistoriqueSms>();
		MongoCursor<Document> curs=coll.find().iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String num= d.getString("num");
			String numAppel= d.getString("numVers");
			String daty=d.getString("daty");
			String msg=d.getString("msg");
	       HistoriqueSms sms=new HistoriqueSms(id, numAppel, num, daty, msg);
	        cl.add(sms);
	    }
		curs.close();
		return cl;
	}
	public static HistoriqueSms findHistoriqueSmsById(int idC) throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("histoSms");
		HistoriqueSms c=null;
		MongoCursor<Document> curs=coll.find(new Document("_id", idC)).iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String num= d.getString("num");
			String numAppel= d.getString("numVers");
			String daty=d.getString("daty");
			String msg=d.getString("msg");
	        c=new HistoriqueSms(id, numAppel, num, daty, msg);
	    }
		curs.close();
		return c;
	}
	public static ArrayList<HistoriqueSms> findHistoriqueSmsClient(String num) throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("histoSms");
		ArrayList<HistoriqueSms> h=new ArrayList<HistoriqueSms>();
		MongoCursor<Document> curs=coll.find(new Document("num", num)).iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String nume= d.getString("num");
			String numAppel= d.getString("numVers");
			String daty=d.getString("daty");
			String msg=d.getString("msg");
	        HistoriqueSms sms=new HistoriqueSms(id, numAppel, nume, daty, msg);
	        h.add(sms);
	    }
		curs.close();
		return h;
	}
	public static ArrayList<HistoriqueSms> findRecuSmsClient(String num) throws Exception{
		MongoDatabase db=new ConnectionMongo().connect();
		MongoCollection<Document> coll=db.getCollection("histoSms");
		ArrayList<HistoriqueSms> h=new ArrayList<HistoriqueSms>();
		MongoCursor<Document> curs=coll.find(new Document("numVers", num)).iterator();
		while(curs.hasNext())
	    {
			Document d=curs.next();
			String id= d.getInteger("_id").toString();
			String nume= d.getString("num");
			String numAppel= d.getString("numVers");
			String daty=d.getString("daty");
			String msg=d.getString("msg");
	        HistoriqueSms sms=new HistoriqueSms(id, numAppel, nume, daty, msg);
	        h.add(sms);
	    }
		curs.close();
		return h;
	}
	public static int countHisto()throws Exception{
		return HistoriqueSms.getHistoriqueSms().size()+1;
	}
	public static String insertHistoriqueSms(String num,String numVers,String daty,String msg)throws Exception{
		String val="";
		try {
			MongoDatabase db=new ConnectionMongo().connect();
			MongoCollection<Document> coll=db.getCollection("histoSms");
			Document d=new Document();
			d.put("_id", HistoriqueAppel.countHisto());
			d.put("num", num);
			d.put("numVers", numVers);
			d.put("daty",daty);
			d.put("msg", msg);
			coll.insertOne(d);
			val="Insertion reussi";
		}catch(Exception e) {
			val=e.getMessage();
		}
		return val;
	}
}
