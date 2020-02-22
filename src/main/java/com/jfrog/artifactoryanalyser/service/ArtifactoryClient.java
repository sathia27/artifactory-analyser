package com.jfrog.artifactoryanalyser.service;

import com.jfrog.artifactoryanalyser.model.Artifactory;
import java.util.List;

public interface ArtifactoryClient {
    public List<Artifactory> listArtifacts(String repoName);
}
