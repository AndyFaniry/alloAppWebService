package com.mobilemoney.model.offre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.model.Compte;
import com.mobilemoney.model.Response;
import com.mobilemoney.model.Token;

	public class Offre {
		int idOffre;
		int idOperateur;
		String nom;
		String code;
		int prix;
		int validite;
		
		ArrayList<DetailsOffreAppel> appel;
		ArrayList<DetailsOffreInternet> internet;
		ArrayList<DetailsOffreSms> sms;
		
		public int getIdOffre() {
			return idOffre;
		}
		public void setIdOffre(int idOffre) {
			this.idOffre = idOffre;
		}
		public int getIdOperateur() {
			return idOperateur;
		}
		public void setIdOperateur(int idOperateur) {
			this.idOperateur = idOperateur;
		}
		public String getNom() {
			return nom;
		}
		public void setNom(String nom) {
			this.nom = nom;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public int getPrix() {
			return prix;
		}
		public void setPrix(int prix) {
			this.prix = prix;
		}
		public int getValidite() {
			return validite;
		}
		public void setValidite(int validite) {
			this.validite = validite;
		}
		public ArrayList<DetailsOffreAppel> getAppel() {
			return appel;
		}
		public void setAppel(ArrayList<DetailsOffreAppel> appel) {
			this.appel = appel;
		}
		public ArrayList<DetailsOffreInternet> getInternet() {
			return internet;
		}
		public void setInternet(ArrayList<DetailsOffreInternet> internet) {
			this.internet = internet;
		}
		public ArrayList<DetailsOffreSms> getSms() {
			return sms;
		}
		public void setSms(ArrayList<DetailsOffreSms> sms) {
			this.sms = sms;
		}
		public Offre() {}
		public Offre(int idOffre, int idOperateur, String nom, String code, int prix, int validite) {
			setIdOffre(idOffre);
			setIdOperateur(idOperateur);
			setNom(nom);
			setCode(code);
			setPrix(prix);
			setValidite(validite);
		}
		public static ArrayList<Offre> findAllOffre(String sql,Connection co) throws Exception{
			PreparedStatement st = null;
			ResultSet result = null;
			ArrayList<Offre> array = new ArrayList<Offre>();
			try {
				st = co.prepareStatement(sql);
				result = st.executeQuery(); 
				while(result.next()) {
					int idOffre=result.getInt("idoffre");
					int idOperateur=result.getInt("idoperateur");
					String nom=result.getString("nom");
					String code=result.getString("code");
					int prix=result.getInt("prix");
					int validite=result.getInt("validite");
					Offre offre=new Offre(idOffre,idOperateur,nom,code,prix,validite);
					array.add(offre);
				}
			}catch(Exception e) {
				e.getMessage();
			}finally {
				if(st!=null) st.close();
			}
			return array;
	    }
		public static Response insertOffre(String token, String nom, String code, String prix, String validite) throws Exception {
			Connection co= new ConnectionPstg().getConnection();
			Response r= new Response();
			r.code= "200";
			r.data=null;
			String idOperateur= Token.verificationTokenAdmin(token, co);
			int idOp1= Integer.parseInt(idOperateur);
			int prix1= Integer.parseInt(prix);
			int validite1= Integer.parseInt(validite);
			PreparedStatement st = null;
			try {
				String sql= "insert into offre(idOffre,idOperateur,nom,code,prix,validite)VALUES (nextval('seqOffre'),?,?,?,?,?)";
				st = co.prepareStatement(sql);
				st.setInt(1,idOp1);
				st.setString(2,nom);
				st.setString(3,code);
				st.setInt(4,prix1);
				st.setInt(5,validite1);
				st.execute();
				co.commit();
				r.data=  getLastOffre(idOp1,co);
				r.message= "la nouvelle offre est creer ajouter les details";
				
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
		public static ArrayList<Offre> getLastOffre(int idOperateur,Connection co) throws Exception{
			String sql= "select * from offre where idOperateur="+idOperateur+" order by idOffre desc limit 1";
			ArrayList<Offre> mouvs= Offre.findAllOffre(sql, co);
			return mouvs;
	    }
		
		public static Response getListeOffre(String token) throws Exception{
			Connection co= new ConnectionPstg().getConnection();
			Response r= new Response();
			String idOperateur= Token.verificationTokenAdmin(token, co);
			try {
				String sql= "select * from offre where idOperateur="+idOperateur;
				ArrayList<Offre> offre= Offre.findAllOffre(sql, co);
				ArrayList<Offre> offreDetails= new ArrayList<Offre>();
				for(int i=0; i<offre.size();i++) {
					Offre offre2= Offre.detailsOffre(offre.get(i).getIdOffre(),co);
					offreDetails.add(offre2);
				}
				 
				r.data=  offreDetails;
				r.message= "liste des offres";
				
			} catch (Exception e) {
				r.code= "400";
				r.message= e.getMessage();
				e.printStackTrace();
			} finally {
				if(co!=null) co.close();
			}
			return  r;
	    }
	    public static Response getListeOffreById(String token) throws Exception{
			Connection co= new ConnectionPstg().getConnection();
			Response r= new Response();
			int idCompte= Token.verificationToken(token, co);
			int idOperateur=Compte.findCompteById(idCompte,co).getIdOperateur();
			try {
				String sql= "select * from offre where idOperateur="+idOperateur;
				ArrayList<Offre> offre= Offre.findAllOffre(sql, co);
				ArrayList<Offre> offreDetails= new ArrayList<Offre>();
				for(int i=0; i<offre.size();i++) {
					Offre offre2= Offre.detailsOffre(offre.get(i).getIdOffre(),co);
					offreDetails.add(offre2);
				}
				 
				r.data=  offreDetails;
				r.message= "liste des offres";
				
			} catch (Exception e) {
				r.code= "400";
				r.message= e.getMessage();
				e.printStackTrace();
			} finally {
				if(co!=null) co.close();
			}
			return  r;
	    }
		public static ArrayList<Offre> getOffreById(int idOffre,Connection co) throws Exception{
			String sql= "select * from offre where idOffre="+idOffre;
			ArrayList<Offre> mouvs= Offre.findAllOffre(sql, co);
			return mouvs;
	    }
		
		public static Offre detailsOffre(int idOffre,Connection co) throws Exception{
			Offre offre= new Offre();
			Offre offre1= Offre.getOffreById(idOffre,co).get(0);
			offre.idOffre= offre1.getIdOffre();
			offre.idOperateur= offre1.getIdOperateur();
			offre.code= offre1.getCode();
			offre.nom= offre1.getNom();
			offre.prix= offre1.getPrix();
			offre.validite= offre1.getValidite();
			offre.setAppel(DetailsOffreAppel.getDetailsOffreAppel(idOffre,co));
			offre.setInternet(DetailsOffreInternet.getDetailsOffreInternet(idOffre,co));
			offre.setSms(DetailsOffreSms.getDetailsOffreSms(idOffre,co));
			return offre;
	    }
		public static Offre getOffreByCode(String code,Connection co) throws Exception{
			String sql= "select * from offre where code='"+code+"'";
			ArrayList<Offre> offre= Offre.findAllOffre(sql, co);
			if(offre.size()<1) throw new Exception("code invalide");
			return offre.get(0);
	    }
		public static Response getDetailsOffre(String idOffre) throws Exception{
			Connection co= new ConnectionPstg().getConnection();
			Response r= new Response();
			r.code= "200";
			r.data=null;
			int id= Integer.parseInt(idOffre);;
			try {
				r.data=  detailsOffre(id,co);
				r.message= "Details des offres";
				
			} catch (Exception e) {
				r.code= "400";
				r.message= e.getMessage();
				e.printStackTrace();
			} finally {
				if(co!=null) co.close();
			}
			return  r;
	    }
		
		public static Response deleteOffre(String idOffre) throws Exception {
			Connection co= new ConnectionPstg().getConnection();
			Response r= new Response();
			r.code= "200";
			r.data=null;
			int idOffre1= Integer.parseInt(idOffre);
			PreparedStatement st = null;
			try {
				String sql= " delete from offre where idOffre=?";
				st = co.prepareStatement(sql);
				st.setInt(1,idOffre1);
				st.execute();
				co.commit();
				r.data= null ;
				r.message= "Offre supprimer";
				
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
		public static void upDateOffre(String idOffre, String nom, String code, String prix, String validite,Connection co) throws Exception {
			PreparedStatement st = null;
			int idOffre1= Integer.parseInt(idOffre);
			int prix1= Integer.parseInt(prix);
			int validite1= Integer.parseInt(validite);
			try {
				String sql= "update offre set nom=?, code=?, prix=?, validite=? where idOffre=?";
				st = co.prepareStatement(sql);
				st.setString(1,nom);
				st.setString(2,code);
				st.setInt(3,prix1);
				st.setInt(4,validite1);
				st.setInt(5,idOffre1);
				st.execute();
				co.commit();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(st != null) st.close();
			}
		}
		public static Response updateOffre(String idOffre, String nom, String code, String prix, String validite) throws Exception {
			Connection co= new ConnectionPstg().getConnection();
			Response r= new Response();
			r.code= "200";
			r.data=null;
			try {
				Offre.upDateOffre(idOffre,nom, code, prix, validite, co);
				r.data= Offre.getDetailsOffre(idOffre);
				r.message= "update offre Effectuer";
				
			} catch (Exception e) {
				r.code= "400";
				r.message= e.getMessage();
				e.printStackTrace();
			} finally {
				if(co!=null) co.close();
			}
			return  r;
		}
	}
