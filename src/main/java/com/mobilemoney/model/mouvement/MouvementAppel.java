package com.mobilemoney.model.mouvement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MouvementAppel {
	int idAppel;
	int idCompte;
	int valeur;
	LocalDateTime datyDebut;
	LocalDateTime datyFin;
	
	public int getIdAppel() {
		return idAppel;
	}

	public void setIdAppel(int idAppel) {
		this.idAppel = idAppel;
	}

	public int getIdCompte() {
		return idCompte;
	}

	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}

	public int getValeur() {
		return valeur;
	}

	public void setValeur(int valeur) {
		this.valeur = valeur;
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

	public MouvementAppel(int idAppel, int idCompte, int valeur, LocalDateTime datyDebut, LocalDateTime datyFin) {
		super();
		this.idAppel = idAppel;
		this.idCompte = idCompte;
		this.valeur = valeur;
		this.datyDebut = datyDebut;
		this.datyFin = datyFin;
	}
	public static ArrayList<MouvementAppel> findAllMouvAppel(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet result = null;
		ArrayList<MouvementAppel> array = new ArrayList<MouvementAppel>();
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				int idAppel=result.getInt("idAppel");
				int idCompte=result.getInt("idCompte");
				int valeur=result.getInt("valeur");
				LocalDateTime datyDebut= result.getTimestamp("datyDebut").toLocalDateTime();
				LocalDateTime datyFin= result.getTimestamp("datyFin").toLocalDateTime();
				MouvementAppel appel=new MouvementAppel(idAppel,idCompte,valeur,datyDebut,datyFin);
				array.add(appel);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return array;
    }
	public static void insertmouvAppel(int idCompte, int valeur,LocalDateTime dateDebut,LocalDateTime dateFin,Connection co ) throws Exception {
		PreparedStatement st = null;
		try {
				Timestamp dDebut = Timestamp.valueOf(dateDebut);
				Timestamp dFin = Timestamp.valueOf(dateFin);
				String sql= "insert into appel(idAppel,idCompte,valeur,datydebut,datyFin) values(nextval('seqAppel'),?,?,?,?)";
				st = co.prepareStatement(sql);
				st.setInt(1,idCompte);
				st.setInt(2,valeur);
				st.setTimestamp(3,dDebut);
				st.setTimestamp(4,dFin);
				st.execute();
				co.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}	
	}
	
}
