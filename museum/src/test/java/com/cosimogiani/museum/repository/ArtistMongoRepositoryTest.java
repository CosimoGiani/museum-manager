package com.cosimogiani.museum.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;

import com.cosimogiani.museum.model.Artist;
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
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
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
	public void testFindAllArtistsWhenDatabaseIsEmpty() {
		List<Artist> artists = artistRepository.findAllArtists();
		assertThat(artists).isEmpty();
	}
	
	@Test
	public void testFindAllArtistsWhenDatabaseIsNotEmpty() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		addTestArtistToDatabase(artist1);
		addTestArtistToDatabase(artist2);
		List<Artist> artists = artistRepository.findAllArtists();
		assertThat(artists).containsExactly(artist1, artist2);
	}
	
	@Test
	public void testFindArtistByIdNotFound() {
		Artist artistFound = artistRepository.findArtistById("" + new ObjectId());
		assertThat(artistFound).isNull();
	}
	
	@Test
	public void testFindArtistByIdFound() {
		Artist artist = new Artist("test1");
		String idArtist = addTestArtistToDatabase(artist);
		addTestArtistToDatabase(new Artist("test2"));
		Artist artistFound = artistRepository.findArtistById(idArtist);
		assertThat(artistFound).isEqualTo(artist);
	}
	
	@Test
	public void testSaveArtist() {
		Artist artistToAdd = new Artist("test");
		Artist artistAdded = artistRepository.saveArtist(artistToAdd);
		assertThat(artistAdded).isEqualTo(artistToAdd);
		assertThat(readAllArtistsFromDatabase()).containsExactly(artistToAdd);
		
	}
	
	@Test
	public void testDeleteArtistWhenArtistExistsInDatabase() {
		Artist artist = new Artist("test");
		String idArtistToDelete = addTestArtistToDatabase(artist);
		artistRepository.deleteArtist(idArtistToDelete);
		assertThat(readAllArtistsFromDatabase()).isEmpty();
	}
	
	private String addTestArtistToDatabase(Artist artistToAdd) {
		Document artist = new Document().append("name", artistToAdd.getName());
		artistCollection.insertOne(artist);
		artistToAdd.setId(artist.get("_id").toString());
		return artist.get("_id").toString();
	}
	
	private List<Artist> readAllArtistsFromDatabase() {
		return StreamSupport
				.stream(artistCollection.find().spliterator(), false)
				.map(d -> new Artist(d.get("_id").toString(), d.getString("name")))
				.collect(Collectors.toList());
	}
	
}
