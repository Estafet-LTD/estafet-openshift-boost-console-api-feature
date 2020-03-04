package com.estafet.openshift.boost.console.api.feature.model;

import static org.junit.Assert.*;

import org.junit.Test;

public class EnvTest {

	@Test
	public void testGetPreviousEnvTest() {
		Env env = new Env();
		env.setName("test");
		assertEquals("build", env.getPreviousEnv());
	}
	
	@Test
	public void testGetPreviousEnvGreenLive() {
		Env env = new Env();
		env.setName("green");
		env.setLive(true);
		assertEquals("blue", env.getPreviousEnv());
	}
	
	@Test
	public void testGetPreviousEnvGreenStage() {
		Env env = new Env();
		env.setName("green");
		env.setLive(false);
		assertEquals("test", env.getPreviousEnv());
	}
	
	@Test
	public void testGetPreviousEnvBlueLive() {
		Env env = new Env();
		env.setName("blue");
		env.setLive(true);
		assertEquals("green", env.getPreviousEnv());
	}
	
	@Test
	public void testGetPreviousEnvBlueStage() {
		Env env = new Env();
		env.setName("blue");
		env.setLive(false);
		assertEquals("test", env.getPreviousEnv());
	}	

}
