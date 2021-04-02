package com.mobilemoney.model.mouvement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class MouvementSms {
	int idSms;
	int idCompte;
	int nbrSms;
	LocalDateTime datyDebut;
	LocalDateTime datyFin;
	
	public MouvementSms(int idSms, int idCompte, int nbrSms, LocalDateTime datyDebut, LocalDateTime datyFin) {
		super();
		this.idSms = idSms;
		this.idCompte = idCompte;
		this.nbrSms = nbrSms;
		this.datyDebut = datyDebut;
		this.datyFin = datyFin;
	}

	public int getIdSms() {
		return idSms;
	}

	public void setIdSms(int idSms) {
		this.idSms = idSms;
	}

	public int getIdCompte() {
		return idCompte;
	}

	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}

	public int getNbrSms() {
		return nbrSms;
	}

	public void setNbrSms(int nbrSms) {
		this.nbrSms = nbrSms;
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
	public static ArrayList<MouvementSms> findAllMouvSms(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet result = null;
		ArrayList<MouvementSms> array = new ArrayList<MouvementSms>();
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				int idSms=result.getInt("idSms");
				int idCompte=result.getInt("idCompte");
				int nbrSms=result.getInt("nbrSms");
				LocalDateTime datyDebut= result.getTimestamp("datyDebut").toLocalDateTime();
				LocalDateTime datyFin= result.getTimestamp("datyFin").toLocalDateTime();
				MouvementSms sms=new MouvementSms(idSms,idCompte,nbrSms,datyDebut,datyFin);
				array.add(sms);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return array;
    }

	public static void insertmouvSms(int idCompte, int nbr,LocalDateTime dateDebut,LocalDateTime dateFin,Connection co ) throws Exception {
		PreparedStatement st = null;
		try {
				Timestamp dDebut = Timestamp.valueOf(dateDebut);
				Timestamp dFin = Timestamp.valueOf(dateFin);
				String sql= "insert into sms(idCompte,nbrSms,datyDebut,datyFin) VALUES(?,?,?,?)";
				st = co.prepareStatement(sql);
				st.setInt(1,idCompte);
				st.setInt(2,nbr);
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
