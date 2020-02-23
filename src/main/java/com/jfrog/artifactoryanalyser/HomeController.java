package com.jfrog.artifactoryanalyser;

import com.jfrog.artifactoryanalyser.model.Artifactory;
import com.jfrog.artifactoryanalyser.model.request.ArtifactoryRequest;
import com.jfrog.artifactoryanalyser.service.ArtifactoryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    @Autowired
    private ArtifactoryClient artifactoryClient;

    @RequestMapping(value = "/")
    public String index(ArtifactoryRequest artifactoryRequest, @RequestParam(name = "topn_results", defaultValue = "2") Integer topn_results, Model model) {
        List<Artifactory> artifacts  = artifactoryClient.listArtifacts(artifactoryRequest).stream()
                .sorted(Comparator.comparing(Artifactory::getDownloadCount).reversed())
                .limit(topn_results).collect(Collectors.toList());
        System.out.println(artifacts);
        model.addAttribute("artifacts", artifacts);
        return "artifact-index";
    }
}
