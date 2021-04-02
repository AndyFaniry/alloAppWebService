package com.mobilemoney.model.simulation;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.fonction.Fonction;
import com.mobilemoney.model.Compte;
import com.mobilemoney.model.Credit;
import com.mobilemoney.model.Operateur;
import com.mobilemoney.model.Response;
import com.mobilemoney.model.Solde;
import com.mobilemoney.model.Token;
import com.mobilemoney.model.historique.HistoriqueSms;
import com.mobilemoney.model.mouvement.MouvementAppel;
import com.mobilemoney.model.mouvement.MouvementSms;

public class SimulationSms {
	static int tailleMsg1= 120;
	int idCompte;
	String num;
	String numCompose;
	String message;
	LocalDateTime daty;
	public String getNum() {
		return num;
	}

	public void setNum(Connection co) throws Exception {
		Compte compte= Compte.findCompteById(this.getIdCompte(),co);
		this.num= compte.getNum();	
	}
	public int getIdCompte() {
		return idCompte;
	}
	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}
	public String getNumCompose() {
		return numCompose;
	}
	public void setNumCompose(String numCompose) {
		this.numCompose = numCompose;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public LocalDateTime getDaty() {
		return daty;
	}
	public void setDaty(LocalDateTime daty) {
		this.daty = daty;
	}
	public SimulationSms(int idCompte, String numCompose, String message, LocalDateTime daty,Connection co) throws Exception {
		super();
		setIdCompte(idCompte);
		setNum(co);
		setNumCompose(numCompose);
		setMessage(message);
		setDaty(daty);
	}
	public static int getNbrMsg(String msg) {
		int nbrSms= msg.length()/tailleMsg1;
		int reste= msg.length()%tailleMsg1;
		if(reste>0) nbrSms=nbrSms+1;
		return nbrSms;
	}
	
	public static int prixSms(int idCompte, String numCompose,String msg, Connection co) throws Exception {
		int nbrSms= SimulationSms.getNbrMsg(msg);
		Compte compte1= Compte.findCompteById(idCompte,co);
		String n= numCompose;
		String prefixe= numCompose.substring(0,3);
		Operateur op2= Operateur.getOperateurByPrefixe(prefixe,co);
		Tarif tarif= Tarif.getTarif(compte1.getIdOperateur(),co);
		int puSms= tarif.getPuSmsAutreOp();
		if(compte1.getIdOperateur()==op2.getIdOperateur()) puSms= tarif.getPuSmsMemeOp();
		int prix= puSms*nbrSms;
		return prix;
	}
	
	public static int valeurOffreSms(int idCompte,String daty,Connection co) throws Exception {
		String datysql= daty.replace("T"," ");
		String sql= "select * from sms where datyDebut<'"+datysql+"' and '"+datysql+"'<datyFin and idCompte="+idCompte;
		ArrayList<MouvementSms> offre= MouvementSms.findAllMouvSms(sql, co);
		MouvementSms sms= Solde.getSoldeOffreSms(idCompte,co).get(0);
		if(offre.size()>0) {
			return sms.getNbrSms();
		}
		if(offre.size()<=0 && sms.getNbrSms()>0) {
			int valeur2= sms.getNbrSms()*(-1);
			LocalDateTime d= Fonction.setLocalDateTime(daty);
			 MouvementSms.insertmouvSms(idCompte,valeur2 ,d,d,co);
		}
		return 0;
	}
	public static void payerParCredit(int idCompte, int valeur, String daty, Connection co) throws Exception {
		Credit crd= Solde.getSoldeCredit(idCompte, co);
		if(crd.getValeur()<valeur) throw new Exception("echec d'envoie credit insuffisant");
		else Credit.insertMouvementCredit(idCompte,"**************",new Integer(valeur*(-1)).toString(), co);
	}
	public static SimulationSms simulationSms(int idCompte, String numCompose,String msg,String daty, Connection co)throws Exception {
		LocalDateTime d= Fonction.setLocalDateTime(daty);
		int prixSms= SimulationSms.prixSms(idCompte, numCompose,msg,co);
		int valeurOffreSms= SimulationSms.valeurOffreSms(idCompte, daty,co);
		int nbrSms= SimulationSms.getNbrMsg(msg);
		if(valeurOffreSms==0) {
			SimulationSms.payerParCredit(idCompte,prixSms,daty.replace("T"," "),co);
		}
		if(valeurOffreSms>=nbrSms) {
			MouvementSms.insertmouvSms(idCompte,nbrSms*(-1) ,d,d,co);
		}
		SimulationSms simSms= new SimulationSms(idCompte,numCompose,msg,d,co);
		return simSms;
	}
	public static Response historiqueSms(String token,String numCompose, String msg, String daty) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		co.setAutoCommit(false);
		int idCompte= Token.verificationToken(token,co);
		Response reponse= new Response();
		try {
			SimulationSms simSms= SimulationSms.simulationSms(idCompte,numCompose,msg,daty, co);
			String insertion= HistoriqueSms.insertHistoriqueSms(simSms.getNum(),simSms.getNumCompose(),simSms.getDaty().toString(),simSms.getMessage());
			reponse.data= simSms;
			reponse.message= "message envoyer";
			reponse.code="200";
			co.commit();
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
