package com.cosimogiani.museum.view;

import java.util.List;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;

public interface View {
	
	public void showArtists(List<Artist> artists);
	
	public void showWorks(List<Work> works);
	
	public void artistAdded(Artist artist);
	
	public void artistRemoved(Artist artist);
	
	public void showArtistError(String message, Artist artist);
	
	public void removeWorksOfArtist(Artist artist);
	
	public void workAdded(Work work);
	
	public void workRemoved(Work work);
	
	public void showWorkError(String message, Work work);

}
