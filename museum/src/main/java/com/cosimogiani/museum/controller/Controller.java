package com.cosimogiani.museum.controller;

import com.cosimogiani.museum.exception.ArtistException;
import com.cosimogiani.museum.exception.WorkException;
import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.service.ArtistService;
import com.cosimogiani.museum.service.WorkService;
import com.cosimogiani.museum.view.View;

public class Controller {
	
	private View view;
	private ArtistService artistService;
	private WorkService workService;
	
	public Controller(View view, ArtistService artistService, WorkService workService) {
		this.view = view;
		this.artistService = artistService;
		this.workService = workService;
	}
	
	public void allArtists() {
		view.showArtists(artistService.findAllArtists());
	}
	
	public void allWorksByArtist(Artist artist) {
		try {
			view.showWorks(workService.findWorksByArtist(artist));
		}
		catch (ArtistException ae) {
			artistDoesNotExistInDatabase(artist);
		}
	}
	
	public void allWorks() {
		view.showWorks(workService.findAllWorks());
	}
	
	public void newArtist(Artist artist) {
		artistService.addArtist(artist);
		view.artistAdded(artist);
	}
	
	public void deleteArtist(Artist artist) {
		try {
			artistService.deleteArtist(artist.getId());
		}
		catch (ArtistException ae) {
			view.showArtistError("Artist no longer in the database", artist);
		}
		view.artistRemoved(artist);
		view.removeWorksOfArtist(artist);
	}
	
	public void newWork(Work work) {
		try {
			workService.addWork(work);
			view.workAdded(work);
		}
		catch (ArtistException ae) {
			artistDoesNotExistInDatabase(work.getArtist());
		}
		catch (WorkException we) {
			view.showWorkError("This artwork already exists", work);
		}
	}
	
	public void deleteWork(Work work) {
		try {
			workService.deleteWork(work);
			view.workRemoved(work);
		}
		catch (ArtistException ae) {
			artistDoesNotExistInDatabase(work.getArtist());
		}
		catch (WorkException we) {
			view.showWorkError("Artwork no longer in the database", work);
			view.workRemoved(work);
		}
	}
	
	public void viewInitialization() {
		allArtists();
		allWorks();
	}
	
	private void artistDoesNotExistInDatabase(Artist artist) {
		view.showArtistError("Artist no longer in the database", artist);
		view.artistRemoved(artist);
		// If an artist is no longer in the database, then artist's works should be removed from the view
		// because the museum doesn't exhibit that artist's works anymore
		view.removeWorksOfArtist(artist);
	}

}
