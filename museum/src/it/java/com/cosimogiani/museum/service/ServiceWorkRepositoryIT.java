package com.cosimogiani.museum.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.repository.mongo.ArtistMongoRepository;
import com.cosimogiani.museum.repository.mongo.WorkMongoRepository;
import com.cosimogiani.museum.transaction.TransactionManager;
import com.cosimogiani.museum.transaction.mongo.TransactionManagerMongo;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;

public class ServiceWorkRepositoryIT {
	
	private static final String MUSEUM_DB_NAME = "museum";
	private static final String ARTIST_COLLECTION_NAME = "artist";
	private static final String WORK_COLLECTION_NAME = "work";
	
	private Artist ARTIST_FIXTURE_1;
	private Artist ARTIST_FIXTURE_2;
	private Work WORK_FIXTURE_1;
	private Work WORK_FIXTURE_2;
	
	private MongoClient client;
	private ClientSession session;
	private TransactionManager transactionManager;
	private WorkService workService;
	private WorkMongoRepository workRepository;
	private ArtistMongoRepository artistRepository;
	
	@Before
	public void setup() {
		client = new MongoClient("localhost");
		session = client.startSession();
		transactionManager = new TransactionManagerMongo(client, session, MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME, WORK_COLLECTION_NAME);
		workService = new WorkServiceTransactional(transactionManager);
		artistRepository = new ArtistMongoRepository(client, session, MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME);
		workRepository = new WorkMongoRepository(client, session, MUSEUM_DB_NAME, WORK_COLLECTION_NAME, artistRepository);
		for (Artist artist : artistRepository.findAllArtists()) {
			artistRepository.deleteArtist(artist.getId());
		}
		for (Work work : workRepository.findAllWorks()) {
			workRepository.deleteWork(work.getId());
		}
		ARTIST_FIXTURE_1 = artistRepository.saveArtist(new Artist("test1"));
		ARTIST_FIXTURE_2 = artistRepository.saveArtist(new Artist("test2"));
		WORK_FIXTURE_1 = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		WORK_FIXTURE_2 = new Work(ARTIST_FIXTURE_2, "title2", "type2", "description2");
	}
	
	@After
	public void tearDown() {
		client.close();
	}
	
	@Test
	public void testFindAllWorks() {
		workRepository.saveWork(WORK_FIXTURE_1);
		workRepository.saveWork(WORK_FIXTURE_2);
		List<Work> works = workService.findAllWorks();
		assertThat(works).containsExactly(WORK_FIXTURE_1, WORK_FIXTURE_2);
	}
	
	@Test
	public void testFindWorksByArtist() {
		workRepository.saveWork(WORK_FIXTURE_1);
		workRepository.saveWork(WORK_FIXTURE_2);
		List<Work> works = workService.findWorksByArtist(ARTIST_FIXTURE_1);
		assertThat(works).containsOnly(WORK_FIXTURE_1);
	}
	
	@Test
	public void testAddWork() {
		Work workAdded = workService.addWork(WORK_FIXTURE_1);
		Work workFound = workRepository.findWorkById(workAdded.getId());
		assertThat(workFound).isEqualTo(workAdded);
	}
	
	@Test
	public void testDeleteWork() {
		Work workToDelete = workRepository.saveWork(WORK_FIXTURE_1);
		workRepository.saveWork(WORK_FIXTURE_2);
		workService.deleteWork(workToDelete);
		Work workFound = workRepository.findWorkById(workToDelete.getId());
		assertThat(workFound).isNull();
	}

}
