package com.mobilemoney.model;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.mobilemoney.bdb.ConnectionPstg;
import com.mobilemoney.controller.ClientController;
import com.mobilemoney.fonction.Fonction;
import com.mobilemoney.model.mouvement.*;
import com.mobilemoney.model.offre.*;
import com.mobilemoney.model.simulation.SimulationAppel;
import com.mobilemoney.model.simulation.SimulationSms;
import com.mobilemoney.model.simulation.Tarif;


public class Main {
	private static final DateTimeFormatter DateTimeFormatter = null;
	@Autowired
	public ClientRepository clientRepository;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Connection co= new ConnectionPstg().getConnection();
		String retour="debut";
		try {
			int idCompte=1;
			String daty="2021-03-26T14:12:21";
			String numCompose= "0343433434";
			String msg="jkx rtyuiopkjghgfcvb sdfghjkhrdc khjghfswdfygb gdrtrhtyfgjhn xsd ghvhsbc";
			int nbr= SimulationSms.getNbrMsg(msg);
			int prix= SimulationSms.prixSms(idCompte, numCompose,msg, co);
			int valeur= SimulationSms.valeurOffreSms(idCompte,daty,co);
			SimulationSms.payerParCredit(idCompte,prix,daty,co);
			SimulationSms sim=SimulationSms.simulationSms(idCompte, numCompose,msg,daty, co);
			System.out.println("nbr= "+sim.getMessage());
		}
		catch(Exception ex) {
			retour= ex.getMessage();
			ex.printStackTrace();
		}finally {
			if(co != null) co.close();
			System.out.println(retour);
		}
		

	}
}
