package com.cosimogiani.museum.view.swing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.fixture.JButtonFixture;
import org.assertj.swing.fixture.JComboBoxFixture;
import org.assertj.swing.fixture.JTextComponentFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cosimogiani.museum.controller.Controller;
import com.cosimogiani.museum.model.Artist;
import com.cosimogiani.museum.model.Work;

@RunWith(GUITestRunner.class)
public class SwingViewTest extends AssertJSwingJUnitTestCase {
	
	private AutoCloseable closeable;
	
	private FrameFixture window;
	
	private SwingView swingView;
	
	@Mock
	private Controller controller;
	
	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			swingView = new SwingView();
			swingView.setController(controller);
			return swingView;
		});
		window = new FrameFixture(robot(), swingView);
		window.show();
	}
	
	@Override
	protected void onTearDown() throws Exception {
		closeable.close();
	}

	@Test
	public void testControlsInitialState() {
		window.label(JLabelMatcher.withText("ARTIST"));
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("textArtistName").requireEnabled();
		window.button(JButtonMatcher.withText("ADD ARTIST")).requireDisabled();
		window.list("listArtist");
		window.button(JButtonMatcher.withText("DELETE ARTIST")).requireDisabled();
		window.label("lblArtistError").requireText(" ");
		window.label(JLabelMatcher.withText("WORK"));
		window.textBox("textWorkTitle").requireEnabled();
		window.button(JButtonMatcher.withText("ADD WORK")).requireDisabled();
		window.label(JLabelMatcher.withText("Type"));
		window.comboBox("comboBoxWorkType").requireSelection("---");
		window.label(JLabelMatcher.withText("Description"));
		window.textBox("textDescription").requireEnabled();
		window.list("listWorks");
		window.button(JButtonMatcher.withText("DELETE WORK")).requireDisabled();
		window.label("lblWorkError").requireText(" ");
		window.label(JLabelMatcher.withText("SEARCH"));
		window.label("infoText1").requireText("Select an artist from the list, then press the");
		window.label("infoText2").requireText("\"Search\" button to see the works of the selected");
		window.label("infoText3").requireText("artist and related informations.");
		window.comboBox("comboBoxSearch");
		window.button(JButtonMatcher.withText("SEARCH")).requireDisabled();
		window.list("listSearch");
		window.label("lblSearchError").requireText(" ");
	}
	
	@Test
	public void testShowArtistsShouldAddArtistsToArtistsListAndSearchComboBox() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		GuiActionRunner.execute(() -> swingView.showArtists(Arrays.asList(artist1, artist2)) );
		String[] listArtist = window.list("listArtist").contents();
		assertThat(listArtist).containsExactly(artist1.toString(), artist2.toString());
		String[] artistsComboBoxSearch = window.comboBox("comboBoxSearch").contents();
		assertThat(artistsComboBoxSearch).containsExactly(artist1.toString(), artist2.toString());
	}
	
	@Test
	public void testShowArtistsShouldAddArtistsToArtistsListAndSearchComboBoxAndClearPreviousElements() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		GuiActionRunner.execute(() -> {
			swingView.getArtistListModel().addElement(artist1);
			swingView.getComboBoxSearchModel().addElement(artist1);
			swingView.showArtists(Arrays.asList(artist2));
		});
		String[] listArtist = window.list("listArtist").contents();
		assertThat(listArtist).containsExactly(artist2.toString());
		String[] artistsComboBoxSearch = window.comboBox("comboBoxSearch").contents();
		assertThat(artistsComboBoxSearch).containsExactly(artist2.toString());
	}
	
	@Test
	public void testShowWorksShouldAddWorksToWorksList() {
		Artist artist = new Artist("1", "test1");
		Work work1 = new Work("1", artist, "title1", "type1", "descrp1");
		Work work2 = new Work("2", artist, "title2", "type2", "descrp2");
		GuiActionRunner.execute(() -> swingView.showWorks(Arrays.asList(work1, work2)) );
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsExactly(work1.toString(), work2.toString());
	}
	
	@Test
	public void testShowWorksShouldAddWorksToWorksListAndClearPreviousElements() {
		Artist artist = new Artist("1", "test1");
		Work work1 = new Work("1", artist, "title1", "type1", "descrp1");
		Work work2 = new Work("2", artist, "title2", "type2", "descrp2");
		GuiActionRunner.execute(() -> {
			swingView.getWorkListModel().add(0, work1);
			swingView.showWorks(Arrays.asList(work2));
		});
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsExactly(work2.toString());
	}
	
	@Test
	public void testWhenNameIsNotEmptyThenAddArtistButtonShouldBeEnabled() {
		JTextComponentFixture nameArtistTextField = window.textBox("textArtistName");
		nameArtistTextField.enterText("test");
		window.button("btnArtistAdd").requireEnabled();
	}
	
	@Test
	public void testWhenNameIsBlankThenAddArtistButtonShouldBeDisabled() {
		JTextComponentFixture nameArtistTextField = window.textBox("textArtistName");
		nameArtistTextField.enterText(" ");
		window.button("btnArtistAdd").requireDisabled();
	}
	
	@Test
	public void testArtistAddedShouldAddArtistToArtistsListAndSearchComboBoxAndResetEverything() {
		Artist artist = new Artist("1", "test");
		window.textBox("textArtistName").enterText("test");
		window.button("btnArtistAdd").requireEnabled();
		GuiActionRunner.execute(() -> swingView.artistAdded(artist));
		String[] listArtist = window.list("listArtist").contents();
		assertThat(listArtist).contains(artist.toString());
		String[] artistsComboBoxSearch = window.comboBox("comboBoxSearch").contents();
		assertThat(artistsComboBoxSearch).contains(artist.toString());
		window.textBox("textArtistName").requireText("");
		window.label("lblArtistError").requireText("");
		window.button("btnArtistAdd").requireDisabled();
	}
	
	@Test
	public void testArtistAddedShouldAddNewArtistAndUpdateArtistsListAndSearchComboBox() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		GuiActionRunner.execute(() -> {
			swingView.getArtistListModel().addElement(artist1);
			swingView.getComboBoxSearchModel().addElement(artist1);
			swingView.artistAdded(artist2);
		});
		String[] listArtist = window.list("listArtist").contents();
		assertThat(listArtist).contains(artist1.toString(), artist2.toString());
		String[] artistsComboBoxSearch = window.comboBox("comboBoxSearch").contents();
		assertThat(artistsComboBoxSearch).contains(artist1.toString(), artist2.toString());
	}
	
	@Test
	public void testAddArtistButtonShouldDelegateToControllerNewArtist() {
		window.textBox("textArtistName").enterText("test");
		window.button(JButtonMatcher.withText("ADD ARTIST")).click();
		verify(controller).newArtist(new Artist("test"));
	}
	
	@Test
	public void testDeleteArtistButtonShouldBeEnabledOnlyWhenAnArtistIsSelectedFromTheList() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Artist> artistListModel = swingView.getArtistListModel();
			artistListModel.addElement(artist1);
			artistListModel.addElement(artist2);
		});
		window.list("listArtist").selectItem(1);
		JButtonFixture deleteArtistButton = window.button(JButtonMatcher.withText("DELETE ARTIST"));
		deleteArtistButton.requireEnabled();
		window.list("listArtist").clearSelection();
		deleteArtistButton.requireDisabled();
	}
	
	@Test
	public void testArtistRemovedShouldRemoveArtistFromArtistsListAndComboBox() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Artist> artistListModel = swingView.getArtistListModel();
			artistListModel.addElement(artist1);
			artistListModel.addElement(artist2);
			DefaultComboBoxModel<Artist> comboBoxSearchModel = swingView.getComboBoxSearchModel();
			comboBoxSearchModel.addElement(artist1);
			comboBoxSearchModel.addElement(artist2);
		});
		GuiActionRunner.execute(() -> swingView.artistRemoved(artist1));
		String[] listArtist = window.list("listArtist").contents();
		assertThat(listArtist).containsOnly(artist2.toString());
		String[] artistsComboBoxSearch = window.comboBox("comboBoxSearch").contents();
		assertThat(artistsComboBoxSearch).containsOnly(artist2.toString());
	}
	
	@Test
	public void testDeleteArtistButtonShouldDelegateToControllerDeleteArtist() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Artist> artistListModel = swingView.getArtistListModel();
			artistListModel.addElement(artist1);
			artistListModel.addElement(artist2);
		});
		window.list("listArtist").selectItem(1);
		window.button(JButtonMatcher.withText("DELETE ARTIST")).click();
		verify(controller).deleteArtist(artist2);
	}
	
	@Test
	public void testShowArtistErrorShouldShowTheMessageInTheArtistErrorLabel() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> swingView.showArtistError("error", artist));
		window.label("lblArtistError").requireText("error: " + artist.getName());
	}
	
	@Test
	public void testWorkAddedShouldAddWorkToWorksListAndResetEverything() {
		Artist artist = new Artist("1", "test");
		Work work = new Work(artist, "title", "type", "description");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Artist> listArtist = swingView.getArtistListModel();
			listArtist.addElement(artist);
		});
		JTextComponentFixture textWorkTitle = window.textBox("textWorkTitle");
		JComboBoxFixture comboBoxWorkType = window.comboBox("comboBoxWorkType");
		JTextComponentFixture textDescription = window.textBox("textDescription");
		
		textWorkTitle.enterText("title");
		comboBoxWorkType.selectItem(1);
		textDescription.enterText("description");
		window.list("listArtist").selectItem(0);
		window.button("btnWorkAdd").requireEnabled();
		
		GuiActionRunner.execute(() -> swingView.workAdded(work));
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).contains(work.toString());
		textWorkTitle.requireText("");
		textDescription.requireText("");
		comboBoxWorkType.requireSelection("---");
		window.label("lblWorkError").requireText("");
		window.button("btnWorkAdd").requireDisabled();
	}
	
	@Test
	public void testWhenAnArtistIsSelectedAndWorkFieldsAreNotEmptyThenAddWorkButtonShouldBeEnabled() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> swingView.getArtistListModel().addElement(artist));
		JTextComponentFixture titleWorkTextField = window.textBox("textWorkTitle");
		titleWorkTextField.enterText("title");
		JComboBoxFixture typeComboBox = window.comboBox("comboBoxWorkType");
		typeComboBox.selectItem(1);
		JTextComponentFixture textDescription = window.textBox("textDescription");
		textDescription.enterText("description");
		window.list("listArtist").selectItem(0);
		window.button("btnWorkAdd").requireEnabled();
	}
	
	@Test
	public void testWhenNoArtistIsSelectedOrWorkFieldsAreEmptyThenAddWorkButtonShouldBeDisabled() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> swingView.getArtistListModel().addElement(artist));
		JTextComponentFixture titleWorkTextField = window.textBox("textWorkTitle");
		JComboBoxFixture typeComboBox = window.comboBox("comboBoxWorkType");
		JTextComponentFixture textDescription = window.textBox("textDescription");
		
		window.list("listArtist").requireNoSelection();
		titleWorkTextField.enterText("title");
		typeComboBox.selectItem(1);
		textDescription.enterText("description");
		window.button("btnWorkAdd").requireDisabled();
		
		clearFieldsForAddWork();
		
		window.list("listArtist").selectItem(0);
		titleWorkTextField.enterText("");
		typeComboBox.selectItem(1);
		textDescription.enterText("description");
		window.button("btnWorkAdd").requireDisabled();
		
		clearFieldsForAddWork();
		
		window.list("listArtist").selectItem(0);
		titleWorkTextField.enterText("title");
		typeComboBox.selectItem(0);
		textDescription.enterText("description");
		window.button("btnWorkAdd").requireDisabled();
		
		clearFieldsForAddWork();
		
		window.list("listArtist").selectItem(0);
		titleWorkTextField.enterText("title");
		typeComboBox.selectItem(1);
		textDescription.enterText("");
		window.button("btnWorkAdd").requireDisabled();
	}
	
	@Test
	public void testAddWorkButtonShouldDelegateToControllerNewWork() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> {
			DefaultListModel<Artist> artistListModel = swingView.getArtistListModel();
			artistListModel.addElement(artist);
		});
		window.list("listArtist").selectItem(0);
		window.textBox("textWorkTitle").enterText("title");
		window.comboBox("comboBoxWorkType").selectItem(1);
		window.textBox("textDescription").enterText("description");
		window.button(JButtonMatcher.withText("ADD WORK")).click();
		verify(controller).newWork(new Work(artist, "title", "Painting", "description"));
	}
	
	@Test
	public void testWorkRemovedShouldRemoveWorkFromWorksListAndSearchList() {
		Artist artist = new Artist("1", "test");
		Work work = new Work(artist, "title", "type", "description");
		GuiActionRunner.execute(() -> {
			swingView.getWorkListModel().addElement(work);
			swingView.getSearchListModel().addElement(work);
			swingView.workRemoved(work);
		});
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).isEmpty();
		String[] listSearch = window.list("listSearch").contents();
		assertThat(listSearch).isEmpty();
	}
	
	@Test
	public void testDeleteWorkButtonShouldBeEnabledOnlyWhenAWorkIsSelectedFomTheList() {
		Artist artist = new Artist("1", "test");
		Work work = new Work(artist, "title", "type", "description");
		GuiActionRunner.execute(() -> swingView.getWorkListModel().addElement(work));
		window.list("listWorks").selectItem(0);
		window.button("btnWorkDelete").requireEnabled();
	}
	
	@Test
	public void testDeleteWorkButtonShouldDelegateToControllerDeleteWork() {
		Artist artist = new Artist("1", "test");
		Work work = new Work(artist, "title", "type", "description");
		GuiActionRunner.execute(() -> swingView.getWorkListModel().addElement(work));
		window.list("listWorks").selectItem(0);
		window.button(JButtonMatcher.withText("DELETE WORK")).click();
		verify(controller).deleteWork(work);
	}
	
	@Test
	public void testRemoveWorksOfArtistShouldUpdateWorksListAndSearchListByRemovingWorks() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		GuiActionRunner.execute(() -> {
			swingView.getWorkListModel().addElement(work1);
			swingView.getWorkListModel().addElement(work2);
			swingView.getSearchListModel().addElement(work1);
			swingView.getSearchListModel().addElement(work2);
			swingView.removeWorksOfArtist(artist2);
		});
		String[] listWorks = window.list("listWorks").contents();
		assertThat(listWorks).containsOnly(work1.toString());
		String[] listSearch = window.list("listSearch").contents();
		assertThat(listSearch).containsOnly(work1.toString());
	}
	
	@Test
	public void testShowWorkErrorShouldShowTheMessageInTheWorkErrorLabel() {
		Artist artist = new Artist("1", "test");
		Work work = new Work(artist, "title", "type", "description");
		GuiActionRunner.execute(() -> swingView.showWorkError("error", work));
		window.label("lblWorkError").requireText("error: " + work.getTitle());
	}
	
	@Test
	public void testShowWorksInSearchListShouldAddWorksToSearchListWhenArtistInComboBox() {
		Artist artist = new Artist("1", "test");
		Work work = new Work(artist, "title", "type", "description");
		GuiActionRunner.execute(() -> swingView.getComboBoxSearchModel().addElement(artist));
		window.comboBox("comboBoxSearch").selectItem(0);
		GuiActionRunner.execute(() -> swingView.showWorksInSearchList(Arrays.asList(work)));
		String[] listSearch = window.list("listSearch").contents();
		assertThat(listSearch).contains(work.toString());
	}
	
	@Test
	public void testShowWorksInSearchListIsEmptyWhenNoArtistIsInComboBox() {
		Artist artist = new Artist("1", "test");
		Work work = new Work(artist, "title", "type", "description");
		GuiActionRunner.execute(() -> swingView.showWorksInSearchList(Arrays.asList(work)));
		String[] listSearch = window.list("listSearch").contents();
		assertThat(listSearch).isEmpty();
	}
	
	@Test
	public void testShowWorksInSearchListShouldAddWorksToSearchListAndClearPreviousElements() {
		Artist artist1 = new Artist("1", "test1");
		Artist artist2 = new Artist("2", "test2");
		Work work1 = new Work(artist1, "title1", "type1", "description1");
		Work work2 = new Work(artist2, "title2", "type2", "description2");
		GuiActionRunner.execute(() -> {
			swingView.getComboBoxSearchModel().addElement(artist1);
			swingView.getComboBoxSearchModel().addElement(artist2);
			swingView.getSearchListModel().addElement(work2);
		});
		assertThat(window.list("listSearch").contents()).containsOnly(work2.toString());
		GuiActionRunner.execute(() -> swingView.showWorksInSearchList(Arrays.asList(work1)));
		assertThat(window.list("listSearch").contents()).containsExactly(work1.toString());
	}
	
	@Test
	public void testSearchButtonShouldBeEnabledOnlyWhenAnArtistFromComboBoxIsSelected() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> swingView.getComboBoxSearchModel().addElement(artist));
		window.comboBox("comboBoxSearch").selectItem(0);
		window.button("btnSearch").requireEnabled();
		
		GuiActionRunner.execute(() -> swingView.getComboBoxSearchModel().removeAllElements());
		window.button("btnSearch").requireDisabled();
	}
	
	@Test
	public void testSearchButtonShouldDelegateToControllerAllWorksByArtist() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> swingView.getComboBoxSearchModel().addElement(artist));
		window.comboBox("comboBoxSearch").selectItem(0);
		window.button(JButtonMatcher.withText("SEARCH")).click();
		verify(controller).allWorksByArtist(artist);
	}
	
	@Test
	public void testShowSearchErrorShouldShowTheMessageInTheSearchErrorLabel() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> swingView.showSearchError("error", artist));
		window.label("lblSearchError").requireText("error: " + artist.getName());
	}
	
	@Test
	public void testWhenArtistIsAddedThenResetSearchErrorLabel() {
		Artist artist = new Artist("1", "test");
		GuiActionRunner.execute(() -> {
			swingView.showSearchError("error", artist);
			swingView.artistAdded(artist);
		});
		window.label("lblSearchError").requireText("");
	}
	
	private void clearFieldsForAddWork() {
		window.list("listArtist").clearSelection();
		window.textBox("textWorkTitle").setText("");
		window.comboBox("comboBoxWorkType").selectItem(0);
		window.textBox("textDescription").setText("");
	}

}
