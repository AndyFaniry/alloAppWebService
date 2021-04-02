package com.mobilemoney.bdb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class ConnectionMongo {
	public ConnectionMongo() {}
	public MongoDatabase connect() {
		ConnectionString connString = new ConnectionString(
			    "mongodb+srv://mobile:mobile123456@mobilemoney.ayfpc.mongodb.net/mobiledb?w=majority"
			);

			MongoClientSettings settings = MongoClientSettings.builder()
			    .applyConnectionString(connString)
			    .retryWrites(true)
			    .build();
			MongoClient mongoClient = MongoClients.create(settings);
			MongoDatabase database = mongoClient.getDatabase("mobiledb");
			return database;
	}
	
}
