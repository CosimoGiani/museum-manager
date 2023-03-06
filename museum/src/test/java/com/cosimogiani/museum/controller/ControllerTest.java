package com.cosimogiani.museum.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.service.ArtistService;
import com.cosimogiani.museum.service.WorkService;
import com.cosimogiani.museum.view.View;

public class ControllerTest {
	
	private AutoCloseable closeable;
	
	@Mock
	private ArtistService artistService;
	
	@Mock
	private WorkService workService;
	
	@Mock
	private View view;
	
	@InjectMocks
	private Controller controller;
	
	private static final Artist ARTIST_FIXTURE = new Artist("1", "test");
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testAllArtists() {
		List<Artist> artists = Arrays.asList(new Artist());
		when(artistService.findAllArtists()).thenReturn(artists);
		controller.allArtists();
		verify(view).showArtists(artists);
	}
	
	@Test
	public void testAllWorksByArtist() {
		List<Work> works = Arrays.asList(new Work());
		when(workService.findWorksByArtist(ARTIST_FIXTURE)).thenReturn(works);
		controller.allWorksByArtist(ARTIST_FIXTURE);
		verify(view).showWorksInSearchList(works);
	}

}
