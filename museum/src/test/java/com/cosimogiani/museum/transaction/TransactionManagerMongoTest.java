package com.cosimogiani.museum.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.MongoDBContainer;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.transaction.mongo.TransactionManagerMongo;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class TransactionManagerMongoTest {
	
	private static final String MUSEUM_DB_NAME = "museum";
	private static final String ARTIST_COLLECTION_NAME = "artist";
	private static final String WORK_COLLECTION_NAME = "work";
	
	@ClassRule
	public static final MongoDBContainer mongo = new MongoDBContainer("mongo:4.4.3");
	
	private MongoClient client;
	private ClientSession session;
	private MongoCollection<Document> artistCollection;
	private MongoCollection<Document> workCollection;
	private TransactionManagerMongo transactionManager;
	
	@Before
	public void setup() {
		client = spy(new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017))));
		MongoDatabase database = client.getDatabase(MUSEUM_DB_NAME);
		database.drop();
		database.createCollection(ARTIST_COLLECTION_NAME);
		database.createCollection(WORK_COLLECTION_NAME);
		artistCollection = database.getCollection(ARTIST_COLLECTION_NAME);
		workCollection = database.getCollection(WORK_COLLECTION_NAME);
		transactionManager = new TransactionManagerMongo(client, MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME, WORK_COLLECTION_NAME);
		session = client.startSession();
		when(client.startSession()).thenReturn(session);
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testDoInTransactionWhenCommitSuccessfully() {
		List<String> idsElementsAdded = transactionManager.doInTransaction(
				(artistRepository, workRepository) -> {
					String idArtistAdded = addTestArtistToDatabase("test");
					String idWorkAdded = addTestWorkToDatabase(idArtistAdded, "title", "type", "description");
					return new ArrayList<String>(Arrays.asList(idArtistAdded, idWorkAdded));
				});
		List<Artist> artists = readAllArtistsFromDatabase();
		assertThat(artists).containsOnly(new Artist(idsElementsAdded.get(0), "test"));
		List<Work> works = readAllWorksFromDatabase();
		assertThat(works).containsOnly(
				new Work(idsElementsAdded.get(1), new Artist(idsElementsAdded.get(0), "test"), "title", "type", "description"));
	}
	
	@Test
	public void testDoInTransactionWhenRollBack() {
		transactionManager.doInTransaction(
				(artistRepository, workRepository) -> {
					String idArtistAdded = addTestArtistToDatabase("test");
					addTestWorkToDatabase(idArtistAdded, "title", "type", "description");
					throw new MongoException("Error: abort transaction.");
				});
		List<Artist> artists = readAllArtistsFromDatabase();
		assertThat(artists).isEmpty();
		List<Work> works = readAllWorksFromDatabase();
		assertThat(works).isEmpty();
	}
	
	private String addTestArtistToDatabase(String name) {
		Document artistToAdd = new Document().append("name", name);
		artistCollection.insertOne(session, artistToAdd);
		return artistToAdd.get("_id").toString();
	}
	
	private String addTestWorkToDatabase(String artistId, String title, String type, String description) {
		Document workToAdd = new Document()
				.append("artist", new DBRef(ARTIST_COLLECTION_NAME, new ObjectId(artistId)))
				.append("title", title)
				.append("type", type)
				.append("description", description);
		workCollection.insertOne(session, workToAdd);
		return workToAdd.get("_id").toString();
	}
	
	private List<Artist> readAllArtistsFromDatabase() {
		return StreamSupport
				.stream(artistCollection.find().spliterator(), false)
				.map(d -> new Artist(d.get("_id").toString(), d.getString("name")))
				.collect(Collectors.toList());
	}
	
	private Artist findArtistById(String artistId) {
		Document d = artistCollection.find(session, Filters.eq("_id", new ObjectId(artistId))).first();
		if (d != null) {
			return new Artist(d.get("_id").toString(), d.getString("name"));
		}
		return null;
	}
	
	private List<Work> readAllWorksFromDatabase() {
		return StreamSupport
				.stream(workCollection.find().spliterator(), false)
				.map(d -> new Work(d.get("_id").toString(),
						findArtistById(((DBRef) d.get("artist")).getId().toString()),
						d.get("title").toString(),
						d.get("type").toString(),
						d.get("description").toString()))
				.collect(Collectors.toList());
	}
	
}
