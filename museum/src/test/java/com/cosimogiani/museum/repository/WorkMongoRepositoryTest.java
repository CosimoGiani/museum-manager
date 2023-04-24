package com.cosimogiani.museum.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testcontainers.containers.GenericContainer;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.repository.mongo.ArtistMongoRepository;
import com.cosimogiani.museum.repository.mongo.WorkMongoRepository;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class WorkMongoRepositoryTest {
	
	private static final String MUSEUM_DB_NAME = "museum";
	private static final String ARTIST_COLLECTION_NAME = "artist";
	private static final String WORK_COLLECTION_NAME = "work";
	
	private static final Artist ARTIST_FIXTURE_1 = new Artist(new ObjectId().toString(), "test");
	private static final Artist ARTIST_FIXTURE_2 = new Artist(new ObjectId().toString(), "test2");
	
	@SuppressWarnings("rawtypes")
	@ClassRule
	public static final GenericContainer mongo = new GenericContainer("candis/mongo-replica-set:0.0.2").withExposedPorts(27017);
	
	private MongoClient client;
	private MongoCollection<Document> workCollection;
	private AutoCloseable closeable;
	
	@Mock
	private ArtistMongoRepository artistRepository;
	
	@InjectMocks
	private WorkMongoRepository workRepository;
	
	@BeforeClass
	public static void init() throws InterruptedException {
		TimeUnit.MINUTES.sleep(1);
	}
	
	@Before
	public void setup() {
		client = new MongoClient(new ServerAddress(mongo.getContainerIpAddress(), mongo.getMappedPort(27017)));
		MongoDatabase database = client.getDatabase(MUSEUM_DB_NAME);
		database.drop();
		database.createCollection(WORK_COLLECTION_NAME);
		workRepository = new WorkMongoRepository(client, client.startSession(), MUSEUM_DB_NAME, WORK_COLLECTION_NAME, artistRepository);
		closeable = MockitoAnnotations.openMocks(this);
		workCollection = database.getCollection(WORK_COLLECTION_NAME);
		when(artistRepository.findArtistById(ARTIST_FIXTURE_1.getId())).thenReturn(ARTIST_FIXTURE_1);
		when(artistRepository.findArtistById(ARTIST_FIXTURE_2.getId())).thenReturn(ARTIST_FIXTURE_2);
	}
	
	@After
	public void tearDown() throws Exception {
		client.close();
		closeable.close();
	}
	
	@Test
	public void testFindAllWorksWhenDatabaseIsEmpty() {
		List<Work> works = workRepository.findAllWorks();
		assertThat(works).isEmpty();
	}
	
	@Test
	public void testFindAllWorksWhenDatabaseIsNotEmpty() {
		Work work1 = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		Work work2 = new Work(ARTIST_FIXTURE_1, "title2", "type2", "description2");
		String idWork1 = addTestWorkToDatabase(work1);
		String idWork2 = addTestWorkToDatabase(work2);
		List<Work> works = workRepository.findAllWorks();
		assertThat(works).containsExactly(
				new Work(idWork1, ARTIST_FIXTURE_1, "title1", "type1", "description1"),
				new Work(idWork2, ARTIST_FIXTURE_1, "title2", "type2", "description2"));
	}
	
	@Test
	public void testFindWorksByArtistWhenDatabaseIsEmpty() {
		List<Work> works = workRepository.findWorksByArtist(ARTIST_FIXTURE_1);
		assertThat(works).isEmpty();
	}
	
	@Test
	public void testFindWorksByArtistWhenDatabaseIsNotEmptyAndWorksBelongToSameArtist() {
		Work work1 = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		Work work2 = new Work(ARTIST_FIXTURE_1, "title2", "type2", "description2");
		String idWork1 = addTestWorkToDatabase(work1);
		String idWork2 = addTestWorkToDatabase(work2);
		List<Work> works = workRepository.findWorksByArtist(ARTIST_FIXTURE_1);
		assertThat(works).containsExactly(
				new Work(idWork1, ARTIST_FIXTURE_1, "title1", "type1", "description1"),
				new Work(idWork2, ARTIST_FIXTURE_1, "title2", "type2", "description2"));
	}
	
	@Test
	public void testFindWorksByArtistWhenDatabaseIsNotEmptyAndWorksDoNotBelongToSameArtist() {
		Work work1 = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		Work work2 = new Work(ARTIST_FIXTURE_2, "title2", "type2", "description2");
		String idWork1 = addTestWorkToDatabase(work1);
		addTestWorkToDatabase(work2);
		List<Work> works = workRepository.findWorksByArtist(ARTIST_FIXTURE_1);
		assertThat(works).containsOnly(new Work(idWork1, ARTIST_FIXTURE_1, "title1", "type1", "description1"));
	}
	
	@Test
	public void testFindWorkByIdNotFound() {
		Work workFound = workRepository.findWorkById("" + new ObjectId());
		assertThat(workFound).isNull();
	}
	
	@Test
	public void testFindWorkByIdFound() {
		Work work1 = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		Work work2 = new Work(ARTIST_FIXTURE_2, "title2", "type2", "description2");
		String idWorkToFind = addTestWorkToDatabase(work1);
		addTestWorkToDatabase(work2);
		Work workFound = workRepository.findWorkById(idWorkToFind);
		assertThat(workFound).isEqualTo(new Work(idWorkToFind, ARTIST_FIXTURE_1, "title1", "type1", "description1"));
	}
	
	@Test
	public void testFindWorkByArtistAndTitleWhenNoWorksOfArtistAreInDatabase() {
		addTestWorkToDatabase(new Work(ARTIST_FIXTURE_2, "title1", "type1", "description1"));
		addTestWorkToDatabase(new Work(ARTIST_FIXTURE_2, "title2", "type2", "description2"));
		Work work = workRepository.findWorkByArtistAndTitle(ARTIST_FIXTURE_1, "title");
		assertThat(work).isNull();
	}
	
	@Test
	public void testFindWorkByArtistAndTitleWhenOnlyWorkOfArtistIsInDatabase() {
		Work work = new Work(ARTIST_FIXTURE_1, "title", "type", "description");
		String idWork = addTestWorkToDatabase(work);
		Work workFound = workRepository.findWorkByArtistAndTitle(ARTIST_FIXTURE_1, "title");
		assertThat(workFound).isEqualTo(new Work(idWork, ARTIST_FIXTURE_1, "title", "type", "description"));
	}
	
	@Test
	public void testFindWorkByArtistAndTitleWhenWorksOfMultipleArtistsAreInDatabase() {
		Work work1 = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		Work work2 = new Work(ARTIST_FIXTURE_2, "title2", "type2", "description2");
		String idWorkToFind = addTestWorkToDatabase(work1);
		addTestWorkToDatabase(work2);
		Work workFound = workRepository.findWorkByArtistAndTitle(ARTIST_FIXTURE_1, "title1");
		assertThat(workFound).isEqualTo(new Work(idWorkToFind, ARTIST_FIXTURE_1, "title1", "type1", "description1"));
	}
	
	@Test
	public void testSaveWork() {
		Work workToAdd = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		when(artistRepository.getArtistCollection()).thenReturn(client.getDatabase(MUSEUM_DB_NAME).getCollection(ARTIST_COLLECTION_NAME));
		Work workAdded = workRepository.saveWork(workToAdd);
		assertThat(workAdded).isEqualTo(workToAdd);
		assertThat(workAdded.getId()).isNotNull();
		assertThat(readAllWorksFromDatabase()).containsExactly(workToAdd);
	}
	
	@Test
	public void testDeleteWork() {
		Work work = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		String idWorkToDelete = addTestWorkToDatabase(work);
		workRepository.deleteWork(idWorkToDelete);
		assertThat(readAllWorksFromDatabase()).isEmpty();
	}
	
	@Test
	public void testDeleteWorksOfArtist() {
		Work work1 = new Work(ARTIST_FIXTURE_1, "title1", "type1", "description1");
		Work work2 = new Work(ARTIST_FIXTURE_1, "title2", "type2", "description2");
		Work work3 = new Work(ARTIST_FIXTURE_2, "title3", "type3", "description3");
		addTestWorkToDatabase(work1);
		addTestWorkToDatabase(work2);
		addTestWorkToDatabase(work3);
		workRepository.deleteWorksOfArtist(ARTIST_FIXTURE_1.getId());
		List<Work> works = readAllWorksFromDatabase();
		assertThat(works).containsOnly(new Work(ARTIST_FIXTURE_2, "title3", "type3", "description3"));
	}
	
	@Test
	public void testCreateWorkCollectionWhenCollectionDoesNotExistInDatabase() {
		workRepository = new WorkMongoRepository(client, client.startSession(), MUSEUM_DB_NAME, "new_collection", artistRepository);
		assertThat(client.getDatabase(MUSEUM_DB_NAME).listCollectionNames()).contains("new_collection");
	}
	
	private String addTestWorkToDatabase(Work work) {
		Document workToAdd = new Document()
				.append("artist", new DBRef(ARTIST_COLLECTION_NAME, new ObjectId(work.getArtist().getId())))
				.append("title", work.getTitle())
				.append("type", work.getType())
				.append("description", work.getDescription());
		workCollection.insertOne(workToAdd);
		return workToAdd.get("_id").toString();
	}
	
	private List<Work> readAllWorksFromDatabase() {
		return StreamSupport
				.stream(workCollection.find().spliterator(), false)
				.map(d -> new Work(d.get("_id").toString(),
						artistRepository.findArtistById(((DBRef) d.get("artist")).getId().toString()),
						d.get("title").toString(),
						d.get("type").toString(),
						d.get("description").toString()))
				.collect(Collectors.toList());
	}

}
