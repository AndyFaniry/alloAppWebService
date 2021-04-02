package com.mobilemoney.model.offre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.fonction.Fonction;
import com.mobilemoney.model.Compte;
import com.mobilemoney.model.Credit;
import com.mobilemoney.model.Response;
import com.mobilemoney.model.Solde;
import com.mobilemoney.model.Token;
import com.mobilemoney.model.mouvement.*;

public class AchatOffre {
	int idAchatOffre;
	int idCompte;
	int idOffre;
	LocalDateTime datyDebut;
	LocalDateTime datyFin;
	int appel;
	int sms;
	int internet;
	public int getIdAchatOffre() {
		return idAchatOffre;
	}
	public void setIdAchatOffre(int idAchatOffre) {
		this.idAchatOffre = idAchatOffre;
	}
	public int getIdCompte() {
		return idCompte;
	}
	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}
	public int getIdOffre() {
		return idOffre;
	}
	public void setIdOffre(int idOffre) {
		this.idOffre = idOffre;
	}
	public LocalDateTime getDatyDebut() {
		return datyDebut;
	}
	public void setDatyDebut(LocalDateTime datyDebut) {
		this.datyDebut = datyDebut;
	}
	public LocalDateTime getDatyFin() {
		return datyFin;
	}
	public void setDatyFin(LocalDateTime datyFin) {
		this.datyFin = datyFin;
	}
	public int getAppel() {
		return appel;
	}
	public void setAppel(int appel) {
		this.appel = appel;
	}
	public int getSms() {
		return sms;
	}
	public void setSms(int sms) {
		this.sms = sms;
	}
	public int getInternet() {
		return internet;
	}
	public void setInternet(int internet) {
		this.internet = internet;
	}
	public AchatOffre(int idAchatOffre, int idCompte, int idOffre, LocalDateTime datyDebut, LocalDateTime datyFin,
			int appel, int sms, int internet) {
		super();
		this.idAchatOffre = idAchatOffre;
		this.idCompte = idCompte;
		this.idOffre = idOffre;
		this.datyDebut = datyDebut;
		this.datyFin = datyFin;
		this.appel = appel;
		this.sms = sms;
		this.internet = internet;
	}
	public static void insertAchatOffre(int idCompte, int idOffre, LocalDateTime datyDebut, LocalDateTime datyFin, int appel, int sms, int internet,Connection co) throws Exception {
		PreparedStatement st = null;
		try {
				Timestamp dDebut = Timestamp.valueOf(datyDebut);
				Timestamp dFin = Timestamp.valueOf(datyFin);
				String sql= "insert into achatOffre(idCompte,idOffre,datyDebut,datyFin,appel,sms,internet) values(?,?,?,?,?,?,?)";
				st = co.prepareStatement(sql);
				st.setInt(1,idCompte);
				st.setInt(2,idOffre);
				st.setTimestamp(3,dDebut);
				st.setTimestamp(4,dFin);
				st.setInt(5,appel);
				st.setInt(6,sms);
				st.setInt(7,internet);
				st.execute();
				co.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}	
	}
	public static void insertAchatOffre(int idCompte,String idOffre1,String datyDebut,String modePayement,Connection co) throws Exception {	
		
		int idOffre= Integer.parseInt(idOffre1);
		Offre offre=Offre.detailsOffre(idOffre,co);
		Boolean moneySuffisant= true;
		int  prix= offre.getPrix();
		if(modePayement.compareTo("credit")==0) {
			Integer prix1= new Integer(offre.getPrix()*(-1));
			Credit.insertMouvementCredit(idCompte,"**************",prix1.toString(), co);
			if(Solde.creditSuffisant(idCompte,prix,co)==false) {
				moneySuffisant=false;
				throw new Exception("votre credit est inssuffisante");
			}
		}
		if(modePayement.compareTo("mobileMoney")==0) {
			Integer prix1= new Integer(offre.getPrix()*(-1));
			MouvementMobileMoney.insertMouvementDebut(idCompte,prix1.toString(),co);
			if(Solde.mobileMoneySuffisant(idCompte,prix,co)==false) {
				moneySuffisant=false;
				throw new Exception("votre solde est inssuffisante");
			}
		}
		if(moneySuffisant) {
			int v= offre.getValidite();
			LocalDateTime dDebut = Fonction.setLocalDateTime(datyDebut);
			System.out.println("debut= "+dDebut);
			System.out.println("validiter= "+offre.getValidite());
			LocalDateTime dFin= dDebut.plusDays(offre.getValidite());
			System.out.println("fin= "+dFin);
			int nbrAppel=0;
			int nbrSms=0;
			int nbrMo=0;
			if(offre.getAppel().size()>=1) {
				
				DetailsOffreAppel appel= (DetailsOffreAppel) offre.getAppel().get(0);
				MouvementAppel.insertmouvAppel(idCompte, appel.getValeurTTC(), dDebut, dFin, co);
				nbrAppel=appel.getValeurTTC();
			}
			if(offre.getSms().size()>=1) {
				DetailsOffreSms sms= (DetailsOffreSms) offre.getSms().get(0);
				MouvementSms.insertmouvSms(idCompte,sms.getNbrSms(), dDebut, dFin, co);
				nbrSms= sms.getNbrSms();
			}
			if(offre.getInternet().size()>=1) {
				ArrayList<DetailsOffreInternet> internet1= offre.getInternet();
				for(int i=0; i< internet1.size(); i++) {
					MouvementInternet.insertmouvInternet(idCompte,internet1.get(i).getMo(),internet1.get(i).getTypes(), dDebut, dFin, co);
					nbrMo= nbrMo+internet1.get(i).getMo();
				}
				
			}
			insertAchatOffre(idCompte,idOffre,dDebut,dFin,nbrAppel,nbrSms,nbrMo,co);
			
		}
		
	}
	public static Response achatOffre(String token, String idOffre, String daty,String modePayement) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		PreparedStatement st = null;
		try {
			int idCompte= Token.verificationToken(token, co);
			insertAchatOffre(idCompte,idOffre,daty,modePayement,co);
			co.commit();
			Solde solde= new Solde(idCompte,co);
			r.data=solde;
			r.message="votre achat est effectué";
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
	public static Response achatOffreCode(String token, String code, String daty,String modePayement) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		PreparedStatement st = null;
		try {
			int idCompte= Token.verificationToken(token, co);
			Offre offre= Offre.getOffreByCode(code, co);
			insertAchatOffre(idCompte,new Integer(offre.getIdOffre()).toString(),daty,modePayement,co);
				Solde solde= new Solde(idCompte,co);
			r.data=solde;
			r.message="votre achat est effectué";
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
