package com.jfrog.artifactoryanalyser.service;

import com.jfrog.artifactoryanalyser.model.Artifactory;
import com.jfrog.artifactoryanalyser.model.request.ArtifactRequest;

import java.util.List;

public interface ArtifactoryClient {
    public List<Artifactory> listArtifacts(ArtifactRequest artifactRequest);
}
