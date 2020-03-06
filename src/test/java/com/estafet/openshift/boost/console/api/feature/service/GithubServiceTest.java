package com.estafet.openshift.boost.console.api.feature.service;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

@Ignore
public class GithubServiceTest {

	GithubService githubService;

	@Before
	public void before() throws Exception, SecurityException {
		githubService = new GithubService();
		RestTemplate restTemplate = new RestTemplateBuilder()
				.basicAuthorization("dennyjoe", "24c1176df4c4a821da83be091b2d5b72b7924cfb").build();

		Field restTemplateField = githubService.getClass().getDeclaredField("restTemplate");
		restTemplateField.setAccessible(true);
		restTemplateField.set(githubService, restTemplate);
	}

	@Test
	public void testGetVersionForCommit_1() {
		assertEquals("0.0.6", githubService.getVersionForCommit("Estafet-LTD", "estafet-blockchain-demo-bank-ms",
				"717aee6879fb396f81ce35b34646aa8dc098d052"));
	}

	@Test
	public void testGetVersionForCommit_2() {
		assertEquals("0.0.5", githubService.getVersionForCommit("Estafet-LTD", "estafet-blockchain-demo-bank-ms",
				"6dcffa80f14705da803fa6e42dcaea984e024652"));
	}

	@Test
	public void testGetVersionForCommit_3() {
		assertEquals("0.0.1", githubService.getVersionForCommit("Estafet-LTD", "estafet-blockchain-demo-bank-ms",
				"ab42581a93b5ee6df4ea6a71c71c6c9c8d28da44"));
	}

	@Test
	public void testGetVersionForCommit_4() {
		assertEquals("1.0.5", githubService.getVersionForCommit("Estafet-LTD", "estafet-blockchain-demo-bank-ms",
				"31763422ec9b79aea1cd81690fcfa13fbeb1986b"));
	}

	@Test
	public void testGetVersionForCommit_5() {
		assertEquals("1.0.5", githubService.getVersionForCommit("Estafet-LTD", "estafet-blockchain-demo-bank-ms",
				"31763422ec9b79aea1cd81690fcfa13fbeb1986b"));
	}
	
	@Test
	public void testGetVersionForCommit_6() {
		assertEquals("1.0.5", githubService.getVersionForCommit("Estafet-LTD", "estafet-blockchain-demo-bank-ms",
				"86e1c5675b93a862f05c80ce50252c9f261433f6"));
	}

}
