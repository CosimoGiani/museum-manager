package com.cosimogiani.museum.service;

import java.util.List;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;

public interface WorkService {
	
	public List<Work> findAllWorks();
	
	public List<Work> findWorksByArtist(Artist artist);
	
	public void addWork(Work work);
	
	public void deleteWork(Work work);

}
