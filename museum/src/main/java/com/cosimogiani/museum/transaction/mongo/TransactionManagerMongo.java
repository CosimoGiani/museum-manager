package com.cosimogiani.museum.transaction.mongo;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.cosimogiani.museum.repository.mongo.ArtistMongoRepository;
import com.cosimogiani.museum.repository.mongo.WorkMongoRepository;
import com.cosimogiani.museum.transaction.TransactionFunction;
import com.cosimogiani.museum.transaction.TransactionManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.ClientSession;
import com.mongodb.client.TransactionBody;

public class TransactionManagerMongo implements TransactionManager {
	
	private static final Logger LOGGER = Logger.getLogger(TransactionManagerMongo.class.getName());
	
	private MongoClient client;
	private ClientSession session;
	private String dbName;
	private String artistCollectionName;
	private String workCollectionName;
	
	public TransactionManagerMongo(MongoClient client, ClientSession session, String dbName, 
			String artistCollectionName, String workCollectionName) {
		this.client = client;
		this.session = session;
		this.dbName = dbName;
		this.artistCollectionName = artistCollectionName;
		this.workCollectionName = workCollectionName;
	}
	
	@Override
	public <T> T doInTransaction(TransactionFunction<T> code) {
		TransactionOptions tOptions = TransactionOptions.builder()
				.readPreference(ReadPreference.primary())
				.readConcern(ReadConcern.LOCAL)
				.writeConcern(WriteConcern.MAJORITY)
				.build();
		try {
			ArtistMongoRepository artistRepository = new ArtistMongoRepository(client, session, dbName, artistCollectionName);
			WorkMongoRepository workRepository = new WorkMongoRepository(client, session, dbName, workCollectionName, artistRepository);
			TransactionBody<T> tBody = () -> code.apply(artistRepository, workRepository);
			return session.withTransaction(tBody, tOptions);
		}
		catch (MongoException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			return null;
		}
	}

}
