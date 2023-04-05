package com.cosimogiani.museum.repository;

import java.util.List;

import com.cosimogiani.museum.model.Artist;

public interface ArtistRepository {
	
	public List<Artist> findAllArtists();
	
	public Artist saveArtist(Artist artist);
	
	public Artist findArtistById(String id);
	
	public Artist deleteArtist(String id);

}
