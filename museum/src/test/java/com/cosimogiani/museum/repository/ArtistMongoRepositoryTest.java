package com.cosimogiani.museum.repository;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.cosimogiani.museum.repository.mongo.ArtistMongoRepository;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ArtistMongoRepositoryTest {
	
	private static final String MUSEUM_DB_NAME = "museum";
	private static final String ARTIST_COLLECTION_NAME = "artist";
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("candis/mongo-replica-set:0.0.2").withExposedPorts(27017);
	
	private MongoClient client;
	private ArtistMongoRepository artistRepository;
	private MongoCollection<Document> artistCollection;
	
	@BeforeClass
	public static void init() throws InterruptedException {
		TimeUnit.MINUTES.sleep(1);
	}
	
	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getFirstMappedPort()));
		MongoDatabase database = client.getDatabase(MUSEUM_DB_NAME);
		database.drop();
		database.createCollection(ARTIST_COLLECTION_NAME);
		artistRepository = new ArtistMongoRepository(client, client.startSession(), MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME);
		artistCollection = database.getCollection(ARTIST_COLLECTION_NAME);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void test() {}

}
