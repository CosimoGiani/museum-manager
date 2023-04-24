package com.cosimogiani.museum.repository.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.repository.WorkRepository;
import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class WorkMongoRepository implements WorkRepository {
	
	private MongoCollection<Document> workCollection;
	private ArtistMongoRepository artistRepository;
	private ClientSession session;
	
	private static final String UNIQUE_ID = "_id";
	private static final String FIELD_ARTIST = "artist";
	private static final String FIELD_TITLE = "title";
	private static final String FIELD_TYPE = "type";
	private static final String FIELD_DESCRIPTION = "description";
	
	public WorkMongoRepository(MongoClient client, ClientSession session, String dbName, String workCollectionName, 
			ArtistMongoRepository artistRepository) {
		MongoDatabase db = client.getDatabase(dbName);
		if (!db.listCollectionNames().into(new ArrayList<String>()).contains(workCollectionName)) {
			db.createCollection(workCollectionName);
		}
		workCollection = db.getCollection(workCollectionName);
		this.artistRepository = artistRepository;
		this.session = session;
	}
	
	private Work fromDocumentToWork(Document d) {
		return new Work((d.get(UNIQUE_ID).toString()),
				artistRepository.findArtistById(((DBRef) d.get(FIELD_ARTIST)).getId().toString()),
				d.get(FIELD_TITLE).toString(),
				d.get(FIELD_TYPE).toString(),
				d.get(FIELD_DESCRIPTION).toString());
	}
	
	@Override
	public List<Work> findAllWorks() {
		return StreamSupport
				.stream(workCollection.find(session).spliterator(), false)
				.map(this::fromDocumentToWork)
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Work> findWorksByArtist(Artist artist) {
		return StreamSupport
				.stream(workCollection.find(session, Filters.eq(FIELD_ARTIST+".$id", new ObjectId(artist.getId()))).spliterator(), false)
				.map(this::fromDocumentToWork)
				.collect(Collectors.toList());
	}
	
	@Override
	public Work findWorkById(String id) {
		Document d = workCollection.find(session, Filters.eq(UNIQUE_ID, new ObjectId(id))).first();
		if (d != null) {
			return fromDocumentToWork(d);
		}
		return null;
	}
	
	@Override
	public Work findWorkByArtistAndTitle(Artist artist, String title) {
		Document d = workCollection.find(session, Filters.and(
				Filters.eq(FIELD_ARTIST+".$id", new ObjectId(artist.getId())), Filters.eq(FIELD_TITLE, title))).first();
		if (d != null) {
			return fromDocumentToWork(d);
		}
		return null;
	}
	
	@Override
	public Work saveWork(Work work) {
		Document newWork = new Document()
				.append(FIELD_ARTIST, new DBRef(artistRepository.getArtistCollection().getNamespace().getCollectionName(), 
								  new ObjectId(work.getArtist().getId())))
				.append(FIELD_TITLE, work.getTitle())
				.append(FIELD_TYPE, work.getType())
				.append(FIELD_DESCRIPTION, work.getDescription());
		workCollection.insertOne(session, newWork);
		work.setId(newWork.get(UNIQUE_ID).toString());
		return work;
	} 
	
	@Override
	public void deleteWork(String id) {
		workCollection.deleteOne(session, Filters.eq(UNIQUE_ID, new ObjectId(id)));
	}
	
	@Override
	public void deleteWorksOfArtist(String artistId) {
		workCollection.deleteMany(session, Filters.eq(FIELD_ARTIST+".$id", new ObjectId(artistId)));
	}

}
