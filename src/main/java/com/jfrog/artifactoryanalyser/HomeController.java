package com.jfrog.artifactoryanalyser;
import com.jfrog.artifactoryanalyser.model.Artifactory;
import com.jfrog.artifactoryanalyser.model.request.ArtifactRequest;
import com.jfrog.artifactoryanalyser.service.ArtifactoryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HomeController {

    @Autowired
    private ArtifactoryClient artifactoryClient;

    @RequestMapping(value = "/", produces = "application/json")
    public List<Artifactory> index(ArtifactRequest artifactRequest, @RequestParam(name = "topn_results", defaultValue = "2") Integer topn_results) {
        String artifactQuery = artifactRequest.toString();
        List<Artifactory> artifactories  = artifactoryClient.listArtifacts(artifactQuery).stream()
                .sorted(Comparator.comparing(Artifactory::getDownloadCount).reversed())
                .limit(topn_results).collect(Collectors.toList());
        return artifactories;
    }
}
