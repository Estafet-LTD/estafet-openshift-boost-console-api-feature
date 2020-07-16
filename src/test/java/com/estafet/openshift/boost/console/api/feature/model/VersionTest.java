package com.estafet.openshift.boost.console.api.feature.model;

import static org.junit.Assert.*;

import org.junit.Test;

import com.estafet.boostcd.feature.api.model.Version;

public class VersionTest {

	@Test
	public void testIsLessThanOrEqualRevisionTrue() {
		Version v1 = new Version("v1.1.0");
		Version v2 = new Version("v1.1.2");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualRevisionTrue_2() {
		Version v1 = new Version("v0.0.1");
		Version v2 = new Version("v1.1.0");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMinorTrue() {
		Version v1 = new Version("v1.1.0");
		Version v2 = new Version("v1.2.0");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMajorTrue() {
		Version v1 = new Version("v1.1.0");
		Version v2 = new Version("v2.1.0");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualTrue() {
		Version v1 = new Version("v1.1.2");
		Version v2 = new Version("v1.1.2");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualRevisionFalse() {
		Version v1 = new Version("v1.1.2");
		Version v2 = new Version("v1.1.1");
		assertFalse(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMinorFalse() {
		Version v1 = new Version("v1.14.0");
		Version v2 = new Version("v1.2.0");
		assertFalse(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMajorFalse() {
		Version v1 = new Version("v13.1.0");
		Version v2 = new Version("v2.1.0");
		assertFalse(v1.isLessThanOrEqual(v2));
	}	

}
