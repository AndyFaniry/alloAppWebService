package com.mobilemoney.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mobilemoney.model.Client;
import com.mobilemoney.model.Compte;
import com.mobilemoney.model.Credit;
import com.mobilemoney.model.MouvementMoney;
import com.mobilemoney.model.Operateur;
import com.mobilemoney.model.Response;
import com.mobilemoney.model.Solde;
import com.mobilemoney.model.Token;
import com.mobilemoney.model.offre.AchatOffre;
import com.mobilemoney.model.simulation.SimulationAppel;
import com.mobilemoney.model.simulation.SimulationInternet;
import com.mobilemoney.model.simulation.SimulationSms;

@CrossOrigin(origins="*",allowedHeaders="*")
@RestController
public class CompteController {
	@PostMapping(value="/compte/login")
	public Response loginClient(@RequestBody Compte compte) throws Exception {
		return compte.login();
	}
	@PostMapping(value="/compte/deconnect")
	public Response deconnectAdmin(@RequestHeader("Authorization") String bearertoken) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);
		return Compte.deconnect(token);
	}
	@PostMapping(value="/compte/inscription")
	public Response inscriptionClient(@RequestBody Map<String,String> compte) throws Exception {
		Compte cmp=new Compte(Integer.parseInt(compte.get("idoperateur")),compte.get("mdp"));
		return cmp.insertCompte(compte.get("idoperateur"),compte.get("nom"), compte.get("email"));
	}
	@GetMapping("/compte/getcompte")
	public Response getClient(@RequestHeader("Authorization") String bearertoken) throws Exception{
		String token= Token.deleteBearerToToken(bearertoken);
		return Compte.getCompte(token);
	}
	@PostMapping(value="/compte/depot")
	public Response depot(@RequestHeader("Authorization") String bearertoken,@RequestBody Map<String,String> valeur) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);
		String valeur1= valeur.get("valeur");
		Response r= Compte.depotMoney(token,valeur1);
		return r;
	}
	@PostMapping(value="/compte/achat/offre")
	public Response achatOffre(@RequestHeader("Authorization") String bearertoken,@RequestBody Map<String,String> donner) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);
		String idOffre= donner.get("idOffre");
		String daty= LocalDateTime.now().toString();
		String mode= donner.get("modePayement");
		Response r= AchatOffre.achatOffre(token, idOffre,daty,mode);
		return r;
	}
	@PostMapping(value="/compte/achat/offre/code")
	public Response achatOffrecode(@RequestHeader("Authorization") String bearertoken,@RequestBody Map<String,String> donner) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);
		String code= donner.get("code");
		String daty= donner.get("daty");
		String mode= donner.get("modePayement");
		Response r= AchatOffre.achatOffreCode(token,code,daty,mode);
		return r;
	}
	@GetMapping(value="/compte/solde/credit")
	public Response soldeCredit(@RequestHeader("Authorization") String bearertoken) throws Exception{
		String token= Token.deleteBearerToToken(bearertoken);
		return Solde.getSoldeWebService(token);
	}
	@GetMapping(value="/compte/solde/mobileMoney")
	public Response soldeMobileMoney(@RequestHeader("Authorization") String bearertoken) throws Exception{
		String token= Token.deleteBearerToToken(bearertoken);
		return Solde.getSoldeCompte(token);
	}
	@PostMapping(value="/compte/appel")
	public Response simulationAppel(@RequestHeader("Authorization") String bearertoken,@RequestBody Map<String,String> donner) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);
		String numCompose= donner.get("numCompose");
		String duree= donner.get("duree");
		String daty= LocalDateTime.now().toString();
		Response r= SimulationAppel.historiqueAppel(token, numCompose, duree, daty);
		return r;
	}
	@GetMapping(value="/compte/solde")
	public Response solde(@RequestHeader("Authorization") String bearertoken) throws Exception{
		String token= Token.deleteBearerToToken(bearertoken);
		return Compte.solde(token);
	}
	@PostMapping(value="/compte/internet")
	public Response simulationIntener(@RequestHeader("Authorization") String bearertoken,@RequestBody Map<String,String> donner) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);
		String types= donner.get("types");
		String daty= LocalDateTime.now().toString();
		String mo= donner.get("mo");
		Response r= SimulationInternet.webSerciceSimulationInternet(token,mo,types,daty);
		return r;
	}
	@PostMapping(value="/compte/sms")
	public Response simulationSms(@RequestHeader("Authorization") String bearertoken,@RequestBody Map<String,String> donner) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);
		String numCompose= donner.get("numCompose");
		String daty= LocalDateTime.now().toString();
		String msg= donner.get("msg");
		Response r= SimulationSms.historiqueSms(token,numCompose, msg,daty);
		return r;
	}
}
