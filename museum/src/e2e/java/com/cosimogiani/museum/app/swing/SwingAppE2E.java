package com.cosimogiani.museum.app.swing;

import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.swing.launcher.ApplicationLauncher.application;

import javax.swing.JFrame;

import org.assertj.swing.core.GenericTypeMatcher;
import org.assertj.swing.finder.WindowFinder;
import org.assertj.swing.fixture.FrameFixture;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

@RunWith(GUITestRunner.class)
public class SwingAppE2E extends AssertJSwingJUnitTestCase {
	
	private static final String MUSEUM_DB_NAME = "test-museum";
	private static final String ARTIST_COLLECTION_NAME = "test-artist";
	private static final String WORK_COLLECTION_NAME = "test-work";
	
	private static final String ARTIST_FIXTURE_1_NAME = "test1";
	private static final String ARTIST_FIXTURE_2_NAME = "test2";
	
	private MongoClient client;
	private FrameFixture window;
	
	@Override
	protected void onSetUp() {
		client = new MongoClient(new ServerAddress("localhost", 27017));
		client.getDatabase(MUSEUM_DB_NAME).drop();
		addTestArtistToDatabase(ARTIST_FIXTURE_1_NAME);
		addTestArtistToDatabase(ARTIST_FIXTURE_2_NAME);
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
			.anySatisfy(e -> e.contains(ARTIST_FIXTURE_1_NAME))
			.anySatisfy(e -> e.contains(ARTIST_FIXTURE_2_NAME));
		assertThat(window.comboBox("comboBoxSearch").contents())
			.anySatisfy(e -> e.contains(ARTIST_FIXTURE_1_NAME))
			.anySatisfy(e -> e.contains(ARTIST_FIXTURE_2_NAME));
	}
	
	private void addTestArtistToDatabase(String artistName) {
		Document artist = new Document().append("name", artistName);
		client.getDatabase(MUSEUM_DB_NAME).getCollection(ARTIST_COLLECTION_NAME).insertOne(artist);
	}

}
