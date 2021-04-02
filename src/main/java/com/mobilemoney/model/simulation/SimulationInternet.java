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
import com.mobilemoney.model.mouvement.MouvementInternet;

public class SimulationInternet {
	int idCompte;
	int mo;
	String types;
	LocalDateTime debut;
	public int getIdCompte() {
		return idCompte;
	}
	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}
	public int getMo() {
		return mo;
	}
	public void setMo(int mo) {
		this.mo = mo;
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public LocalDateTime getDebut() {
		return debut;
	}
	public void setDebut(LocalDateTime debut) {
		this.debut = debut;
	}
	public SimulationInternet(int idCompte, int mo, String types, LocalDateTime debut) {
		super();
		setIdCompte(idCompte);
		setMo(mo);
		setTypes(types);
		setDebut(debut);
	}
	public static int PayerParCredit(int idCompte, String types,int valeurMo, String daty, Connection co) throws Exception {
		Compte compte= Compte.findCompteById(idCompte,co);
		Tarif tarif= Tarif.getTarif(compte.getIdOperateur(),co);
		int prix= valeurMo*tarif.getPuMo();
		Credit crd= Solde.getSoldeCredit(idCompte, co);
		int reste= crd.getValeur()-prix;
		int val=0;
		if(reste>=0) {
			Credit.insertMouvementCredit(idCompte,"**************",new Integer(prix*(-1)).toString(), co);
			val= prix;
		}
		if(reste<0) {
			Credit.insertMouvementCredit(idCompte,"**************",new Integer(crd.getValeur()*(-1)).toString(), co);
			val= crd.getValeur();
		}
		int mo= val/tarif.getPuMo();
		return mo;
	}
	public static int soldeMoInternet(int idCompte, String type, Connection co) throws Exception {
		int mo=0;
		ArrayList<MouvementInternet> solde = Solde.getSoldeOffreInternet(idCompte, co);
		if(solde==null) return 0;
		for(int i=0; i<solde.size(); i++) {
			String typeSolde= solde.get(i).getTypes();
			if(typeSolde.compareTo(type)==0 || typeSolde.compareTo("global")==0) {
				mo= mo+solde.get(i).getMo();
			}
		}
		return mo;
	}
	public static ArrayList<MouvementInternet> valeurOffreInternet(int idCompte,String daty,Connection co) throws Exception {
		String sql= "select * from internet where datyDebut<'"+daty+"' and '"+daty+"'<datyFin and idCompte="+idCompte;
		System.out.println(sql);
		ArrayList<MouvementInternet> offre= MouvementInternet.findAllMouvInternet(sql, co);
		return offre;
	}
	public static SimulationInternet simulationInternet(int idCompte, String mo, String types,String daty1, Connection co) throws Exception {
		LocalDateTime d= Fonction.setLocalDateTime(daty1);
		String daty= daty1.replace("T"," ");
		int moSolde= SimulationInternet.soldeMoInternet(idCompte,types,co);
		ArrayList<MouvementInternet> offreValide= SimulationInternet.valeurOffreInternet(idCompte, daty, co);
		int moCredit=0;
		int reste= Integer.parseInt(mo);
		int moFinale=0;
		if(offreValide.size()>0) {
			for(int i=0; i<offreValide.size(); i++) {
				if(offreValide.get(i).getMo()>=reste) {
					MouvementInternet.insertmouvInternet(idCompte,reste*(-1),types,d,d,co);
					moFinale=reste;
					reste=0;
					break;
				}
				if(offreValide.get(i).getMo()<reste) {
					MouvementInternet.insertmouvInternet(idCompte,offreValide.get(i).getMo()*(-1),types,d,d,co);
					reste=reste-offreValide.get(i).getMo();
				}
			}
			if(reste>0) {
				moCredit= SimulationInternet.PayerParCredit(idCompte, types, reste, daty, co);
			}
			moFinale= (Integer.parseInt(mo)-reste)+moCredit;
		}
		if(offreValide.size()<=0 && moSolde==0) {
			moFinale= SimulationInternet.PayerParCredit(idCompte, types, reste, daty, co);
		}
		if(offreValide.size()<=0 && moSolde>0) {
			ArrayList<MouvementInternet> solde= Solde.getSoldeOffreInternet(idCompte, co);
			for(int i=0; i<solde.size(); i++) {
				MouvementInternet.insertmouvInternet(idCompte,solde.get(i).getMo()*(-1),types,d,d,co);
			}
		}
		SimulationInternet internet= new SimulationInternet(idCompte,moFinale,types,d);
		return internet;
	}
	public static Response webSerciceSimulationInternet(String token,String mo, String types, String daty) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		int idCompte= Token.verificationToken(token,co);
		Response reponse= new Response();
		try {
			SimulationInternet simInternet= SimulationInternet.simulationInternet(idCompte,mo, types, daty,co);
			reponse.data= simInternet;
			reponse.message= "consommation de donner effectuer";
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
