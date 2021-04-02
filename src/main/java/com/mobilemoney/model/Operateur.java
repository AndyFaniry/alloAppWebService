package com.mobilemoney.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.fonction.Fonction;
import com.mobilemoney.model.mouvement.MouvementMobileMoney;

public class Operateur {
	 int idOperateur;
	 String nom;
	 String prefixe;
	 String mdp;
	public int getIdOperateur() {
		return idOperateur;
	}
	public void setIdOperateur(int idOperateur) {
		this.idOperateur = idOperateur;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getPrefixe() {
		return prefixe;
	}
	public void setPrefixe(String prefixe) {
		this.prefixe = prefixe;
	}
	public String getMdp() {
		return mdp;
	}
	public void setMdp(String mdp) {
		this.mdp = mdp;
	}
	public Operateur() {}
	public Operateur(int idOperateur, String nom, String prefixe, String mdp) {
		setIdOperateur(idOperateur);
		setNom(nom);
		setPrefixe(prefixe);
		setMdp(mdp);
	}
	public Operateur(String token, String nom, String prefixe, String mdp) {
		setIdOperateur(idOperateur);
		setNom(nom);
		setPrefixe(prefixe);
		setMdp(mdp);
	}
	public static ArrayList<Operateur> findOperateur(String sql,Connection co){
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<Operateur> op=new ArrayList<Operateur>();
		try {
			preparedStatement = co.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				int idOperateur=resultSet.getInt("idOperateur");
				String nom=resultSet.getString("nom");
				String prefixe=resultSet.getString("prefixe");
				String mdp=resultSet.getString("mdp");
				Operateur operateur= new Operateur(idOperateur,nom,prefixe,mdp);
				op.add(operateur);
			}
		}catch(Exception e) {
			e.getMessage();
		}
		return op;
    }
	public static Operateur getOperateurByPrefixe(String prefixe,Connection co) throws Exception {
		String sql= "select * from operateur where prefixe='"+prefixe+"'";
		ArrayList<Operateur> op= Operateur.findOperateur(sql, co);
		System.out.println(op.size());
		if(op.size()<0) throw new Exception("le numero que vous appelez n'existe pas");
		return op.get(0);
	}
	public static Operateur valideLogin(String nom, String mdp, Connection co) throws Exception {
		String sql= "select * from operateur where nom='"+nom+"' and mdp=md5('@admin123"+mdp+"')";
		ArrayList<Operateur> operateur= Operateur.findOperateur(sql, co);
		if(operateur.size()!=1) throw new Exception("mot de passe ou nom non valide");
		return operateur.get(0);
	}
	public Response login() throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			Operateur opValide= Operateur.valideLogin(this.getNom(),this.getMdp(), co);
			reponse.data= opValide;
			reponse.message= Token.insertTokenAdmin(opValide,co);
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
			Token.updateTokenAdmin(token, co);
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
	public static Response getDepotNonValide(String token) throws Exception{
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		ArrayList<MouvementMobileMoney> val=Operateur.getMouvDepotNonValide(token,co);
		try {
			reponse.data= val;
			reponse.message= null;
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
	public static ArrayList<MouvementMobileMoney> getMouvDepotNonValide(String token,Connection co) throws Exception {
		ArrayList<MouvementMobileMoney> val= new ArrayList<MouvementMobileMoney>();
		String idOperateur= Token.verificationTokenAdmin(token,co);
		String sql="select * from v_depot_non_valide where idOperateur="+idOperateur;
		System.out.println("sql andramana="+sql);
		val= MouvementMobileMoney.findMouvementMobileMoney(sql,co);
		return val;
	}
	public static Response getRetraitNonValide(String token) throws Exception{
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		ArrayList<MouvementMobileMoney> val=Operateur.getMouvRetraitNonValide(token,co);
		try {
			reponse.data= val;
			reponse.message= null;
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
	public static ArrayList<MouvementMobileMoney> getMouvRetraitNonValide(String token,Connection co) throws Exception {
		ArrayList<MouvementMobileMoney> val= new ArrayList<MouvementMobileMoney>();
		String idOperateur= Token.verificationTokenAdmin(token,co);
		String sql="select * from v_retrait_non_valide where idOperateur="+idOperateur;
		System.out.println("sql andramana="+sql);
		val= MouvementMobileMoney.findMouvementMobileMoney(sql,co);
		return val;
	}
	public static Response validerMouvement(int idMouvementMoney) throws Exception{
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		MouvementMobileMoney.upDateMouvementMobileMoney(idMouvementMoney,co);
		ArrayList<MouvementMobileMoney> mouvs= MouvementMobileMoney.findMouvementMobileMoneyById(idMouvementMoney,co);
		try {
			reponse.data= mouvs;
			reponse.message= "mouvement valider";
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
	public static Response validerMouvement(String token, String idMouv) throws Exception{
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		int idMouvementMoney= Integer.parseInt(idMouv);
		String idAdmin= Token.verificationTokenAdmin(token, co);
		ArrayList<MouvementMobileMoney> mouvs= new ArrayList<MouvementMobileMoney>();
		try {
			if(idAdmin!=null) {
				MouvementMobileMoney.upDateMouvementMobileMoney(idMouvementMoney,co);
				mouvs= MouvementMobileMoney.findMouvementMobileMoneyById(idMouvementMoney,co);
				reponse.data= mouvs;
				reponse.message= "mouvement valider";
				reponse.code="200";
			}
			else {
				reponse.data= null;
				reponse.message= "veuillez vous connecter";
				reponse.code="200";
			}
			
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
	public static ArrayList<MouvementMobileMoney> getDepotEffectuer(String token,String daty1, String daty2,Connection co) throws Exception {
		ArrayList<MouvementMobileMoney> val= new ArrayList<MouvementMobileMoney>();
		String idOperateur= Token.verificationTokenAdmin(token,co);
		String sql="select * from v_depot_valide where idOperateur="+idOperateur+" and daty>'"+daty1+"' and daty<'"+daty2+"'";
		val= MouvementMobileMoney.findMouvementMobileMoney(sql,co);
		return val;
	}
	public static ArrayList<MouvementMobileMoney> getRetraitEffectuer(String token,String daty1, String daty2,Connection co) throws Exception {
		ArrayList<MouvementMobileMoney> val= new ArrayList<MouvementMobileMoney>();
		String idOperateur= Token.verificationTokenAdmin(token,co);
		String sql="select * from v_retrait_valide where idOperateur="+idOperateur+" and date(daty)>='"+daty1+"' and date(daty)<='"+daty2+"'";
		val= MouvementMobileMoney.findMouvementMobileMoney(sql,co);
		return val;
	}
	public static Response statDepot(String token,String type, String daty1, String daty2) throws Exception{
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			ArrayList<MouvementMobileMoney> mouvs=  new ArrayList<MouvementMobileMoney>();
			if(type.compareTo("depot")==0) mouvs= Operateur.getDepotEffectuer(token,daty1,daty2,co);
			else mouvs= Operateur.getRetraitEffectuer(token,daty1,daty2,co);
			reponse.data= mouvs;
			reponse.message= "Tous les Depots effectués";
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
	public static Operateur findOperateurById(String ido,Connection co){
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		Operateur operateur=null;
		try {
			String sql="select * from operateur where  idoperateur="+ido;
			preparedStatement = co.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				int idOperateur=resultSet.getInt("idOperateur");
				String nom=resultSet.getString("nom");
				String prefixe=resultSet.getString("prefixe");
				String mdp=resultSet.getString("mdp");
				operateur= new Operateur(idOperateur,nom,prefixe,mdp);
			}
		}catch(Exception e) {
			e.getMessage();
		}
		return operateur;
    }
	public static Response getOperateur(String token) throws Exception{
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		String id=Token.verificationTokenAdmin(token, co);
		Operateur val=Operateur.findOperateurById(id, co);
		try {
			reponse.data= val;
			reponse.message= null;
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
	public static Response findAllOperateur() throws Exception{
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			reponse.data= Operateur.findOperateur("select * from operateur", co);
			reponse.message= null;
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
	public static Response checkMdp(String token,String mdp) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			String ido=Token.verificationTokenAdmin(token, co);
			Operateur opValide= Operateur.findOperateurById(ido, co);
			System.out.println(opValide.getMdp());
			String mdpp="@admin123"+mdp;
			String mdpsha1=Fonction.addSha1(mdpp, co);
			System.out.println(mdpsha1);
			if(opValide.getMdp().compareTo(mdpsha1)==0) {
				reponse.data= opValide;
				reponse.message= "Mots de passe compatible";
				reponse.code="200";
			}else {
				throw new Exception("Mots de passe incorrect");
			}
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
	public static void updateOperateur(int idOperateur,String nom,String Prefix,String mdp,Connection co) throws Exception{
		PreparedStatement st = null;
		try {
			String sql= "update operateur set nom=?,prefix=?,mdp=?  where idOperateur=?";
			st = co.prepareStatement(sql);
			st.setString(1,nom);
			st.setString(2, Prefix);
			st.setString(3, mdp);
			st.setInt(4, idOperateur);
			st.execute();
				co.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
	}
	public static Response update(String token,String nom,String Prefix,String mdp) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response reponse= new Response();
		try {
			String ido=Token.verificationTokenAdmin(token, co);
			Operateur opValide= Operateur.findOperateurById(ido, co);
			String mdpsha1=Fonction.addSha1(mdp, co);
			Operateur.updateOperateur(Integer.parseInt(ido), nom, Prefix, mdpsha1, co);
				reponse.data= opValide;
				reponse.message= "Operateur update";
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
	public static ArrayList<MouvementMobileMoney> soldeAllClient(String token,Connection co) throws Exception {
		ArrayList<MouvementMobileMoney> val= new ArrayList<MouvementMobileMoney>();
		String idOperateur= Token.verificationTokenAdmin(token,co);
		String sql="select max(idMobileMoney) as idMobileMoney,idCompte,idOperateur,num,sum(valeur) as valeur, max(daty) as daty,statu,nom from v_MobileMoney where idOperateur="+idOperateur+" and statu=1 group by idCompte,idOperateur,num,statu,nom order by valeur";
		val= MouvementMobileMoney.findMouvementMobileMoney(sql,co);
		return val;
	}
	public static Response getSoldeClient(String token) throws Exception {
		Connection co= new ConnectionPstg().getConnection();	
		Response reponse= new Response();
		try {
			ArrayList<MouvementMobileMoney> solde=  Operateur.soldeAllClient(token,co);
				reponse.data= solde;
				reponse.message= "Solde client";
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
