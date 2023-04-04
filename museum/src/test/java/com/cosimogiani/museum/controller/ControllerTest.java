package com.cosimogiani.museum.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cosimogiani.museum.exception.ArtistException;
import com.cosimogiani.museum.exception.WorkException;
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
	
	private static final Artist ARTIST_FIXTURE = new Artist("1", "Botticelli");
	private static final Work WORK_FIXTURE = new Work("1", ARTIST_FIXTURE, "Primavera", "painting", "description");
	
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
	public void testWorksByArtist() {
		List<Work> works = Arrays.asList(new Work());
		when(workService.findWorksByArtist(ARTIST_FIXTURE)).thenReturn(works);
		controller.allWorksByArtist(ARTIST_FIXTURE);
		verify(view).showWorks(works);
	}
	
	@Test
	public void testAllWorks() {
		List<Work> works = Arrays.asList(new Work());
		when(workService.findAllWorks()).thenReturn(works);
		controller.allWorks();
		verify(view).showWorks(works);
	}
	
	@Test
	public void testWorksByArtistWhenArtistDoesNotExistInDatabase() {
		when(workService.findWorksByArtist(ARTIST_FIXTURE)).thenThrow(new ArtistException("Artist not found"));
		controller.allWorksByArtist(ARTIST_FIXTURE);
		verify(view).showArtistError("Artist no longer in the database", ARTIST_FIXTURE);
		verify(view).artistRemoved(ARTIST_FIXTURE);
		verify(view).removeWorksOfArtist(ARTIST_FIXTURE);
	}
	
	@Test
	public void testAddNewArtist() {
		controller.newArtist(ARTIST_FIXTURE);
		InOrder inOrder = Mockito.inOrder(artistService, view);
		inOrder.verify(artistService).addArtist(ARTIST_FIXTURE);
		inOrder.verify(view).artistAdded(ARTIST_FIXTURE);
	}
	
	@Test
	public void testDeleteArtistWhenArtistExistsInDatabase() {
		controller.deleteArtist(ARTIST_FIXTURE);
		InOrder inOrder = Mockito.inOrder(artistService, view);
		inOrder.verify(artistService).deleteArtist(ARTIST_FIXTURE.getId());
		inOrder.verify(view).artistRemoved(ARTIST_FIXTURE);
		inOrder.verify(view).removeWorksOfArtist(ARTIST_FIXTURE);
	}
	
	@Test
	public void testDeleteArtistWhenArtistDoesNotExistInDatabase() {
		doThrow(new ArtistException("Artist not found")).when(artistService).deleteArtist(ARTIST_FIXTURE.getId());
		controller.deleteArtist(ARTIST_FIXTURE);
		verify(view).showArtistError("Artist no longer in the database", ARTIST_FIXTURE);
		InOrder inOrder = Mockito.inOrder(artistService, view);
		inOrder.verify(artistService).deleteArtist(ARTIST_FIXTURE.getId());
		inOrder.verify(view).showArtistError("Artist no longer in the database", ARTIST_FIXTURE);
		inOrder.verify(view).artistRemoved(ARTIST_FIXTURE);
		inOrder.verify(view).removeWorksOfArtist(ARTIST_FIXTURE);
	}
	
	@Test
	public void testAddNewWorkWhenArtistExistsInDatabase() {
		controller.newWork(WORK_FIXTURE);
		InOrder inOrder = Mockito.inOrder(workService, view);
		inOrder.verify(workService).addWork(WORK_FIXTURE);
		inOrder.verify(view).workAdded(WORK_FIXTURE);
	}
	
	@Test
	public void testAddNewWorkWhenArtistDoesNotExistInDatabase() {
		doThrow(new ArtistException("Artist not found")).when(workService).addWork(WORK_FIXTURE);
		controller.newWork(WORK_FIXTURE);
		InOrder inOrder = Mockito.inOrder(workService, view);
		inOrder.verify(workService).addWork(WORK_FIXTURE);
		inOrder.verify(view).showArtistError("Artist no longer in the database", ARTIST_FIXTURE);
		inOrder.verify(view).artistRemoved(ARTIST_FIXTURE);
		inOrder.verify(view).removeWorksOfArtist(ARTIST_FIXTURE);
	}
	
	@Test
	public void testAddNewWorkWhenWorkAlreadyExistsInDatabase() {
		doThrow(new WorkException("Artwork already exists")).when(workService).addWork(WORK_FIXTURE);
		controller.newWork(WORK_FIXTURE);
		InOrder inOrder = Mockito.inOrder(workService, view);
		inOrder.verify(workService).addWork(WORK_FIXTURE);
		inOrder.verify(view).showWorkError("This artwork already exists", WORK_FIXTURE);
	}
	
	@Test
	public void testDeleteWorkWhenArtistAndWorkExistInDatabase() {
		controller.deleteWork(WORK_FIXTURE);
		InOrder inOrder = Mockito.inOrder(workService, view);
		inOrder.verify(workService).deleteWork(WORK_FIXTURE);
		inOrder.verify(view).workRemoved(WORK_FIXTURE);
	}
	
	@Test
	public void testDeleteWorkWhenArtistDoesNotExistInDatabase() {
		doThrow(new ArtistException("Artist not found")).when(workService).deleteWork(WORK_FIXTURE);
		controller.deleteWork(WORK_FIXTURE);
		InOrder inOrder = Mockito.inOrder(workService, view);
		inOrder.verify(workService).deleteWork(WORK_FIXTURE);
		inOrder.verify(view).showArtistError("Artist no longer in the database", ARTIST_FIXTURE);
		inOrder.verify(view).artistRemoved(ARTIST_FIXTURE);
		inOrder.verify(view).removeWorksOfArtist(ARTIST_FIXTURE);
	}
	
	@Test
	public void testDeleteWorkWhenWorkDoesNotExistInDatabase() {
		doThrow(new WorkException("Artwork not found")).when(workService).deleteWork(WORK_FIXTURE);
		controller.deleteWork(WORK_FIXTURE);
		InOrder inOrder = Mockito.inOrder(workService, view);
		inOrder.verify(workService).deleteWork(WORK_FIXTURE);
		inOrder.verify(view).showWorkError("Artwork no longer in the database", WORK_FIXTURE);
		inOrder.verify(view).workRemoved(WORK_FIXTURE);
	}
	
	@Test
	public void testViewInitialization() {
		List<Artist> artists = Arrays.asList(new Artist());
		when(artistService.findAllArtists()).thenReturn(artists);
		List<Work> works = Arrays.asList(new Work());
		when(workService.findAllWorks()).thenReturn(works);
		controller.viewInitialization();
		verify(view).showArtists(artists);
		verify(view).showWorks(works);
	}

}
