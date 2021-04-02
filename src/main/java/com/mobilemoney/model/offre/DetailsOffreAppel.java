package com.mobilemoney.model.offre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.model.Response;
import com.mobilemoney.model.Token;

public class DetailsOffreAppel {
	int idDetailOffreAppel;
	int idOffre;
	int valeurTTC;
	int puMemeOp;
	int puAutreOp;
	public int getIdDetailOffreAppel() {
		return idDetailOffreAppel;
	}
	public void setIdDetailOffreAppel(int idDetailOffreAppel) {
		this.idDetailOffreAppel = idDetailOffreAppel;
	}
	public int getIdOffre() {
		return idOffre;
	}
	public void setIdOffre(int idOffre) {
		this.idOffre = idOffre;
	}
	public int getValeurTTC() {
		return valeurTTC;
	}
	public void setValeurTTC(int valeurTTC) {
		this.valeurTTC = valeurTTC;
	}
	public int getPuMemeOp() {
		return puMemeOp;
	}
	public void setPuMemeOp(int puMemeOp) {
		this.puMemeOp = puMemeOp;
	}
	public int getPuAutreOp() {
		return puAutreOp;
	}
	public void setPuAutreOp(int puAutreOp) {
		this.puAutreOp = puAutreOp;
	}
	public DetailsOffreAppel() {}
	public DetailsOffreAppel(int idDetailOffreAppel, int idOffre, int valeurTTC, int puMemeOp, int puAutreOp) {
		super();
		setIdDetailOffreAppel(idDetailOffreAppel);
		setIdOffre(idOffre);
		setValeurTTC(valeurTTC);
		setPuMemeOp(puMemeOp);
		setPuAutreOp(puAutreOp);
	}
	public static ArrayList<DetailsOffreAppel> findAllDetailOffreAppel(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet result = null;
		ArrayList<DetailsOffreAppel> array = new ArrayList<DetailsOffreAppel>();
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				int idDetailOffreAppel=result.getInt("idOAppel");
				int idOffre=result.getInt("idOffre");
				int valeurTTC=result.getInt("valeurTTC");
				int puMemeOp=result.getInt("puMemeOp");
				int puAutreOp=result.getInt("puAutreOp");
				DetailsOffreAppel offre=new DetailsOffreAppel(idDetailOffreAppel,idOffre,valeurTTC,puMemeOp,puAutreOp);
				array.add(offre);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return array;
    }

	public static Response insertOffreAppel(String idOffre, String valeurTTC, String puMemeOp, String puAutreOp) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		int idOffre1= Integer.parseInt(idOffre);
		int valeurTTC1= Integer.parseInt(valeurTTC);
		int puMemeOp1= Integer.parseInt(puMemeOp);
		int puAutreOp1= Integer.parseInt(puAutreOp);
		PreparedStatement st = null;
		try {
			String sql= "insert into detailOffreAppel(idOAppel, idOffre,valeurTTC,puMemeOp,puAutreOp) VALUES (nextval('seqDetailsOffreAppel'),?,?,?,?)";
			st = co.prepareStatement(sql);
			st.setInt(1,idOffre1);
			st.setInt(2,valeurTTC1);
			st.setInt(3,puMemeOp1);
			st.setInt(4,puAutreOp1);
			st.execute();
			co.commit();
			r.data= null ;
			r.message= "la nouvelle details offre inserer";
			
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
	public static ArrayList<DetailsOffreAppel> getDetailsOffreAppel(int idOffre,Connection co) throws Exception{
		String sql= "select * from detailOffreAppel where idOffre="+idOffre;
		ArrayList<DetailsOffreAppel> details= DetailsOffreAppel.findAllDetailOffreAppel(sql, co);
		return details;
    }
	public static Response deleteOffreAppel(String idOffre) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		int idOffre1= Integer.parseInt(idOffre);
		PreparedStatement st = null;
		try {
			String sql= " delete from DetailOffreAppel where idOAppel=?";
			st = co.prepareStatement(sql);
			st.setInt(1,idOffre1);
			st.execute();
			co.commit();
			r.data= null ;
			r.message= "la forfait appel supprimer";
			
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
	public static void upDateDetailsOffreAppel(String idOAppel, String valeurTTC, String puMemeOp, String puAutreOp,Connection co) throws Exception {
		PreparedStatement st = null;
		int idOAppel1= Integer.parseInt(idOAppel);
		int valeurTTC1= Integer.parseInt(valeurTTC);
		int puMemeOp1= Integer.parseInt(puMemeOp);
		int puAutreOp1= Integer.parseInt(puAutreOp);
		try {
			String sql= "update detailOffreAppel set valeurTTC=?, puMemeOp=?, puAutreOp=? where idOAppel=?";
			st = co.prepareStatement(sql);
			st.setInt(1,valeurTTC1);
			st.setInt(2,puMemeOp1);
			st.setInt(3,puAutreOp1);
			st.setInt(4,idOAppel1);
			st.execute();
			co.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
	}
	public static Response updateDetailsOffreAppel(String idOAppel,String valeurTTC,String puMemeOp,String puAutreOp) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		PreparedStatement st = null;
		try {
			DetailsOffreAppel.upDateDetailsOffreAppel(idOAppel,valeurTTC, puMemeOp,puAutreOp, co);
			String sql="select * from detailOffreAppel where idOAppel="+idOAppel;
			r.data= DetailsOffreAppel.findAllDetailOffreAppel(sql,co);
			r.message= "update offre Appel Effectuer";
			
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
