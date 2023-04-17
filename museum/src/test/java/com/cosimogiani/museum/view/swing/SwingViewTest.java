package com.cosimogiani.museum.view.swing;

import org.assertj.swing.core.matcher.JButtonMatcher;
import org.assertj.swing.core.matcher.JLabelMatcher;
import org.assertj.swing.edt.GuiActionRunner;
import org.assertj.swing.fixture.FrameFixture;
import org.assertj.swing.junit.runner.GUITestRunner;
import org.assertj.swing.junit.testcase.AssertJSwingJUnitTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

@RunWith(GUITestRunner.class)
public class SwingViewTest extends AssertJSwingJUnitTestCase {
	
	private AutoCloseable closeable;
	
	private FrameFixture window;
	
	private SwingView swingView;
	
	@Override
	protected void onSetUp() {
		closeable = MockitoAnnotations.openMocks(this);
		GuiActionRunner.execute(() -> {
			swingView = new SwingView();
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

}
