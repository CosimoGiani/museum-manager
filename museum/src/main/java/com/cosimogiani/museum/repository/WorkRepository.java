package com.cosimogiani.museum.repository;

import java.util.List;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;

public interface WorkRepository {
	
	public List<Work> findAllWorks();
	
	public List<Work> findWorksByArtist(Artist artist);
	
	public Work findWorkById(String id);
	
	public Work findWorkByArtistAndTitle(Artist artist, String title);
	
	public Work saveWork(Work work);
	
	public void deleteWork(String id);
	
	public void deleteWorksOfArtist(String artistId);

}
