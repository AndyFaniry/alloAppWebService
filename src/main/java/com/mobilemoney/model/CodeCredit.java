package com.mobilemoney.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CodeCredit {
	int idCode;
	String code;
	int valeur;
	public int getIdCode() {
		return idCode;
	}
	public void setIdCode(int idCode) {
		this.idCode = idCode;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getValeur() {
		return valeur;
	}
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}
	public CodeCredit(int idCode, String code, int valeur) {
		super();
		this.idCode = idCode;
		this.code = code;
		this.valeur = valeur;
	}
	public static ArrayList<CodeCredit> findAllCodeCredit(String sql,Connection co) throws Exception{
		PreparedStatement st = null;
		ResultSet resultSet = null;
		ArrayList<CodeCredit> crd=new ArrayList<CodeCredit>();
		try {
			st = co.prepareStatement(sql);
			resultSet = st.executeQuery();
			while (resultSet.next()) {
				int idCredit=resultSet.getInt("idCode");
				String code= resultSet.getString("code");
				int valeur=resultSet.getInt("valeur");
				CodeCredit credit=new CodeCredit(idCredit,code,valeur);
				crd.add(credit);
			}
		}catch(Exception e) {
			e.getMessage();
		}finally {
			if(st != null) st.close();
		}
		return crd;
    }
	public static int getValeurCredit(String code, Connection co) {
		return 1;
	}

}
