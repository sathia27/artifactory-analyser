package com.jfrog.artifactoryanalyser.service;

import com.jfrog.artifactoryanalyser.model.Artifactory;
import com.jfrog.artifactoryanalyser.model.request.ArtifactoryRequest;

import java.util.List;

public interface ArtifactoryClient {
    public List<Artifactory> listArtifacts(ArtifactoryRequest artifactoryRequest);
}
