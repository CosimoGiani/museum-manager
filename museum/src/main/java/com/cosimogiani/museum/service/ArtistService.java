package com.cosimogiani.museum.service;

import java.util.List;

import com.cosimogiani.museum.model.Artist;

public interface ArtistService {
	
	public List<Artist> findAllArtists();
	
	public Artist addArtist(Artist artist);
	
	public void deleteArtist(String id);

}
