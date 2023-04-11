package com.cosimogiani.museum.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.AdditionalAnswers.answer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.cosimogiani.museum.exception.ArtistException;
import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.repository.ArtistRepository;
import com.cosimogiani.museum.repository.WorkRepository;
import com.cosimogiani.museum.transaction.TransactionFunction;
import com.cosimogiani.museum.transaction.TransactionManager;

public class ArtistServiceTransactionalTest {
	
	private AutoCloseable closeable;
	
	@Mock
	private TransactionManager transactionManager;
	
	@Mock
	private ArtistRepository artistRepository;
	
	@Mock
	private WorkRepository workRepository;
	
	@InjectMocks
	private ArtistServiceTransactional artistService;
	
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
	public void testFindAllArtists() {
		List<Artist> artists = Arrays.asList(new Artist());
		when(artistRepository.findAllArtists()).thenReturn(artists);
		List<Artist> artistsFound = artistService.findAllArtists();
		assertThat(artistsFound).isEqualTo(artists);
	}
	
	@Test
	public void testAddArtist() {
		Artist artistToAdd = new Artist("test");
		when(artistRepository.saveArtist(artistToAdd)).thenReturn(artistToAdd);
		Artist artistAdded = artistService.addArtist(artistToAdd);
		assertThat(artistAdded).isEqualTo(new Artist("test"));
		verify(artistRepository).saveArtist(artistToAdd);
	}
	
	@Test
	public void testDeleteArtistWhenArtistExistsInDatabase() {
		String idArtistToRemove = "1";
		when(artistRepository.findArtistById(idArtistToRemove)).thenReturn(new Artist(idArtistToRemove, "test"));
		artistService.deleteArtist(idArtistToRemove);
		InOrder inOrder = Mockito.inOrder(artistRepository, workRepository);
		inOrder.verify(workRepository).deleteWorksOfArtist(idArtistToRemove);
		inOrder.verify(artistRepository).deleteArtist(idArtistToRemove);
	}
	
	@Test
	public void testDeleteArtistWhenArtistDoesNotExistInDatabase() {
		String idArtistToRemove = "1";
		when(artistRepository.findArtistById(idArtistToRemove)).thenReturn(null);
		try {
			artistService.deleteArtist(idArtistToRemove);
			fail("Expected an ArtistException to be thrown");
		}
		catch (ArtistException ae) {
			assertThat("Artist with id " + idArtistToRemove + " not found").isEqualTo(ae.getMessage());
		}
	}

}
