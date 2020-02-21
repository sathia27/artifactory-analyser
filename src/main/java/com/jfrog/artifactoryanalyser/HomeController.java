package com.jfrog.artifactoryanalyser;
import com.jfrog.artifactoryanalyser.model.Artifactory;
import com.jfrog.artifactoryanalyser.service.ArtifactoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class HomeController {

    @Autowired
    private ArtifactoryService artifactoryService;

    @RequestMapping("/")
    public List<Artifactory> index(@RequestParam(name = "repo") String repoName, @RequestParam(defaultValue = "2", name = "topn_repos") Integer topNRepos) {
        List<Artifactory> artifactories  = artifactoryService.listArtifactsByRepos(repoName).stream()
                .sorted(Comparator.comparing(Artifactory::getDownloadCount).reversed())
                .limit(topNRepos).collect(Collectors.toList());

        return artifactories;
    }
}
