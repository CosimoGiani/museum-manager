package com.cosimogiani.museum.repository.mongo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.repository.ArtistRepository;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

public class ArtistMongoRepository implements ArtistRepository {
	
	private MongoCollection<Document> artistCollection;
	private ClientSession session;
	
	public ArtistMongoRepository(MongoClient client, ClientSession session, String dbName, String artistCollectionName) {
		MongoDatabase db = client.getDatabase(dbName);
		if (!db.listCollectionNames().into(new ArrayList<String>()).contains(artistCollectionName)) {
			db.createCollection(artistCollectionName);
		}
		artistCollection = db.getCollection(artistCollectionName);
		this.session = session;
	}
	
	private Artist fromDocumentToArtist(Document d) {
		return new Artist(d.get("_id").toString(), d.getString("name"));
	}
	
	@Override
	public List<Artist> findAllArtists() {
		return StreamSupport
				.stream(artistCollection.find(session).spliterator(), false)
				.map(d -> fromDocumentToArtist(d))
				.collect(Collectors.toList());
	}
	
	@Override
	public Artist findArtistById(String id) {
		Document d = artistCollection.find(session, Filters.eq("_id", new ObjectId(id))).first();
		if (d != null) {
			return fromDocumentToArtist(d);
		}
		return null;
	}
	
	@Override
	public Artist saveArtist(Artist artist) {
		Document newArtist = new Document().append("name", artist.getName());
		artistCollection.insertOne(session, newArtist);
		artist.setId(newArtist.get("_id").toString());
		return artist;
	}
	
	@Override
	public void deleteArtist(String id) {
		artistCollection.deleteOne(session, Filters.eq("_id", new ObjectId(id)));
	}
	
	public MongoCollection<Document> getArtistCollection() {
		return artistCollection;
	}

}
