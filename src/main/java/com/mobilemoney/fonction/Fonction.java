package com.mobilemoney.fonction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class Fonction {
	public static String getDateNow(Connection co) throws Exception {
		String sql="SELECT CURRENT_TIMESTAMP as now";
		PreparedStatement st = null;
		String daty= "";
		try {
			st = co.prepareStatement(sql);
			ResultSet result = st.executeQuery(); 
			while(result.next()) {
				 daty = result.getString("now");			
			}	
			result.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
		return daty;
	}
	public static String addSha1(String mdp,Connection co) throws Exception {
		String sql="select md5('"+mdp+"')";
		PreparedStatement st = null;
		String sha1= "";
		try {
			st = co.prepareStatement(sql);
			ResultSet result = st.executeQuery(); 
			while(result.next()) {
				 sha1 = result.getString("md5");			
			}	
			result.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(st != null) st.close();
		}
		return sha1;
	}
	public static LocalDateTime setLocalDateTime(String datyFORM) {
		String[] dh= datyFORM.split("T");
		String[] daty= dh[0].split("-");
		int year= Integer.parseInt(daty[0]);
		int month= Integer.parseInt(daty[1]);
		int day= Integer.parseInt(daty[2]);
		
		String[] heur= dh[1].split(":");
		int h= Integer.parseInt(heur[0]);
		int mn= Integer.parseInt(heur[1]);
		int s= Integer.parseInt(heur[2]);
		
		LocalDateTime local= LocalDateTime.of(year, month, day,h,mn,s);
		return local;
	}
}
