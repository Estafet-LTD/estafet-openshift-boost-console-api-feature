package com.estafet.openshift.boost.console.api.feature.util;

import static org.junit.Assert.*;

import org.junit.Test;

import com.estafet.openshift.boost.console.api.feature.util.Version;

public class VersionTest {

	@Test
	public void testIsLessThanOrEqualRevisionTrue() {
		Version v1 = new Version("1.1.0");
		Version v2 = new Version("1.1.2");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMinorTrue() {
		Version v1 = new Version("1.1.0");
		Version v2 = new Version("1.2.0");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMajorTrue() {
		Version v1 = new Version("1.1.0");
		Version v2 = new Version("2.1.0");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualTrue() {
		Version v1 = new Version("1.1.2");
		Version v2 = new Version("1.1.2");
		assertTrue(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualRevisionFalse() {
		Version v1 = new Version("1.1.2");
		Version v2 = new Version("1.1.1");
		assertFalse(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMinorFalse() {
		Version v1 = new Version("1.14.0");
		Version v2 = new Version("1.2.0");
		assertFalse(v1.isLessThanOrEqual(v2));
	}
	
	@Test
	public void testIsLessThanOrEqualMajorFalse() {
		Version v1 = new Version("13.1.0");
		Version v2 = new Version("2.1.0");
		assertFalse(v1.isLessThanOrEqual(v2));
	}	

}
