package com.mobilemoney.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.model.GenererCredit;

public class Credit {
	int idCredit;
	int idCompte;
	String code;
	int valeur;
	LocalDateTime daty;

	public int getIdCredit() {
		return idCredit;
	}
	public void setIdCredit(int idCredit) {
		this.idCredit = idCredit;
	}
	public int getIdCompte() {
		return idCompte;
	}
	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) throws Exception {
		this.code = code;
	}
	public int getValeur() {
		return valeur;
	}
	public void setValeur(int valeur) throws Exception {
		this.valeur = valeur;
	}
	public LocalDateTime getDaty() {
		return daty;
	}
	public void setDaty(String daty) {
		this.daty = LocalDateTime.parse(daty);
	}
	public void setDaty(LocalDateTime daty) {
		this.daty = daty;
	}
	public Credit()throws Exception  {}

	public Credit(int idCredit,int idCompte, String code, int valeur, LocalDateTime daty) throws Exception{
		super();
		setIdCredit(idCredit);
		setIdCompte(idCompte);
		setCode(code);
		setValeur(valeur);
		setDaty(daty);
	}
	public Credit(int idCompte, String code, int valeur, LocalDateTime daty) throws Exception{
		super();
		setIdCompte(idCompte);
		setCode(code);
		setValeur(valeur);
		setDaty(daty);
	}	
	public static void insertMouvementCredit(int idCompte,String code, String valeur,Connection co) throws Exception {
		PreparedStatement st = null;
		try {
			int val= Integer.parseInt(valeur);
				String sql= "insert into credit(idCredit,idCompte,code,valeur,daty)VALUES(nextval('seqCredit'),?,?,?,CURRENT_TIMESTAMP);";
				st = co.prepareStatement(sql);
				st.setInt(1,idCompte);
				st.setString(2,code);
				st.setInt(3,val);
				st.execute();
				co.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
	}
	public static ArrayList<Credit> findAllCredit(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet resultSet = null;
		ArrayList<Credit> crd=new ArrayList<Credit>();
		try {
			st = co.prepareStatement(sql);
			resultSet = st.executeQuery();
			while (resultSet.next()) {
				int idCredit=resultSet.getInt("idCredit");
				int idCompte=resultSet.getInt("idCompte");
				String code= resultSet.getString("code");
				int valeur=resultSet.getInt("valeur");
				LocalDateTime daty= resultSet.getTimestamp("daty").toLocalDateTime();
				Credit credit=new Credit(idCredit,idCompte,code,valeur,daty);
				crd.add(credit);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st != null) st.close();
		}
		return crd;
    }
	public static Credit getSoldeCredit(int idCompte1,Connection co) throws Exception{
		String sql="select * from v_credit where idCompte="+idCompte1;
		PreparedStatement st = null;
		ResultSet resultSet = null;
		ArrayList<Credit> crd=new ArrayList<Credit>();
		try {
			st = co.prepareStatement(sql);
			resultSet = st.executeQuery();
			while (resultSet.next()) {
				int idCompte=resultSet.getInt("idCompte");
				int valeur=resultSet.getInt("valeur");
				LocalDateTime daty= resultSet.getTimestamp("daty").toLocalDateTime();
				String code="Solde";
				Credit credit=new Credit(idCompte,code,valeur,daty);
				crd.add(credit);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st != null) st.close();
		}
		return crd.get(0);
    }
	public static Response getSoldeWebService(String token) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		int idCompte= Token.verificationToken(token,co);
		PreparedStatement st = null;
		try {
			r.data= Credit.getSoldeCredit(idCompte,co);
			r.message= "votre solde";
			
		} catch (Exception e) {
			r.code= "400";
			r.message= e.getMessage();
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
			if(co!=null) co.close();
		}
		return  r;
	}
	public static Response ajoutCreditWebService(String code,String token) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			int idCompte= Token.verificationToken(token,co);
			int valeur=GenererCredit.getCreditCode(code,co).getValeur();
			if(code.length()!=14) throw new Exception("code invalide");
			insertMouvementCredit(idCompte,code,String.valueOf(valeur),co);
			Credit crd= Credit.getSoldeCredit(idCompte,co);
			reponse.data= crd;
			reponse.message= valeur +"Ar de credit ajouter";
			reponse.code="200";
		}
		catch(Exception ex) {
			reponse.code="400";
			reponse.message= ex.getMessage();
		}
		finally {
			if(co != null) co.close();
		}
		return reponse;
	}

}
