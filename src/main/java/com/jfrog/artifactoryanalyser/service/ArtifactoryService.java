package com.jfrog.artifactoryanalyser.service;

import com.jfrog.artifactoryanalyser.model.Artifactory;
import java.util.List;

public interface ArtifactoryService {
    public List<Artifactory> listArtifactsByRepos(String repoName);
    public Integer getDownloadcount(Artifactory artifactory);
}
