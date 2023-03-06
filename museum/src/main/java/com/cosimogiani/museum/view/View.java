package com.cosimogiani.museum.view;

import java.util.List;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;

public interface View {
	
	public void showArtists(List<Artist> artists);
	
	public void showWorksInSearchList(List<Work> works);

}
