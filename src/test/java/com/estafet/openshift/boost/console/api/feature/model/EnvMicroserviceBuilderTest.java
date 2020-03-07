package com.estafet.openshift.boost.console.api.feature.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class EnvMicroserviceBuilderTest {

	Env buildEnv, testEnv;
	Repo bankRepo, walletRepo;

	@Before
	public void before() {
		buildEnv = Env.builder().setName("build").setLive(false).build();
		testEnv = Env.builder().setName("test").setLive(false).build();
		bankRepo = Repo.builder().setName("bank").setMicroservice("bank").build();
		walletRepo = Repo.builder().setName("wallet").setMicroservice("wallet").build();
	}

	@Test
	public void testBuild() {
		EnvMicroservice.builder()
				.setDeployedDate("")
				.setEnv(buildEnv)
				.setVersion("1.0.0")
				.setRepo(bankRepo)
				.build();
		EnvMicroservice.builder()
				.setDeployedDate("")
				.setEnv(buildEnv)
				.setVersion("1.0.0")
				.setRepo(bankRepo)
				.build();
		assertEquals(1, buildEnv.getMicroservices().size());
	}

}
