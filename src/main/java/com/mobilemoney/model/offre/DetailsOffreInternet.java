package com.mobilemoney.model.offre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.model.Response;

public class DetailsOffreInternet {
	int idOInternet;
	int idOffre;
	int mo;
	String types;
	public String getTypes() {
		return types;
	}
	public void setTypes(String types) {
		this.types = types;
	}
	public int getIdOInternet() {
		return idOInternet;
	}
	public void setIdOInternet(int idOInternet) {
		this.idOInternet = idOInternet;
	}
	public int getIdOffre() {
		return idOffre;
	}
	public void setIdOffre(int idOffre) {
		this.idOffre = idOffre;
	}
	public int getMo() {
		return mo;
	}
	public void setMo(int mo) {
		this.mo = mo;
	}
	public DetailsOffreInternet(int idOInternet, int idOffre, int mo,String types) {
		super();
		setIdOInternet(idOInternet);
		setIdOffre(idOffre);
		setMo(mo);
		setTypes(types);
	}
	public static ArrayList<DetailsOffreInternet> findAllDetailOffreInternet(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet result = null;
		ArrayList<DetailsOffreInternet> array = new ArrayList<DetailsOffreInternet>();
		try {
			st = co.prepareStatement(sql);
			result = st.executeQuery(); 
			while(result.next()) {
				int idDetailOffreInternet=result.getInt("idOInternet");
				int idOffre=result.getInt("idOffre");
				int mo=result.getInt("mo");
				String types= result.getString("types");
				DetailsOffreInternet offre=new DetailsOffreInternet(idDetailOffreInternet,idOffre,mo,types);
				array.add(offre);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st!=null) st.close();
		}
		return array;
    }

	public static Response insertOffreInternet(String idOffre, String mo,String types) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		int idOffre1= Integer.parseInt(idOffre);
		int mo1= Integer.parseInt(mo);
		PreparedStatement st = null;
		try {
			String sql= "insert into detailOffreInternet(idOInternet, idOffre,mo,types) VALUES (nextval('seqDetailsOffreInternet'),?,?,?)";
			st = co.prepareStatement(sql);
			st.setInt(1,idOffre1);
			st.setInt(2,mo1);
			st.setString(3,types);
			st.execute();
			co.commit();
			r.data= null ;
			r.message= "la nouvelle details offre Internet inserer";
			
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
	public static ArrayList<DetailsOffreInternet> getDetailsOffreInternet(int idOffre,Connection co) throws Exception{
		String sql= "select * from detailOffreInternet where idOffre="+idOffre;
		ArrayList<DetailsOffreInternet> details= DetailsOffreInternet.findAllDetailOffreInternet(sql, co);
		return details;
    }
	public static Response deleteOffreInternet(String idOffre) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		int idOffre1= Integer.parseInt(idOffre);
		PreparedStatement st = null;
		try {
			String sql= " delete from DetailOffreInternet where idOInternet=?";
			st = co.prepareStatement(sql);
			st.setInt(1,idOffre1);
			st.execute();
			co.commit();
			r.data= null ;
			r.message= "la forfait inetrnet supprimer";
			
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
	public static void upDateDetailsOffreInternet(String idOInternet, String mo, String types,Connection co) throws Exception {
		PreparedStatement st = null;
		int idOInternet1= Integer.parseInt(idOInternet);
		int mo1= Integer.parseInt(mo);
		try {
			String sql= "update detailOffreInternet set mo=?, types=? where idOInternet=?";
			st = co.prepareStatement(sql);
			st.setInt(1,mo1);
			st.setString(2,types);
			st.setInt(3,idOInternet1);
			st.execute();
			co.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
	}
	public static Response updateDetailsOffreInternet(String idOInternet,String mo,String types) throws Exception {
		Connection co= new ConnectionPstg().getConnection();
		Response r= new Response();
		r.code= "200";
		r.data=null;
		PreparedStatement st = null;
		try {
			DetailsOffreInternet.upDateDetailsOffreInternet(idOInternet,mo,types,co);
			String sql="select * from detailOffreInternet where idOInternet="+idOInternet;
			r.data= DetailsOffreInternet.findAllDetailOffreInternet(sql,co);
			r.message= "update offre Internet Effectuer";
			
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
