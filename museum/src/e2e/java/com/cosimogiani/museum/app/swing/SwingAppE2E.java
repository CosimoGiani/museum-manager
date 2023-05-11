package com.cosimogiani.museum.app.swing;

import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import java.util.regex.Pattern;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.DBRef;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

@RunWith(GUITestRunner.class)
public class SwingAppE2E extends AssertJSwingJUnitTestCase {
	
	private static final String MUSEUM_DB_NAME = "museum";
	private static final String ARTIST_COLLECTION_NAME = "artist";
	private static final String WORK_COLLECTION_NAME = "work";
	
	private static final String ARTIST_FIXTURE_1_NAME = "test1";
	private static String ARTIST_FIXTURE_1_ID;
	private static final String ARTIST_FIXTURE_2_NAME = "test2";
	private static String ARTIST_FIXTURE_2_ID;
	
	private static final String WORK_FIXTURE_1_TITLE = "title1";
	private static final String WORK_FIXTURE_1_TYPE = "Painting";
	private static final String WORK_FIXTURE_1_DESCRIPTION = "description1";
	private static String WORK_FIXTURE_1_ID;
	private static final String WORK_FIXTURE_2_TITLE = "title2";
	private static final String WORK_FIXTURE_2_TYPE = "Sculpture";
	private static final String WORK_FIXTURE_2_DESCRIPTION = "description2";
	
	private MongoClient client;
	private FrameFixture window;
	
	@Override
	protected void onSetUp() {
		client = new MongoClient(new ServerAddress("localhost", 27017));
		client.getDatabase(MUSEUM_DB_NAME).drop();
		ARTIST_FIXTURE_1_ID = addTestArtistToDatabase(ARTIST_FIXTURE_1_NAME);
		ARTIST_FIXTURE_2_ID = addTestArtistToDatabase(ARTIST_FIXTURE_2_NAME);
		WORK_FIXTURE_1_ID = addTestWorkToDatabase(ARTIST_FIXTURE_1_ID, WORK_FIXTURE_1_TITLE, 
				WORK_FIXTURE_1_TYPE, WORK_FIXTURE_1_DESCRIPTION);
		addTestWorkToDatabase(ARTIST_FIXTURE_2_ID, WORK_FIXTURE_2_TITLE, WORK_FIXTURE_2_TYPE, 
				WORK_FIXTURE_2_DESCRIPTION);
		application("com.cosimogiani.museum.app.swing.SwingApp")
			.withArgs(
					"--mongo-host=" + "localhost",
					"--mongo-port=" + 27017,
					"--db-name=" + MUSEUM_DB_NAME,
					"--collection-artists-name=" + ARTIST_COLLECTION_NAME,
					"--collection-works-name=" + WORK_COLLECTION_NAME
			).start();
		window = WindowFinder.findFrame(new GenericTypeMatcher<JFrame>(JFrame.class) {
			@Override
			protected boolean isMatching(JFrame frame) {
				return "MUSEUM VIEW".equals(frame.getTitle()) && frame.isShowing();
			}
		}).using(robot());
	}
	
	@Override
	protected void onTearDown() {
		client.close();
	}
	
	@Test
	public void testOnStartAllDatabaseElementsAreShown() {
		assertThat(window.list("listArtist").contents())
			.anySatisfy(e -> assertThat(e).contains(ARTIST_FIXTURE_1_NAME))
			.anySatisfy(e -> assertThat(e).contains(ARTIST_FIXTURE_2_NAME));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.anySatisfy(e -> assertThat(e).contains(ARTIST_FIXTURE_1_NAME))
			.anySatisfy(e -> assertThat(e).contains(ARTIST_FIXTURE_2_NAME));
		assertThat(window.list("listWorks").contents())
			.anySatisfy(e -> assertThat(e).contains(
					WORK_FIXTURE_1_TITLE + " - " + WORK_FIXTURE_1_TYPE + " - " + WORK_FIXTURE_1_DESCRIPTION))
			.anySatisfy(e -> assertThat(e).contains(
					WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION));
	}
	
	@Test
	public void testAddArtistButton() {
		window.textBox("textArtistName").enterText("test3");
		window.button(JButtonMatcher.withText("ADD ARTIST")).click();
		assertThat(window.list("listArtist").contents())
			.anySatisfy(e -> assertThat(e).contains("test3"));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.anySatisfy(e -> assertThat(e).contains("test3"));
	}
	
	@Test
	public void testDeleteArtistButtonSuccess() {
		window.list("listArtist").selectItem(Pattern.compile(ARTIST_FIXTURE_1_NAME));
		window.button(JButtonMatcher.withText("DELETE ARTIST")).click();
		assertThat(window.list("listArtist").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.list("listWorks").contents())
			.containsOnly(WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	@Test
	public void testDeleteArtistButtonError() {
		window.list("listArtist").selectItem(Pattern.compile(ARTIST_FIXTURE_1_NAME));
		removeTestArtistFromDatabase(ARTIST_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("DELETE ARTIST")).click();
		assertThat(window.label("lblArtistError").text()).contains(ARTIST_FIXTURE_1_NAME);
		assertThat(window.list("listArtist").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.list("listWorks").contents())
			.containsOnly(WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	@Test
	public void testAddWorkButtonSuccess() {
		window.list("listArtist").selectItem(Pattern.compile(ARTIST_FIXTURE_1_NAME));
		window.textBox("textWorkTitle").enterText("title3");
		window.comboBox("comboBoxWorkType").selectItem(Pattern.compile("Painting"));
		window.textBox("textDescription").enterText("description3");
		window.button(JButtonMatcher.withText("ADD WORK")).click();
		assertThat(window.list("listWorks").contents())
			.anySatisfy(e -> assertThat(e).contains("title3 - Painting - description3"));
	}
	
	@Test
	public void testAddWorkButtonErrorWhenArtistsDoesNotExist() {
		window.list("listArtist").selectItem(Pattern.compile(ARTIST_FIXTURE_1_NAME));
		window.textBox("textWorkTitle").enterText("title3");
		window.comboBox("comboBoxWorkType").selectItem(Pattern.compile("Painting"));
		window.textBox("textDescription").enterText("description3");
		removeTestArtistFromDatabase(ARTIST_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("ADD WORK")).click();
		assertThat(window.label("lblArtistError").text()).contains(ARTIST_FIXTURE_1_NAME);
		assertThat(window.list("listArtist").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.list("listWorks").contents())
			.containsOnly(WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	@Test
	public void testAddWorkButtonErrorWhenWorkAlreadyExists() {
		window.list("listArtist").selectItem(Pattern.compile(ARTIST_FIXTURE_1_NAME));
		window.textBox("textWorkTitle").enterText(WORK_FIXTURE_1_TITLE);
		window.comboBox("comboBoxWorkType").selectItem(Pattern.compile(WORK_FIXTURE_1_TYPE));
		window.textBox("textDescription").enterText(WORK_FIXTURE_1_DESCRIPTION);
		window.button(JButtonMatcher.withText("ADD WORK")).click();
		assertThat(window.label("lblWorkError").text()).contains(WORK_FIXTURE_1_TITLE);
		assertThat(window.list("listWorks").contents()).containsExactly(
				WORK_FIXTURE_1_TITLE + " - " + WORK_FIXTURE_1_TYPE + " - " + WORK_FIXTURE_1_DESCRIPTION,
				WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	@Test
	public void testDeleteWorkButtonSuccess() {
		window.list("listWorks").selectItem(Pattern.compile(".*" + WORK_FIXTURE_1_TITLE + ".*"));
		window.button(JButtonMatcher.withText("DELETE WORK")).click();
		assertThat(window.list("listWorks").contents())
			.containsOnly(WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	@Test
	public void testDeleteWorkButtonErrorWhenArtistDoesNotExist() {
		window.list("listWorks").selectItem(Pattern.compile(".*" + WORK_FIXTURE_1_TITLE + ".*"));
		removeTestArtistFromDatabase(ARTIST_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("DELETE WORK")).click();
		assertThat(window.label("lblArtistError").text()).contains(ARTIST_FIXTURE_1_NAME);
		assertThat(window.list("listArtist").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.list("listWorks").contents())
			.containsOnly(WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	@Test
	public void testDeleteWorkButtonErrorWhenWorkDoesNotExist() {
		window.list("listWorks").selectItem(Pattern.compile(".*" + WORK_FIXTURE_1_TITLE + ".*"));
		removeTestWorkFromDatabase(WORK_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("DELETE WORK")).click();
		assertThat(window.label("lblWorkError").text()).contains(WORK_FIXTURE_1_TITLE);
		assertThat(window.list("listWorks").contents())
			.containsOnly(WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	@Test
	public void testSearchButtonSuccess() {
		window.comboBox("comboBoxSearch").selectItem(Pattern.compile(ARTIST_FIXTURE_1_NAME));
		window.button(JButtonMatcher.withText("SEARCH")).click();
		assertThat(window.list("listSearch").contents())
			.anySatisfy(e -> assertThat(e).contains(
				WORK_FIXTURE_1_TITLE + " - " + WORK_FIXTURE_1_TYPE + " - " + WORK_FIXTURE_1_DESCRIPTION));
	}
	
	@Test
	public void testSearchButtonError() {
		window.comboBox("comboBoxSearch").selectItem(Pattern.compile(ARTIST_FIXTURE_1_NAME));
		removeTestArtistFromDatabase(ARTIST_FIXTURE_1_ID);
		window.button(JButtonMatcher.withText("SEARCH")).click();
		assertThat(window.label("lblSearchError").text()).contains(ARTIST_FIXTURE_1_NAME);
		assertThat(window.list("listSearch").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.list("listArtist").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.noneMatch(e -> e.contains(ARTIST_FIXTURE_1_NAME));
		assertThat(window.list("listWorks").contents())
			.containsOnly(WORK_FIXTURE_2_TITLE + " - " + WORK_FIXTURE_2_TYPE + " - " + WORK_FIXTURE_2_DESCRIPTION);
	}
	
	private String addTestArtistToDatabase(String artistName) {
		Document artist = new Document().append("name", artistName);
		client.getDatabase(MUSEUM_DB_NAME).getCollection(ARTIST_COLLECTION_NAME).insertOne(artist);
		return artist.get("_id").toString();
	}
	
	private String addTestWorkToDatabase(String artistId, String title, String type, String description) {
		Document work = new Document()
				.append("artist", new DBRef(ARTIST_COLLECTION_NAME, new ObjectId(artistId)))
				.append("title", title)
				.append("type", type)
				.append("description", description);
		client.getDatabase(MUSEUM_DB_NAME).getCollection(WORK_COLLECTION_NAME).insertOne(work);
		return work.get("_id").toString();
	}
	
	private void removeTestArtistFromDatabase(String artistId) {
		client.getDatabase(MUSEUM_DB_NAME).getCollection(ARTIST_COLLECTION_NAME)
			.deleteOne(Filters.eq("_id", new ObjectId(artistId)));
		client.getDatabase(MUSEUM_DB_NAME).getCollection(WORK_COLLECTION_NAME)
			.deleteMany(Filters.eq("artist.$id", new ObjectId(artistId)));
	}
	
	private void removeTestWorkFromDatabase(String workId) {
		client.getDatabase(MUSEUM_DB_NAME).getCollection(WORK_COLLECTION_NAME)
			.deleteOne(Filters.eq("_id", new ObjectId(workId)));
	}

}
