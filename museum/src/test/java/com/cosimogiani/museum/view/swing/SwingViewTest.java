package com.cosimogiani.museum.view.swing;

import org.assertj.swing.annotation.GUITest;
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

	@Test @GUITest
	public void testControlsInitialState() {
		window.label(JLabelMatcher.withText("ARTIST"));
		window.label(JLabelMatcher.withText("Name"));
		window.textBox("textArtistName").requireEnabled();
	}

}
