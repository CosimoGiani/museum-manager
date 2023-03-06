package com.cosimogiani.museum.controller;

import com.cosimogiani.museum.model.Artist;
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
		view.showWorksInSearchList(workService.findWorksByArtist(artist));
	}

}
