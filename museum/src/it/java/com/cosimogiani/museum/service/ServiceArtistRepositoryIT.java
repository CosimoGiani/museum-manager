package com.cosimogiani.museum.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.repository.mongo.ArtistMongoRepository;
import com.cosimogiani.museum.transaction.TransactionManager;
import com.cosimogiani.museum.transaction.mongo.TransactionManagerMongo;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;

public class ServiceArtistRepositoryIT {
	
	private static final String MUSEUM_DB_NAME = "museum";
	private static final String ARTIST_COLLECTION_NAME = "artist";
	private static final String WORK_COLLECTION_NAME = "work";
	
	private static final Artist ARTIST_FIXTURE_1 = new Artist("test1");
	private static final Artist ARTIST_FIXTURE_2 = new Artist("test2");
	
	private MongoClient client;
	private ClientSession session;
	private TransactionManager transactionManager;
	private ArtistService artistService;
	private ArtistMongoRepository artistRepository;
	
	@Before
	public void setup() {
		client = new MongoClient("localhost");
		session = client.startSession();
		transactionManager = new TransactionManagerMongo(client, session, MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME, WORK_COLLECTION_NAME);
		artistService = new ArtistServiceTransactional(transactionManager);
		artistRepository = new ArtistMongoRepository(client, session, MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME);
		for (Artist artist : artistRepository.findAllArtists()) {
			artistRepository.deleteArtist(artist.getId());
		}
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testFindAllArtists() {
		artistRepository.saveArtist(ARTIST_FIXTURE_1);
		artistRepository.saveArtist(ARTIST_FIXTURE_2);
		List<Artist> artists = artistService.findAllArtists();
		assertThat(artists).containsExactly(ARTIST_FIXTURE_1, ARTIST_FIXTURE_2);
	}
	
	@Test
	public void testAddArtist() {
		Artist artistAdded = artistService.addArtist(ARTIST_FIXTURE_1);
		Artist artistFound = artistRepository.findArtistById(artistAdded.getId());
		assertThat(artistFound).isEqualTo(ARTIST_FIXTURE_1);
	}
	
	@Test
	public void testDeleteArtistWhenClientExistsInDatabase() {
		Artist artistToDelete = artistRepository.saveArtist(ARTIST_FIXTURE_1);
		artistRepository.saveArtist(ARTIST_FIXTURE_2);
		artistService.deleteArtist(artistToDelete.getId());
		Artist artistFound = artistRepository.findArtistById(artistToDelete.getId());
		assertThat(artistFound).isNull();
	}

}
