package com.mobilemoney.controller;


import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.mobilemoney.model.Credit;
import com.mobilemoney.model.Response;
import com.mobilemoney.model.Token;
@CrossOrigin(origins="*",allowedHeaders="*")
@RestController
public class CreditController {
	@PostMapping(value="/client/achat/credit")
	public Response createClient(@RequestHeader("Authorization") String bearertoken,@RequestBody Map<String,String> donner) throws Exception {
		String token= Token.deleteBearerToToken(bearertoken);;
		String code= donner.get("code");
		return Credit.ajoutCreditWebService(code,token);
	}
}
