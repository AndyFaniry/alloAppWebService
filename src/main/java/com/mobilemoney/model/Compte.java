package com.mobilemoney.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.fonction.Fonction;
import com.mobilemoney.model.mouvement.MouvementMobileMoney;

public class Compte {
	int idCompte;
	int idClient;
	int idOperateur;
	String num;
	String mdp;
	
	public int getIdOperateur() {
		return idOperateur;
	}
	public void setIdOperateur(int idOperateur) {
		this.idOperateur = idOperateur;
	}
	public int getIdCompte() {
		return idCompte;
	}
	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}
	public int getIdClient() {
		return idClient;
	}
	public void setIdClient(int idClient) {
		this.idClient = idClient;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	public Compte() {}
	public Compte(int idCompte, int idClient,int idOperateur, String num, String mdp) {
		setIdCompte(idCompte);
		setIdClient(idClient);
		setIdOperateur(idOperateur);
		setNum(num);
		setMdp(mdp);
	}
	public Compte(int idClient,int idOperateur, String num, String mdp) {
		setIdClient(idClient);
		setIdOperateur(idOperateur);
		setNum(num);
		setMdp(mdp);
	}
	public Compte(int idOperateur, String mdp) {
		setIdOperateur(idOperateur);
		setMdp(mdp);
	}
	public static ArrayList<Compte> findAllCompte(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet result = null;
		ArrayList<Compte> array = new ArrayList<Compte>();
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				int id=result.getInt("idcompte");
				int idC=result.getInt("idclient");
				int idOp=result.getInt("idoperateur");
				String num=result.getString("num");
				String mdp=result.getString("mdp");
				Compte c=new Compte(id,idC,idOp,num,mdp);
				System.out.println(c.getNum());
				array.add(c);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return array;
    }
	public static Compte valideLogin(String num, String mdp, Connection co) throws Exception {
		String sql= "select * from Compte where num='"+num+"' and mdp=md5('@client123"+mdp+"')";
		ArrayList<Compte> comptes= Compte.findAllCompte(sql, co);
		if(comptes.size()!=1) throw new Exception("mot de passe ou numero non valide");
		return comptes.get(0);
	}
	public static Compte findCompteById(int id,Connection co) throws Exception {
		String sql= "select * from Compte where idcompte="+id;
		ArrayList<Compte> comptes= Compte.findAllCompte(sql, co);
		if(comptes.size()!=1) throw new Exception("compte invalide");
		return comptes.get(0);
	}
	public static int findLastIdCompte(Connection co) throws Exception {
		PreparedStatement st = null;
		ResultSet result = null;
		int id=0;
		String sql="select count(idCompte) as id from compte";
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				id=result.getInt("id");
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return id;
	}
	public static Response getCompte(String token) throws Exception {
		Connection co=new ConnectionPstg().getConnection();
		int id= Token.verificationToken(token,co);
		Response res=new Response();
		try {
			res.data= Compte.findCompteById(id, co);
			res.message= "compte";
			res.code="200";
		} catch (Exception e) {
			res.code="400";
			res.message= e.getMessage();
		} finally {
			if(co != null) co.close();
		}
		return res;
	}
	public void insert(Connection co)throws Exception{
		PreparedStatement st = null;
		try {
				String sql= "insert into compte(idCompte,idClient,idoperateur,num,mdp) VALUES (nextval('seqCompte'),?,?,?,md5(?))";
				st = co.prepareStatement(sql);
				st.setInt(1,this.getIdClient());
				st.setInt(2, this.getIdOperateur());
				st.setString(3,this.getNum());
				st.setString(4,"@client123"+this.getMdp());
				st.execute();
				int idCompte1= Compte.findLastIdCompte(co) + 1;
				Credit.insertMouvementCredit(idCompte1,"**************","0",co);
				MouvementMobileMoney.insertMouvementDebut(idCompte1,"0",co);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
	}
	public static String genererNum() {
		String val="";
		int i=0;
		Random	rand=new Random();
		while(i<7) {
			val=val+rand.nextInt(10);
			i++;
		}
		return val;
	}
	public Response insertCompte(String ido,String nm,String mail) throws Exception {
		Connection co=new ConnectionPstg().getConnection();
		Response res=new Response();
		try {
			Client cl=Client.findClient(nm, mail);
			Operateur op=Operateur.findOperateurById(ido, co);
			String num= Compte.genererNum()+op.getPrefixe();
			if(cl!=null) {
				this.setIdClient(Integer.parseInt(cl.getId()));
				this.setNum(num);
				this.insert(co);
			}
			else {
				Client.InsertClient(nm, mail);
				this.setIdClient(Client.countClient());
				this.setNum(num);
				this.insert(co);
			}
			res.data= this;
			res.message= Token.getToken(this, co);
			res.code="200";
		} catch (Exception e) {
			res.code="400";
			res.message= e.getMessage();
		} finally {
			if(co != null) co.close();
		}
		return res;
	}
	public Response login() throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			Compte compteValide= Compte.valideLogin(this.getNum(),this.getMdp(), co);
			reponse.data= compteValide;
			reponse.message= Token.insertToken(compteValide,co);
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
	public static Response deconnect(String token) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			Token.updateToken(token, co);
			reponse.data= null;
			reponse.message= "Deconnection reussi!";
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
	public static Response depotMoney(String token,String valeur) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			int idCompte= Token.verificationToken(token,co);
			MouvementMobileMoney.insertMouvement(idCompte,valeur,co);
			ArrayList<MouvementMobileMoney> mouv= MouvementMobileMoney.getLastMouvBYCompte(idCompte,co);
			reponse.data= mouv;
			reponse.message= "depot effectuer veuillez attendre la validation";
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
	public static int getIdOperateur(int idCompte, Connection co) throws Exception {
		Compte compte= Compte.findCompteById(idCompte,co);
		String prefixe= compte.getNum().substring(0,3);
		
		return 1;
	}
	public static Response solde(String token) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		PreparedStatement st = null;
		try {
			int idCompte= Token.verificationToken(token, co);
			co.commit();
			Solde solde= new Solde(idCompte,co);
			r.data=solde;
			r.message="votre solde";
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
}
