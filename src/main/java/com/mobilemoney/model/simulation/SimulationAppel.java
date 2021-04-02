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
import com.mobilemoney.model.historique.HistoriqueAppel;
import com.mobilemoney.model.mouvement.MouvementAppel;

public class SimulationAppel {
	int idCompte;
	String num;
	String numCompose;
	int duree;
	LocalDateTime debut;
	int valeur;
	
	
	public SimulationAppel(int idCompte, String numCompose, int duree, LocalDateTime debut,int valeur,Connection co) throws Exception {
		super();
		setIdCompte(idCompte);
		setNum(co);
		setNumCompose(numCompose);
		setDuree(duree);
		setDebut(debut);
		setValeur(valeur);
	}
	public int getValeur() {
		return valeur;
	}
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}
	public String getNum() {
		return num;
	}

	public void setNum(Connection co) throws Exception {
		Compte compte= Compte.findCompteById(this.getIdCompte(),co);
		this.num= compte.getNum();	
	}
	public LocalDateTime getDebut() {
		return debut;
	}

	public void setDebut(LocalDateTime debut) {
		this.debut = debut;
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

	public int getDuree() {
		return duree;
	}

	public void setDuree(int duree) {
		this.duree = duree;
	}

	
	public static int prixAppel(int idCompte, String numCompose,int duree, Connection co) throws Exception {
		Compte compte1= Compte.findCompteById(idCompte,co);
		String prefixe= numCompose.substring(0,3);
		Operateur op2= Operateur.getOperateurByPrefixe(prefixe,co);
		Tarif tarif= Tarif.getTarif(compte1.getIdOperateur(),co);
		int puAppel= tarif.getPuAutreOp();
		if(compte1.getIdOperateur()==op2.getIdOperateur()) puAppel= tarif.getPuMemeOp();
		int prix= puAppel*duree;
		return prix;
	}
	public static int dureeAppel(int idCompte, String numCompose,int valeurTTC, Connection co) throws Exception {
		Compte compte1= Compte.findCompteById(idCompte,co);
		String prefixe= numCompose.substring(0,3);
		Operateur op2= Operateur.getOperateurByPrefixe(prefixe,co);
		Tarif tarif= Tarif.getTarif(compte1.getIdOperateur(),co);
		int duree= valeurTTC/tarif.getPuAutreOp();
		if(compte1.getIdOperateur()==op2.getIdOperateur()) duree= valeurTTC/tarif.getPuMemeOp();
		return duree;
	}
	public static int valeurOffreAppel(int idCompte,String daty,Connection co) throws Exception {
		String datysql= daty.replace("T"," ");
		String sql= "select * from appel where datyDebut<'"+datysql+"' and '"+datysql+"'<datyFin and idCompte="+idCompte;
		ArrayList<MouvementAppel> offre= MouvementAppel.findAllMouvAppel(sql, co);
		MouvementAppel appel= Solde.getSoldeOffreAppel(idCompte,co).get(0);
		if(offre.size()>0) {
			System.out.println("valeur= "+appel.getValeur());
			return appel.getValeur();
		}
		if(offre.size()<=0 && appel.getValeur()>0) {
			int valeur2= appel.getValeur()*(-1);
			System.out.println("valeur nega= "+valeur2);
			LocalDateTime d= Fonction.setLocalDateTime(daty);
			 MouvementAppel.insertmouvAppel(idCompte,valeur2 ,d,d,co);
		}
		return 0;
	}
	public static int payerParCredit(int idCompte, int valeur, String daty, Connection co) throws Exception {
		Credit crd= Solde.getSoldeCredit(idCompte, co);
		int val=0;
		int reste= crd.getValeur()-valeur;
		if(reste>=0) {
			Credit.insertMouvementCredit(idCompte,"**************",new Integer(valeur*(-1)).toString(), co);
			val= valeur;
		}
		if(reste<0) {
			Credit.insertMouvementCredit(idCompte,"**************",new Integer(crd.getValeur()*(-1)).toString(), co);
			val= crd.getValeur();
		}
		return val;
	}
	public static SimulationAppel simulationAppel(int idCompte, String numCompose,int duree,String daty, Connection co)throws Exception {
		LocalDateTime d= Fonction.setLocalDateTime(daty);
		int valeurTTC= 0;
		int restePayer=0;
		int payer=0;
		int prixAppel= SimulationAppel.prixAppel(idCompte, numCompose, duree, co);
		int valeurOffreAppel= SimulationAppel.valeurOffreAppel(idCompte,daty,co);
		if(valeurOffreAppel==0) {
			payer= SimulationAppel.payerParCredit(idCompte,prixAppel,daty,co);
			MouvementAppel.insertmouvAppel(idCompte,payer*(-1) ,d,d,co);
			valeurTTC= payer;
		}
		if(valeurOffreAppel>prixAppel) {
			int valeur2= prixAppel*(-1);
			MouvementAppel.insertmouvAppel(idCompte,valeur2 ,d,d,co);
			valeurTTC= prixAppel;
		}
		if(valeurOffreAppel!=0 && valeurOffreAppel<prixAppel) {
			MouvementAppel.insertmouvAppel(idCompte,valeurOffreAppel*(-1) ,d,d,co);
			restePayer= prixAppel-valeurOffreAppel;
			payer= SimulationAppel.payerParCredit(idCompte,restePayer,daty,co);
			valeurTTC= prixAppel-valeurOffreAppel-payer;
		}
		int dureeFinal= SimulationAppel.dureeAppel(idCompte,numCompose,valeurTTC,co);
		SimulationAppel simAppel= new SimulationAppel(idCompte,numCompose, dureeFinal,d,valeurTTC,co);
		return simAppel;
	}
	public static Response historiqueAppel(String token,String numCompose, String duree1, String daty) throws Exception {
		int duree= Integer.parseInt(duree1);
		Connection co= new ConnectionPstg().getConnection();
		int idCompte= Token.verificationToken(token,co);
		Response reponse= new Response();
		try {
			SimulationAppel simAppel= SimulationAppel.simulationAppel(idCompte,numCompose,duree,daty, co);
			String insertion= HistoriqueAppel.insertHistoriqueAppel(simAppel.getNum(),simAppel.getNumCompose(),simAppel.getDebut().toString(),String.valueOf(simAppel.getDuree()));
			reponse.data= simAppel;
			if(simAppel.getDuree()>duree) reponse.message= "credit inssuffisant";
			reponse.message= "appel effectuer";
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
