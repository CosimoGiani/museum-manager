package com.cosimogiani.museum.view.swing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Pattern;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.cosimogiani.museum.controller.Controller;
import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;
import com.cosimogiani.museum.repository.mongo.ArtistMongoRepository;
import com.cosimogiani.museum.repository.mongo.WorkMongoRepository;
import com.cosimogiani.museum.service.ArtistService;
import com.cosimogiani.museum.service.ArtistServiceTransactional;
import com.cosimogiani.museum.service.WorkService;
import com.cosimogiani.museum.service.WorkServiceTransactional;
import com.cosimogiani.museum.transaction.TransactionManager;
import com.cosimogiani.museum.transaction.mongo.TransactionManagerMongo;
import com.mongodb.MongoClient;
import com.mongodb.client.ClientSession;

@RunWith(GUITestRunner.class)
public class SwingViewIT extends AssertJSwingJUnitTestCase {
	
	private static final String MUSEUM_DB_NAME = "museum";
	private static final String ARTIST_COLLECTION_NAME = "artist";
	private static final String WORK_COLLECTION_NAME = "work";
	
	private MongoClient client;
	private ClientSession session;
	private TransactionManager transactionManager;
	private ArtistMongoRepository artistRepository;
	private WorkMongoRepository workRepository;
	private Controller controller;
	private SwingView view;
	private FrameFixture window;
	
	@Override
	protected void onSetUp() {
		client = new MongoClient("localhost");
		session = client.startSession();
		artistRepository = new ArtistMongoRepository(client, session, MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME);
		workRepository = new WorkMongoRepository(client, session, MUSEUM_DB_NAME, WORK_COLLECTION_NAME, artistRepository);
		for (Artist artist : artistRepository.findAllArtists()) {
			artistRepository.deleteArtist(artist.getId());
		}
		for (Work work : workRepository.findAllWorks()) {
			workRepository.deleteWork(work.getId());
		}
		GuiActionRunner.execute(() -> {
			view = new SwingView();
			transactionManager = new TransactionManagerMongo(client, session, MUSEUM_DB_NAME, ARTIST_COLLECTION_NAME, WORK_COLLECTION_NAME);
			ArtistService artistService = new ArtistServiceTransactional(transactionManager);
			WorkService workService = new WorkServiceTransactional(transactionManager);
			controller = new Controller(view, artistService, workService);
			view.setController(controller);
			return view;
		});
		window = new FrameFixture(robot(), view);
		window.show();
	}
	
	@Override
	protected void onTearDown() {
		client.close();
	}
	
	@Test
	public void testAllArtists() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		GuiActionRunner.execute(() -> controller.allArtists());
		String[] listArtist = window.list("listArtist").contents();
		assertThat(listArtist).containsExactly(artist1.toString(), artist2.toString());
		String[] artistsComboBoxSearch = window.comboBox("comboBoxSearch").contents();
		assertThat(artistsComboBoxSearch).containsExactly(artist1.toString(), artist2.toString());
	}
	
	@Test
	public void testAllWorks() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		GuiActionRunner.execute(() -> controller.allWorks());
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsExactly(work1.toString(), work2.toString());
	}
	
	@Test
	public void testAddArtistButton() {
		window.textBox("textArtistName").enterText("test");
		window.button(JButtonMatcher.withText("ADD ARTIST")).click();
		assertThat(window.list("listArtist").contents()).containsOnly(new Artist("test").toString());
		assertThat(window.comboBox("comboBoxSearch").contents()).containsExactly(new Artist("test").toString());
	}
	
	@Test
	public void testDeleteArtistButtonSuccess() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist1, "title2", "type2", "description2");
		Work work3 = new Work(artist2, "title3", "type3", "description3");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		workRepository.saveWork(work3);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listArtist").selectItem(Pattern.compile(artist1.getName()));
		window.button(JButtonMatcher.withText("DELETE ARTIST")).click();
		assertThat(window.list("listArtist").contents()).noneMatch(e -> e.contains(artist1.getName()));
		assertThat(window.comboBox("comboBoxSearch").contents()).noneMatch(e -> e.contains(artist1.getName()));
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsOnly(work3.toString());
	}
	
	@Test
	public void testDeleteArtistButtonError() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist2, "title1", "type1", "description1");
		workRepository.saveWork(work1);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listArtist").selectItem(Pattern.compile(artist1.getName()));
		artistRepository.deleteArtist(artist1.getId());
		window.button(JButtonMatcher.withText("DELETE ARTIST")).click();
		assertThat(window.list("listArtist").contents()).noneMatch(e -> e.contains(artist1.getName()));
		assertThat(window.comboBox("comboBoxSearch").contents()).noneMatch(e -> e.contains(artist1.getName()));
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsOnly(work1.toString());
		window.label("lblArtistError").requireText("Artist no longer in the database: " + artist1.getName());
	}
	
	@Test
	public void testAddWorkButtonSuccess() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listArtist").selectItem(Pattern.compile(artist1.getName()));
		window.textBox("textWorkTitle").enterText("title");
		window.comboBox("comboBoxWorkType").selectItem(Pattern.compile("Painting"));
		window.textBox("textDescription").enterText("description");
		window.button(JButtonMatcher.withText("ADD WORK")).click();
		Work newWork = new Work(artist1, "title", "Painting", "description");
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsExactly(work1.toString(), work2.toString(), newWork.toString());
	}
	
	@Test
	public void testAddWorkButtonErrorWhenArtistDoesNotExist() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist1, "title2", "type2", "description2");
		Work work3 = new Work(artist2, "title3", "type3", "description3");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		workRepository.saveWork(work3);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listArtist").selectItem(Pattern.compile(artist1.getName()));
		window.textBox("textWorkTitle").enterText("title");
		window.comboBox("comboBoxWorkType").selectItem(Pattern.compile("Painting"));
		window.textBox("textDescription").enterText("description");
		artistRepository.deleteArtist(artist1.getId());
		window.button(JButtonMatcher.withText("ADD WORK")).click();
		window.label("lblArtistError").requireText("Artist no longer in the database: " + artist1.getName());
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsOnly(work3.toString());
		assertThat(window.list("listArtist").contents()).noneMatch(e -> e.contains(artist1.toString()));
		assertThat(window.comboBox("comboBoxSearch").contents()).noneMatch(e -> e.contains(artist1.toString()));
	}
	
	@Test
	public void testAddWorkButtonErrorWhenWorkAlreadyExists() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "Painting", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listArtist").selectItem(Pattern.compile(artist1.getName()));
		window.textBox("textWorkTitle").enterText("title1");
		window.comboBox("comboBoxWorkType").selectItem(Pattern.compile("Painting"));
		window.textBox("textDescription").enterText("description1");
		window.button(JButtonMatcher.withText("ADD WORK")).click();
		Work newWork = new Work(artist1, "title1", "Painting", "description1");
		window.label("lblWorkError").requireText("This artwork already exists: " + newWork.getTitle());
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsExactly(work1.toString(), work2.toString());
	}
	
	@Test
	public void testDeleteWorkButtonSuccess() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listWorks").selectItem(Pattern.compile(work1.toString()));
		window.button(JButtonMatcher.withText("DELETE WORK")).click();
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).doesNotContain(work1.toString());
	}
	
	@Test
	public void testDeleteWorkButtonErrorWhenArtistDoesNotExist() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listWorks").selectItem(Pattern.compile(work1.toString()));
		artistRepository.deleteArtist(artist1.getId());
		window.button(JButtonMatcher.withText("DELETE WORK")).click();
		window.label("lblArtistError").requireText("Artist no longer in the database: " + artist1.getName());
		assertThat(window.list("listArtist").contents()).noneMatch(e -> e.contains(artist1.toString()));
		assertThat(window.comboBox("comboBoxSearch").contents()).noneMatch(e -> e.contains(artist1.toString()));
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).doesNotContain(work1.toString());
	}
	
	@Test
	public void testDeleteWorkButtonErrorWhenWorkDoesNotExist() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.list("listWorks").selectItem(Pattern.compile(work1.toString()));
		workRepository.deleteWork(work1.getId());
		window.button(JButtonMatcher.withText("DELETE WORK")).click();
		window.label("lblWorkError").requireText("Artwork no longer in the database: " + work1.getTitle());
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).doesNotContain(work1.toString());
	}
	
	@Test
	public void testSearchButtonSuccess() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		Work work3 = new Work(artist2, "title3", "type3", "description3");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		workRepository.saveWork(work3);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.comboBox("comboBoxSearch").selectItem(Pattern.compile(artist2.toString()));
		window.button(JButtonMatcher.withText("SEARCH")).click();
		String[] listSearch = window.list("listSearch").contents();
		assertThat(listSearch).containsExactly(work2.toString(), work3.toString());
	}
	
	@Test
	public void testSearchButtonError() {
		Artist artist1 = new Artist("test1");
		Artist artist2 = new Artist("test2");
		artistRepository.saveArtist(artist1);
		artistRepository.saveArtist(artist2);
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		Work work3 = new Work(artist2, "title3", "type3", "description3");
		workRepository.saveWork(work1);
		workRepository.saveWork(work2);
		workRepository.saveWork(work3);
		GuiActionRunner.execute(() -> controller.viewInitialization());
		window.comboBox("comboBoxSearch").selectItem(Pattern.compile(artist2.toString()));
		artistRepository.deleteArtist(artist2.getId());
		window.button(JButtonMatcher.withText("SEARCH")).click();
		window.label("lblSearchError").requireText("Artist no longer in the database: " + artist2.getName());
		String[] listSearch = window.list("listSearch").contents();
		assertThat(listSearch).isEmpty();
	}

}
