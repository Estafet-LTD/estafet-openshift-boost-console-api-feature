package com.estafet.openshift.boost.console.api.feature.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class RepoUtilsTest {

	@Test
	public void testGetRepoFromURL() {
		assertEquals("estafet-blockchain-demo-bank-ms",
				RepoUtils.getRepoFromURL("Estafet-LTD", "https://github.com/Estafet-LTD/estafet-blockchain-demo-bank-ms"));
	}

}
