package com.mobilemoney.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.mobilemoney.bdb.ConnectionPstg;

public class MouvementMoney {
	int idMobileMoney;
	int idOperateur;
	String nom;
	int idCompte;
	String num;
	Double valeur;
	LocalDateTime daty;
	int statu;

	public int getIdMobileMoney() {
		return idMobileMoney;
	}
	public void setIdMobileMoney(int idMobileMoney) {
		this.idMobileMoney = idMobileMoney;
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
	public int getIdCompte() {
		return idCompte;
	}
	public void setIdCompte(int idCompte) {
		this.idCompte = idCompte;
	}
	
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	public Double getValeur() {
		return valeur;
	}
	public void setValeur(Double valeur) {
		this.valeur = valeur;
	}
	public LocalDateTime getDaty() {
		return daty;
	}
	public void setDaty(LocalDateTime daty) {
		this.daty = daty;
	}
	public int getStatu() {
		return statu;
	}
	public void setStatu(int statu) {
		this.statu = statu;
	}
	public MouvementMoney(int idMobileMoney, int idOperateur, String nom, int idCompte, String num, Double valeur,
			LocalDateTime daty, int statu) {
		setIdMobileMoney(idMobileMoney);
		setIdOperateur(idOperateur);
		setNom(nom);
		setIdCompte(idCompte);
		setNum(num);
		setValeur(valeur);
		setDaty(daty);
		setStatu(statu);
		
	}
	public static ArrayList<MouvementMoney> findMouvementMoney(String sql,Connection co){
		System.out.println("ssqqll= "+sql);
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		ArrayList<MouvementMoney> mouvs=new ArrayList<MouvementMoney>();
		try {
			preparedStatement = co.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				int idMouv= resultSet.getInt("idmobilemoney");
				int idOperateur= resultSet.getInt("idoperateur");
				String nom= resultSet.getString("nom");
				int idCompte= resultSet.getInt("idcompte");
				String num= resultSet.getString("num");
				Double valeur= resultSet.getDouble("valeur");
				LocalDateTime daty= resultSet.getTimestamp("daty").toLocalDateTime();
				int statu= resultSet.getInt("statu");
				MouvementMoney mouv=new MouvementMoney(idMouv,idOperateur,nom,idCompte,num,valeur,daty,statu);
				mouvs.add(mouv);
			}
		}catch(Exception e) {
			e.getMessage();
		}
		return mouvs;
    }
	
	
	
}
