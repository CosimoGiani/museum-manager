package com.cosimogiani.museum;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

public class AppTest {
	
	private App app;
	
	@Before
	public void setup() {
		app = new App();
	}
    
	@Test
	public void testIncrease() {
		assertThat(app.app(true)).isEqualTo(1);
	}
	
	@Test
	public void testNoIncrease() {
		assertThat(app.app(false)).isEqualTo(0);
	}
	
}
