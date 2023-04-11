package com.cosimogiani.museum.service;

import java.util.List;

import com.cosimogiani.museum.exception.ArtistException;
import com.cosimogiani.museum.exception.WorkException;
import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.transaction.TransactionManager;

public class WorkServiceTransactional implements WorkService {
	
	private TransactionManager transactionManager;
	
	public WorkServiceTransactional(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public List<Work> findAllWorks() {
		return transactionManager.doInTransaction(
				(artistRepository, workRepository) -> workRepository.findAllWorks() );
	}
	
	@Override
	public List<Work> findWorksByArtist(Artist artist) {
		return transactionManager.doInTransaction(
				(artistRepository, workRepository) -> {
					if (artistRepository.findArtistById(artist.getId()) == null) {
						throw new ArtistException("Artist with id: " + artist.getId() + " not found");
					}
					return workRepository.findWorksByArtist(artist);
				});
	}
	
	@Override
	public Work addWork(Work work) {
		return transactionManager.doInTransaction(
				(artistRepository, workRepository) -> {
					if (artistRepository.findArtistById(work.getArtist().getId()) == null) {
						throw new ArtistException("Artist with id: " + work.getArtist().getId() + " not found");
					}
					if (workRepository.findWorkByArtistAndTitle(work.getArtist(), work.getTitle()) != null) {
						throw new WorkException("Artwork " + work.getTitle() + " of " + work.getArtist() + " is already in the database");
					}
					return workRepository.saveWork(work);
				});
	}
	
	@Override
	public void deleteWork(Work work) {
		transactionManager.doInTransaction(
				(artistRepository, workRepository) -> {
					if (artistRepository.findArtistById(work.getArtist().getId()) == null) {
						throw new ArtistException("Artist with id: " + work.getArtist().getId() + " not found");
					}
					if (workRepository.findWorkById(work.getId()) == null) {
						throw new WorkException("Artwork with id " + work.getId() + " not found");
					}
					workRepository.deleteWork(work.getId());
					return null;
				});
	}

}
