package com.mobilemoney.model.mouvement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class MouvementCredit {
	public static void insertmouvCredit(int idCompte, int valeur,LocalDateTime dateDebut,LocalDateTime dateFin,Connection co ) throws Exception {
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
