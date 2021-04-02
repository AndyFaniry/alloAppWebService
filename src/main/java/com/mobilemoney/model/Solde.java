package com.mobilemoney.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.model.mouvement.MouvementAppel;
import com.mobilemoney.model.mouvement.MouvementInternet;
import com.mobilemoney.model.mouvement.MouvementMobileMoney;
import com.mobilemoney.model.mouvement.MouvementSms;
public class Solde {
	MouvementMobileMoney mobileMoney;
	Credit credit;
	ArrayList<MouvementAppel> appel;
	ArrayList<MouvementSms> sms;
	ArrayList<MouvementInternet> internet;
	
	public MouvementMobileMoney getMobileMoney() {
		return mobileMoney;
	}
	public void setMobileMoney(MouvementMobileMoney mobileMoney) {
		this.mobileMoney = mobileMoney;
	}
	public ArrayList<MouvementInternet> getInternet() {
		return internet;
	}
	public void setInternet(ArrayList<MouvementInternet> internet) {
		this.internet = internet;
	}
	public void setAppel(ArrayList<MouvementAppel> appel) {
		this.appel = appel;
	}
	public void setSms(ArrayList<MouvementSms> sms) {
		this.sms = sms;
	}
	public Credit getCredit() {
		return credit;
	}
	public void setCredit(Credit credit) {
		this.credit = credit;
	}
	public Solde(int idCompte,Connection co) throws Exception {
		setMobileMoney(soldeCompte(idCompte,co));
		setCredit(getSoldeCredit(idCompte,co));
		setAppel(getSoldeOffreAppel(idCompte,co));
		setSms(getSoldeOffreSms(idCompte,co));
		setInternet(getSoldeOffreInternet(idCompte,co));
	}
	//solde Mobile Money
	public static MouvementMobileMoney soldeCompte(int idCompte,Connection co) throws Exception {
		ArrayList<MouvementMobileMoney> val= new ArrayList<MouvementMobileMoney>();
		String sql="select max(idMobileMoney) as idMobileMoney,idCompte,idOperateur,num,sum(valeur) as valeur, max(daty) as daty,statu,nom from v_MobileMoney where idCompte="+idCompte+" and statu=1 group by idCompte,idOperateur,num,statu,nom order by valeur";
		return MouvementMobileMoney.findMouvementMobileMoney(sql,co).get(0);
	}
	public static Response getSoldeCompte(String token) throws Exception {
		Connection co= new ConnectionPstg().getConnection();	
		Response reponse= new Response();
		try {
			int idCompte= Token.verificationToken(token,co);
			MouvementMobileMoney solde= soldeCompte(idCompte,co);
				reponse.data= solde;
				reponse.message= "votre solde";
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
	
	//get solde Credit
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
			r.data=getSoldeCredit(idCompte,co);
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

	public static ArrayList<MouvementAppel> getSoldeOffreAppel(int idCompte, Connection co) throws Exception {
		String sql="select * from v_appel where idCompte="+idCompte;
		ArrayList<MouvementAppel> appel= MouvementAppel.findAllMouvAppel(sql,co);
		if(appel.size()<1) return null;
		return appel;
	}
	
	public static ArrayList<MouvementSms> getSoldeOffreSms(int idCompte, Connection co) throws Exception {
		String sql="select * from v_Sms where idCompte="+idCompte;
		ArrayList<MouvementSms> sms= MouvementSms.findAllMouvSms(sql,co);
		if(sms.size()<1) return null;
		return sms;
	}
	public static ArrayList<MouvementInternet> getSoldeOffreInternet(int idCompte, Connection co) throws Exception {
		String sql="select * from v_Internet where idCompte="+idCompte;
		ArrayList<MouvementInternet> internet= MouvementInternet.findAllMouvInternet(sql,co);
		if(internet.size()<1) return null;
		return internet;
	}
	//verification solde
	public static Boolean creditSuffisant(int idCompte,int valeur,Connection co) throws Exception {
		Credit crd=getSoldeCredit(idCompte,co);
		if(valeur<0) throw new Exception("montant negatif");
		if(crd.getValeur()<valeur) return false;
		return true;
	}
	public static Boolean mobileMoneySuffisant(int idCompte, int valeur, Connection co)throws Exception{
		MouvementMobileMoney money= soldeCompte(idCompte,co);
		if(valeur<0) throw new Exception("montant negatif");
		if(money.getValeur()<valeur) return false;
		return true;
	}
}
