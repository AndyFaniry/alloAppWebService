package com.mobilemoney.model.mouvement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mobilemoney.model.offre.DetailsOffreAppel;

public class MouvementInternet {
	int idInternet;
	int idCompte;
	int mo;
	String types;
	LocalDateTime datyDebut;
	LocalDateTime datyFin;
	
	public MouvementInternet(int idInternet, int idCompte, int mo,String types, LocalDateTime datyDebut, LocalDateTime datyFin) {
		super();
		setIdInternet(idInternet);
		setIdCompte(idCompte);
		setMo(mo);
		setTypes(types);
		setDatyDebut(datyDebut);
		setDatyFin(datyFin);
	}
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public int getIdInternet() {
		return idInternet;
	}
	public void setIdInternet(int idInternet) {
		this.idInternet = idInternet;
	}
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
	public static ArrayList<MouvementInternet> findAllMouvInternet(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet result = null;
		ArrayList<MouvementInternet> array = new ArrayList<MouvementInternet>();
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				int idInternet=result.getInt("idInternet");
				int idCompte=result.getInt("idCompte");
				int mo=result.getInt("mo");
				String types=result.getString("types");
				LocalDateTime datyDebut= result.getTimestamp("datyDebut").toLocalDateTime();
				LocalDateTime datyFin= result.getTimestamp("datyFin").toLocalDateTime();
				MouvementInternet internet=new MouvementInternet(idInternet,idCompte,mo,types,datyDebut,datyFin);
				System.out.println("daty fin find all= "+datyFin);
				array.add(internet);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return array;
    }
	public static void insertmouvInternet(int idCompte, int mo,String types,LocalDateTime dateDebut,LocalDateTime dateFin,Connection co ) throws Exception {
		PreparedStatement st = null;
		try {
				Timestamp dDebut = Timestamp.valueOf(dateDebut);
				Timestamp dFin = Timestamp.valueOf(dateFin);
				String sql= "insert into Internet(idCompte,mo,types,datyDebut,datyFin) VALUES(?,?,?,?,?)";
				st = co.prepareStatement(sql);
				st.setInt(1,idCompte);
				st.setInt(2,mo);
				st.setString(3,types);
				st.setTimestamp(4,dDebut);
				st.setTimestamp(5,dFin);
				st.execute();
				co.commit();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}	
	}
}
