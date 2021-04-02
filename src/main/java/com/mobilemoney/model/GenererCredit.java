package com.mobilemoney.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.model.mouvement.MouvementMobileMoney;

public class GenererCredit {
	int idCode;
	int idOperateur;
	String code;
	int valeur;
	int statu;
	public int getIdCode() {
		return idCode;
	}
	public void setIdCode(int idCode) {
		this.idCode = idCode;
	}
	public int getIdOperateur() {
		return idOperateur;
	}
	public void setIdOperateur(int idOperateur) {
		this.idOperateur = idOperateur;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getValeur() {
		return valeur;
	}
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}
	
	public int getstatu() {
		return statu;
	}
	public void setstatu(int statu) {
		this.statu = statu;
	}
	public GenererCredit(int idCode,int idOperateur,String code, int valeur,int statu) {
		super();
		this.idCode = idCode;
		this.idOperateur=idOperateur;
		this.code = code;
		this.valeur = valeur;
		this.statu=statu;
	}
	public GenererCredit(int valeur) {
		this.valeur=valeur;
	}
	public static ArrayList<GenererCredit> findAllCredit(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet resultSet = null;
		ArrayList<GenererCredit> crd=new ArrayList<GenererCredit>();
		try {
			st = co.prepareStatement(sql);
			resultSet = st.executeQuery();
			while (resultSet.next()) {
				int idC=resultSet.getInt("idCode");
				int idO=resultSet.getInt("idOperateur");
				String code=resultSet.getString("code");
				int valeur=resultSet.getInt("valeur");
				int statu=resultSet.getInt("statu");
				GenererCredit gcr=new GenererCredit(idC,idO,code,valeur,statu);
				crd.add(gcr);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st != null) st.close();
		}
		return crd;
    }
	public static ArrayList<GenererCredit> getListeCreditValide(String id,Connection co) throws Exception{
		String sql="select * from creditOperateur where statu=1 and idoperateur="+id;
		System.out.println(sql);
		ArrayList<GenererCredit> cred=GenererCredit.findAllCredit(sql, co);
		return cred;
	}
	public static GenererCredit getCreditCode(String code,Connection co) throws Exception{
		String sql="select * from creditOperateur where statu=1 and code="+code;
		System.out.println(sql);
		ArrayList<GenererCredit> cred=GenererCredit.findAllCredit(sql, co);
		if(cred==null)throw new Exception("Code invalide");
		return cred.get(0);
	}
	public static String genererCode() {
		String val="";
		int i=0;
		Random	rand=new Random();
		while(i<14){
			val=val+rand.nextInt(10);
			i++;
		}
		return val;
	}
	public void insert(Connection co)throws Exception{
		PreparedStatement st = null;
		try {
				String sql= "insert into creditOperateur(idCode,idoperateur,code,valeur,statu) VALUES (nextval('seqCreditOp'),?,?,?,1)";
				st = co.prepareStatement(sql);
				st.setInt(1,this.getIdOperateur());
				st.setString(2, this.getCode());
				st.setInt(3,this.getValeur());
				st.execute();
				co.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
	}
	public static Response listeCredit(String token) throws Exception {
		Connection co=new ConnectionPstg().getConnection();
		Response res=new Response();
		try {
			String idOperateur=Token.verificationTokenAdmin(token, co);
			res.data= GenererCredit.getListeCreditValide(idOperateur, co);
			res.message= "Credit valide";
			res.code="200";
		} catch (Exception e) {
			res.code="400";
			res.message= e.getMessage();
		} finally {
			if(co != null) co.close();
		}
		return res;
	}
	public Response insertCredit(String token) throws Exception {
		Connection co=new ConnectionPstg().getConnection();
		Response res=new Response();
		try {
			this.idOperateur=Integer.parseInt(Token.verificationTokenAdmin(token, co));
			this.setCode(GenererCredit.genererCode());
			this.insert(co);
			res.data= this;
			res.message= "insertion reussi";
			res.code="200";
		} catch (Exception e) {
			res.code="400";
			res.message= e.getMessage();
		} finally {
			if(co != null) co.close();
		}
		return res;
	}
	public static Response deleteCreditGenerer(String idCode) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		int idOffre1= Integer.parseInt(idCode);
		PreparedStatement st = null;
		try {
			String sql= " delete from creditOperateur where idCode=?";
			st = co.prepareStatement(sql);
			st.setInt(1,idOffre1);
			st.execute();
			co.commit();
			r.data= null ;
			r.message= "Credit supprimer";
			
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
	public static void updateCredit(String idCode, String idOperateur,String code,String valeur,String statu,Connection co) throws Exception {
		PreparedStatement st = null;
		int idC=Integer.parseInt(idCode);
		int idO=Integer.parseInt(idOperateur);
		int val=Integer.parseInt(valeur);
		int stat=Integer.parseInt(statu);
		try {
			String sql= "update crediOperateur set idOperateur=?,code=?,valeur=?,statu=? where idcode=?";
			st = co.prepareStatement(sql);
			st.setInt(1, idO);
			st.setString(2, code);
			st.setInt(3, val);
			st.setInt(4, stat);
			st.setInt(5, idC);
			st.execute();
			co.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
	}
	public static Response updateCredits(String idCo,String ido,String code,String valeur,String statu) throws Exception {
		Connection co=new ConnectionPstg().getConnection();
		Response res=new Response();
		try {
			GenererCredit.updateCredit(idCo, ido, code, valeur, statu,co);
			res.data= null;
			res.message= "insertion reussi";
			res.code="200";
		} catch (Exception e) {
			res.code="400";
			res.message= e.getMessage();
		} finally {
			if(co != null) co.close();
		}
		return res;
	}
}
