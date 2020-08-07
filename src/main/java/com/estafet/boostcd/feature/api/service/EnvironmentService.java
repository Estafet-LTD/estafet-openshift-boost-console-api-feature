package com.estafet.boostcd.feature.api.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.estafet.boostcd.feature.api.dao.CommitDAO;
import com.estafet.boostcd.feature.api.dao.EnvDAO;
import com.estafet.boostcd.feature.api.dao.EnvFeatureDAO;
import com.estafet.boostcd.feature.api.dao.ProductDAO;
import com.estafet.boostcd.feature.api.dao.RepoDAO;
import com.estafet.boostcd.feature.api.dto.EnvironmentDTO;
import com.estafet.boostcd.feature.api.model.Env;
import com.estafet.boostcd.feature.api.model.EnvFeature;
import com.estafet.boostcd.feature.api.model.EnvMicroservice;
import com.estafet.boostcd.feature.api.model.Feature;
import com.estafet.boostcd.feature.api.model.Product;
import com.estafet.boostcd.feature.api.model.PromoteStatus;
import com.estafet.boostcd.feature.api.model.Repo;
import com.estafet.boostcd.feature.api.model.RepoCommit;
import com.estafet.boostcd.feature.api.model.Version;
import com.estafet.boostcd.openshift.BuildConfigParser;
import com.estafet.boostcd.openshift.OpenShiftClient;
import com.estafet.openshift.boost.messages.environments.Environment;
import com.estafet.openshift.boost.messages.environments.EnvironmentApp;
import com.estafet.openshift.boost.messages.environments.Environments;
import com.openshift.restclient.model.IBuildConfig;

@Service
public class EnvironmentService {

	private static final Logger log = LoggerFactory.getLogger(EnvironmentService.class);

	@Autowired
	private OpenShiftClient client;

	@Autowired
	private ProductDAO productDAO;
	
	@Autowired
	private EnvDAO envDAO;

	@Autowired
	private RepoDAO repoDAO;

	@Autowired
	private CommitDAO commitDAO;

	@Autowired
	private EnvFeatureDAO envFeatureDAO;

	@Transactional
	public void processEnvMessage(Environments envsMessage) {
		log.info("product - " + envsMessage.getProductId());
		if (updateEnv(envsMessage)) {
			updateRepos(envsMessage);
			updateMicroservices(envsMessage);
			updateEnvFeatures(envsMessage);
		}
		updateMigrationDate(envsMessage);
		updatePromoteStatus(envsMessage);
	}

	
	public void updatePromoteStatus(Environments environments) {
		for (Environment environment : environments.getEnvironments()) {
			Env env = envDAO.getEnv(environments.getProductId(), environment.getName());
			log.info("env - " + env.toString());
			if (env.getName().equals("build")) {
				for (EnvFeature envFeature : env.getEnvFeatures()) {
					if (envFeature.getMigratedDate() != null
							&& PromoteStatus.valueOf(envFeature.getPromoteStatus()) != PromoteStatus.FULLY_PROMOTED) {
						envFeature.setPromoteStatus(PromoteStatus.FULLY_PROMOTED.getValue());
						envFeatureDAO.update(envFeature);
					}
				}
			}
			if (env.isLive()) {
				for (EnvFeature envFeature : envFeatureDAO.getUnResolvedEnvFeatures(env.getName())) {
					envFeature.setPromoteStatus(PromoteStatus.FULLY_PROMOTED.getValue());
					envFeatureDAO.update(envFeature);
				}
			} else {
				Env nextEnv = nextUnResolvedEnv(env);
				if (nextEnv != null) {
					log.info("nextEnv - " + nextEnv.toString());
					for (EnvFeature nextEnvFeature : envFeatureDAO.getUnResolvedEnvFeatures(nextEnv.getName())) {
						log.info("nextEnvFeature - " + nextEnvFeature.toString());
						EnvFeature envFeature = env.getEnvFeature(nextEnvFeature.getFeature().getFeatureId());
						log.info("envFeature - " + envFeature);
						if (envFeature != null) {
							nextEnvFeature.updatePromoteStatus(envFeature);
							envFeatureDAO.update(nextEnvFeature);
						}
					}
				}			
			}
		}
		
	}

	private Env nextUnResolvedEnv(Env env) {
		log.info("nextUnResolvedEnv" + env.toString());
		if (env.isPreProd()) {
			return envDAO.getStagingEnv(env.getProduct().getProductId());
		} else if (env.isStaging()) {
			return envDAO.getLiveEnv(env.getProduct().getProductId());
		} else if (!env.isLive()) {
			return envDAO.getEnv(env.getProduct().getProductId(), env.getNext());
		}
		log.info("nothing for" + env.toString());
		return null;
	}

	public void updateMigrationDate(Environments environments) {
		for (Environment environment : environments.getEnvironments()) {
			Env env = envDAO.getEnv(environments.getProductId(), environment.getName());
			log.info("env - " + env.toString());
			Env nextEnv = nextMigratedEnv(env);
			if (nextEnv != null) {
				log.info("nextEnv - " + nextEnv.toString());
				List<EnvFeature> envFeatures = envFeatureDAO.getNewEnvFeatures(environment.getName());
				for (EnvFeature envFeature : envFeatures) {
					log.info("envFeature - " + envFeature.toString());
					EnvFeature nextEnvFeature = nextEnv.getEnvFeature(envFeature.getFeature().getFeatureId());
					log.info("nextEnvFeature - " + nextEnvFeature);
					if (nextEnvFeature != null) {
						envFeature.setMigratedDate(nextEnvFeature.calculateDeployedDate());
						envFeatureDAO.update(envFeature);
					}
				}
			}
		}
	}

	private Env nextMigratedEnv(Env env) {
		log.info(env.toString());
		if (env.getNext() != null) {
			if (env.getNext().equals("prod")) {
				return envDAO.getStagingEnv(env.getProduct().getProductId());
			} else {
				return envDAO.getEnv(env.getProduct().getProductId(), env.getNext());
			}
		} else if (env.getName().equals("green") || env.getName().equals("blue")) {
			return envDAO.getLiveEnv(env.getProduct().getProductId());
		}
		return null;
	}

	public boolean updateEnv(Environments environments) {
		Product product = productDAO.getProduct(environments.getProductId());
		if (product == null) {
			product = Product.builder().setProductId(environments.getProductId()).build();
			productDAO.create(product);
		}
		for (Environment environment : environments.getEnvironments()) {
			Env env = envDAO.getEnv(environments.getProductId(), environment.getName());
			if (env == null) {
				env = Env.getEnv(environment);
				product.addEnv(env);
				envDAO.createEnv(env);
				log.info("created env - " + env.getName());
				return true;
			} else if (!env.getUpdatedDate().equals(environment.getUpdatedDate())) {
				log.info("env changed - " + environment.getName());
				envDAO.updateEnv(env.merge(Env.getEnv(environment)));
				return true;
			}
		}
		return false;
	}

	public void updateRepos(Environments environments) {
		for (Environment environment : environments.getEnvironments()) {
			log.info("updateRepos for env - " + environment.getName());
			for (EnvironmentApp app : environment.getApps()) {
				updateRepo(environments.getProductId(), environment, app);
			}
			log.info("Repos successfully updated for env - " + environment.getName());
		}
	}

	private Repo updateRepo(String productId, Environment env, EnvironmentApp app) {
		IBuildConfig buildConfig = client.getBuildConfig(productId, app.getName());
		String repoUrl = new BuildConfigParser(buildConfig).getGitRepository();
		Repo repo = repoDAO.getRepoByURL(repoUrl);
		if (repo == null) {
			repo = Repo.builder().setUrl(repoUrl).setMicroservice(app.getName()).build();
			repoDAO.createRepo(repo);
		}
		return repo;
	}

	public void updateEnvFeatures(Environments environments) {
		for (Environment environment : environments.getEnvironments()) {
			log.info("update EnvFeatures for env - " + environment.getName());
			Env env = envDAO.getEnv(environments.getProductId(), environment.getName());
			for (EnvMicroservice envMicroservice : env.getMicroservices()) {
				for (RepoCommit matched : commitDAO.getMatchedForMicroservice(envMicroservice.getMicroservice())) {
					Feature feature = matched.getFeature();
					if (isEnvFeature(env, feature, matched, envMicroservice)) {
						if (!env.getFeatures().contains(feature)) {
							EnvFeature envFeature = EnvFeature.builder().setFeature(feature)
									.setDeployedDate(envMicroservice.getDeployedDate()).setEnv(env).build();
							envFeatureDAO.create(envFeature);
						}
					}
				}
			}
			env.setUpdatedDate(environment.getUpdatedDate());
			envDAO.updateEnv(env);
		}
	}

	private boolean isEnvFeature(Env env, Feature feature, RepoCommit matched, EnvMicroservice envMicroservice) {
		Version matchedVersion = new Version(matched.getTag());
		Version microserviceVersion = new Version(envMicroservice.getVersion());
		if (matchedVersion.isLessThanOrEqual(microserviceVersion)) {
			if (env.getName().equals("build") || env.getName().equals("test")) {
				return true;
			} else if (feature.getStatus().equals("Done")) {
				return true;
			}
		}
		return false;
	}

	@Transactional(readOnly = true)
	public EnvironmentDTO getEnvironment(String productId, String env) {
		return envDAO.getEnv(productId, env).getEnvironmentDTO();
	}

	public void updateMicroservices(Environments environments) {
		for (Environment environment : environments.getEnvironments()) {
			log.info("updateMicroservices for env - " + environment.getName());
			Env env = envDAO.getEnv(environments.getProductId(), environment.getName());
			if (env != null) {
				for (EnvironmentApp app : environment.getApps()) {
					log.info("create envMicroservice for " + app.getName());
					env.updateMicroservice(app, repoDAO.getRepoByMicroservice(app.getName()));
				}
				env.setUpdatedDate(environment.getUpdatedDate()); // reset the date
				envDAO.updateEnv(env);	
			}
		}
	}

	@Transactional(readOnly = true)
	public List<EnvironmentDTO> getEnvironments(String productId) {
		return getEnvironments(productId, "build");
	}

	public List<EnvironmentDTO> getEnvironments(String productId, String envId) {
		return getEnvironments(productId, envId, new ArrayList<EnvironmentDTO>());
	}

	private List<EnvironmentDTO> getEnvironments(String productId, String envId, List<EnvironmentDTO> envs) {
		Env env = envDAO.getEnv(productId, envId);
		envs.add(env.getEnvironmentDTO());
		if (!env.getNext().equals("prod")) {
			return getEnvironments(productId, env.getNext(), envs);
		} else {
			Env green = envDAO.getEnv(productId, "green");
			Env blue = envDAO.getEnv(productId, "blue");
			if (green.getLive()) {
				envs.add(blue.getEnvironmentDTO());
				envs.add(green.getEnvironmentDTO());
			} else {
				envs.add(green.getEnvironmentDTO());
				envs.add(blue.getEnvironmentDTO());
			}
			return envs;
		}
	}

}
