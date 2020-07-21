package com.estafet.boostcd.feature.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.estafet.boostcd.commons.model.API;
import com.estafet.boostcd.feature.api.dto.EnvironmentDTO;
import com.estafet.boostcd.feature.api.service.EnvironmentService;

@RestController
public class FeatureController {

	@Autowired
	private EnvironmentService environmentService;
	
	@Value("${app.version}")
	private String appVersion;
	
	@GetMapping("/api")
	public API getAPI() {
		return new API(appVersion);
	}
	
	@GetMapping("/environment/{env}")
	public EnvironmentDTO getEnvironment(@PathVariable String env) {
		return environmentService.getEnvironment(env);
	}
	
	@GetMapping("/environments")
	public List<EnvironmentDTO> getEnvironments() {
		return environmentService.getEnvironments();
	}
	
}