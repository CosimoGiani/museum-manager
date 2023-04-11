package com.cosimogiani.museum.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.answer;
import static org.mockito.ArgumentMatchers.any;
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

import com.cosimogiani.museum.exception.ArtistException;
import com.cosimogiani.museum.exception.WorkException;
import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.repository.ArtistRepository;
import com.cosimogiani.museum.repository.WorkRepository;
import com.cosimogiani.museum.transaction.TransactionFunction;
import com.cosimogiani.museum.transaction.TransactionManager;

public class WorkServiceTransactionalTest {
	
	private AutoCloseable closeable;
	
	@Mock
	private TransactionManager transactionManager;
	
	@Mock
	private ArtistRepository artistRepository;
	
	@Mock
	private WorkRepository workRepository;
	
	@InjectMocks
	private WorkServiceTransactional workService;
	
	private static final Artist ARTIST_FIXTURE = new Artist("1", "test");
	
	@Before
	public void setup() {
		closeable = MockitoAnnotations.openMocks(this);
		when(transactionManager.doInTransaction(any()))
			.thenAnswer(answer((TransactionFunction<?> code) -> code.apply(artistRepository, workRepository)));
	}
	
	@After
	public void releaseMocks() throws Exception {
		closeable.close();
	}
	
	@Test
	public void testFindAllWorks() {
		List<Work> works = Arrays.asList(new Work());
		when(workRepository.findAllWorks()).thenReturn(works);
		List<Work> worksFound = workService.findAllWorks();
		assertThat(worksFound).isEqualTo(works);
	}
	
	@Test
	public void testFindWorksByArtistWhenArtistExistsInDatabase() {
		List<Work> works = Arrays.asList(new Work());
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(ARTIST_FIXTURE);
		when(workRepository.findWorksByArtist(ARTIST_FIXTURE)).thenReturn(works);
		List<Work> worksFound = workService.findWorksByArtist(ARTIST_FIXTURE);
		assertThat(worksFound).isEqualTo(works);
	}
	
	@Test
	public void testFindWorksByArtistWhenArtistDoesNotExistInDatabase() {
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(null);
		try {
			workService.findWorksByArtist(ARTIST_FIXTURE);
			fail("Expected an ArtistException to be thrown");
		}
		catch (ArtistException ae) {
			assertThat("Artist with id: " + ARTIST_FIXTURE.getId() + " not found").isEqualTo(ae.getMessage());
		}
	}
	
	@Test
	public void addWorkWhenArtistExistsInDatabase() {
		Work workToAdd = new Work(ARTIST_FIXTURE, "title", "type", "description");
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(ARTIST_FIXTURE);
		when(workRepository.saveWork(workToAdd)).thenReturn(workToAdd);
		Work workAdded = workService.addWork(workToAdd);
		assertThat(workAdded).isEqualTo(workToAdd);
		verify(workRepository).saveWork(workToAdd);
	}
	
	@Test
	public void addWorkWhenArtistDoesNotExistInDatabase() {
		Work workToAdd = new Work(ARTIST_FIXTURE, "title", "type", "description");
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(null);
		try {
			workService.addWork(workToAdd);
			fail("Expected an ArtistException to be thrown");
		}
		catch (ArtistException ae) {
			assertThat("Artist with id: " + ARTIST_FIXTURE.getId() + " not found").isEqualTo(ae.getMessage());
		}
	}
	
	@Test
	public void addWorkWhenSameWorkAlreadyExistsInDatabase() {
		Work workAlreadyExisting = new Work(ARTIST_FIXTURE, "title", "type", "description");
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(ARTIST_FIXTURE);
		when(workRepository.findWorkByArtistAndTitle(ARTIST_FIXTURE, "title")).thenReturn(workAlreadyExisting);
		try {
			workService.addWork(workAlreadyExisting);
			fail("Expected a WorkException to be thrown");
		}
		catch (WorkException we) {
			assertThat("Artwork " + workAlreadyExisting.getTitle() + " of " + workAlreadyExisting.getArtist() +
					" is already in the database").isEqualTo(we.getMessage());
		}
	}
	
	@Test
	public void testDeleteWorkWhenArtistAndWorkExistInDatabase() {
		Work workToRemove = new Work("1", ARTIST_FIXTURE, "title", "type", "description");
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(ARTIST_FIXTURE);
		when(workRepository.findWorkById(workToRemove.getId())).thenReturn(workToRemove);
		workService.deleteWork(workToRemove);
		verify(workRepository).deleteWork(workToRemove.getId());
	}
	
	@Test
	public void testDeleteWorkWhenWorkDoesNotExistInDatabase() {
		Work workToRemove = new Work("1", ARTIST_FIXTURE, "title", "type", "description");
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(ARTIST_FIXTURE);
		when(workRepository.findWorkById(workToRemove.getId())).thenReturn(null);
		try {
			workService.deleteWork(workToRemove);
			fail("Expected a WorkException to be thrown");
		}
		catch (WorkException we) {
			assertThat("Artwork with id " + workToRemove.getId() + " not found").isEqualTo(we.getMessage());
		}
	}
	
	@Test
	public void testDeleteWorkWhenArtistDoesNotExistInDatabase() {
		Work workToRemove = new Work("1", ARTIST_FIXTURE, "title", "type", "description");
		when(artistRepository.findArtistById(ARTIST_FIXTURE.getId())).thenReturn(null);
		try {
			workService.deleteWork(workToRemove);
			fail("Expected an ArtistException to be thrown");
		}
		catch (ArtistException ae) {
			assertThat("Artist with id: " + ARTIST_FIXTURE.getId() + " not found").isEqualTo(ae.getMessage());
		}
	}

}
