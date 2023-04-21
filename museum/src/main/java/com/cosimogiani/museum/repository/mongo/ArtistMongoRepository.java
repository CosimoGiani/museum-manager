package com.cosimogiani.museum.repository.mongo;

import org.bson.Document;

import com.cosimogiani.museum.repository.ArtistRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ArtistMongoRepository {
	
	private MongoCollection<Document> artistCollection;
	private ClientSession session;
	
	public ArtistMongoRepository(MongoClient client, ClientSession session, String dbName, String artistCollectionName) {
		MongoDatabase db = client.getDatabase(dbName);
		artistCollection = db.getCollection(artistCollectionName);
		this.session = session;
	}

}
