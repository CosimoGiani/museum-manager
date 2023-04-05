package com.cosimogiani.museum.service;

import java.util.List;

import com.cosimogiani.museum.exception.ArtistException;
import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.transaction.TransactionManager;

public class ArtistServiceTransactional implements ArtistService {
	
	private TransactionManager transactionManager;
	
	public ArtistServiceTransactional(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public List<Artist> findAllArtists() {
		return transactionManager.doInTransaction(
				(artistRepository, workRepository) -> artistRepository.findAllArtists() );
	}
	
	@Override
	public Artist addArtist(Artist artist) {
		return transactionManager.doInTransaction(
				(artistRepository, workRepository) -> artistRepository.saveArtist(artist) );
	}
	
	@Override
	public void deleteArtist(String id) {
		transactionManager.doInTransaction(
				(artistRepository, workRepository) -> {
					if (artistRepository.findArtistById(id) == null) {
						throw new ArtistException("Artist with id " + id + " not found");
					}
					workRepository.deleteWorksOfArtist(id);
					return artistRepository.deleteArtist(id);
				});
	}

}
