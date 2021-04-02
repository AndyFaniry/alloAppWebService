package com.mobilemoney.model.simulation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mobilemoney.model.offre.Offre;

public class Tarif {
	int idTarif;
	int idOperateur;
    int puAutreOp;
    int puMemeOp;
    int puMo ;
    int puSmsAutreOp;
    int puSmsMemeOp;
	public Tarif(int idTarif, int idOperateur, int puAutreOp, int puMemeOp, int puMo, int puSmsAutreOp,
			int puSmsMemeOp) {
		super();
		this.idTarif = idTarif;
		this.idOperateur = idOperateur;
		this.puAutreOp = puAutreOp;
		this.puMemeOp = puMemeOp;
		this.puMo = puMo;
		this.puSmsAutreOp = puSmsAutreOp;
		this.puSmsMemeOp = puSmsMemeOp;
	}
	public int getPuSmsAutreOp() {
		return puSmsAutreOp;
	}
	public void setPuSmsAutreOp(int puSmsAutreOp) {
		this.puSmsAutreOp = puSmsAutreOp;
	}
	public int getPuSmsMemeOp() {
		return puSmsMemeOp;
	}
	public void setPuSmsMemeOp(int puSmsMemeOp) {
		this.puSmsMemeOp = puSmsMemeOp;
	}
	public int getIdTarif() {
		return idTarif;
	}
	public void setIdTarif(int idTarif) {
		this.idTarif = idTarif;
	}
	public int getIdOperateur() {
		return idOperateur;
	}
	public void setIdOperateur(int idOperateur) {
		this.idOperateur = idOperateur;
	}
	public int getPuAutreOp() {
		return puAutreOp;
	}
	public void setPuAutreOp(int puAutreOp) {
		this.puAutreOp = puAutreOp;
	}
	public int getPuMemeOp() {
		return puMemeOp;
	}
	public void setPuMemeOp(int puMemeOp) {
		this.puMemeOp = puMemeOp;
	}
	public int getPuMo() {
		return puMo;
	}
	public void setPuMo(int puMo) {
		this.puMo = puMo;
	}
	
	
	public static ArrayList<Tarif> findAllTarif(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet result = null;
		ArrayList<Tarif> array = new ArrayList<Tarif>();
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				int idTarif=result.getInt("idTarif");
				int idOperateur=result.getInt("idoperateur");
				int puAutreOp=result.getInt("puAutreOp");
				int puMemeOp=result.getInt("puMemeOp");
				int puMo=result.getInt("puMo");
				int puSmsAutreOp=result.getInt("puSmsAutreOp");
				int puSmsMemeOp=result.getInt("puSmsMemeOp");
				Tarif tarif=new Tarif(idTarif, idOperateur, puAutreOp,puMemeOp,puMo,puSmsAutreOp,puSmsMemeOp);
				array.add(tarif);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return array;
    }
	public static Tarif getTarif(int idOperateur,Connection co) throws Exception {
		String sql= "select * from tarif where idOperateur="+idOperateur;
		ArrayList<Tarif> tarif= Tarif.findAllTarif(sql,co);
		return tarif.get(0);
	}
}
