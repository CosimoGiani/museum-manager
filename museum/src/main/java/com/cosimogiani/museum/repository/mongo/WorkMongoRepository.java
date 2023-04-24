package com.cosimogiani.museum.repository.mongo;

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
	
	public WorkMongoRepository(MongoClient client, ClientSession session, String dbName, String workCollectionName, 
			ArtistMongoRepository artistRepository) {
		MongoDatabase db = client.getDatabase(dbName);
		workCollection = db.getCollection(workCollectionName);
		this.artistRepository = artistRepository;
		this.session = session;
	}
	
	private Work fromDocumentToWork(Document d) {
		return new Work((d.get("_id").toString()),
				artistRepository.findArtistById(((DBRef) d.get("artist")).getId().toString()),
				d.get("title").toString(),
				d.get("type").toString(),
				d.get("description").toString());
	}
	
	@Override
	public List<Work> findAllWorks() {
		return StreamSupport
				.stream(workCollection.find(session).spliterator(), false)
				.map(d -> fromDocumentToWork(d))
				.collect(Collectors.toList());
	}
	
	@Override
	public List<Work> findWorksByArtist(Artist artist) {
		return StreamSupport
				.stream(workCollection.find(session, Filters.eq("artist.$id", new ObjectId(artist.getId()))).spliterator(), false)
				.map(d -> fromDocumentToWork(d))
				.collect(Collectors.toList());
	}
	
	@Override
	public Work findWorkById(String id) {
		Document d = workCollection.find(session, Filters.eq("_id", new ObjectId(id))).first();
		if (d != null) {
			return fromDocumentToWork(d);
		}
		return null;
	}
	
	@Override
	public Work findWorkByArtistAndTitle(Artist artist, String title) {
		Document d = workCollection.find(session, Filters.and(
				Filters.eq("artist.$id", new ObjectId(artist.getId())), Filters.eq("title", title))).first();
		if (d != null) {
			return fromDocumentToWork(d);
		}
		return null;
	}
	
	@Override
	public Work saveWork(Work work) {
		Document newWork = new Document()
				.append("artist", new DBRef(artistRepository.getArtistCollection().getNamespace().getCollectionName(), 
								  new ObjectId(work.getArtist().getId())))
				.append("title", work.getTitle())
				.append("type", work.getType())
				.append("description", work.getDescription());
		workCollection.insertOne(session, newWork);
		work.setId(newWork.get("_id").toString());
		return work;
	} 
	
	@Override
	public void deleteWork(String id) {
		workCollection.deleteOne(session, Filters.eq("_id", new ObjectId(id)));
	}
	
	@Override
	public void deleteWorksOfArtist(String artistId) {
		workCollection.deleteMany(session, Filters.eq("artist.$id", new ObjectId(artistId)));
	}

}
