package com.jfrog.artifactoryanalyser.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfrog.artifactoryanalyser.model.Artifactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class JfrogArtifactory implements ArtifactoryService {

    @Value("${jfrog.host}")
    private String host;

    @Value("${jfrog.apiKey}")
    private String apiKey;

    @Value("${jfrog.apiToken}")
    private String apiToken;

    @Autowired
    private ObjectMapper objectMapper;

    private ExecutorService executor = Executors.newFixedThreadPool(5);

    @Override
    public List<Artifactory> listArtifactsByRepos(String repoName) {
        String listUrl = String.format("%s/artifactory/api/search/aql", host);
        String postBody = String.format("items.find(\n" +
                "    {\n" +
                "        \"repo\":{\"$eq\":\"%s\"}\n" +
                "} )", repoName);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(listUrl))
                .timeout(Duration.ofSeconds(3))
                .header(apiKey, apiToken)
                .POST(HttpRequest.BodyPublishers.ofString(postBody)).build();
        HttpClient client = HttpClient.newHttpClient();
        List<Artifactory> artifactories = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            artifactories = parseArtifactories(response.body());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return artifactories;
    }

    public Future<Artifactory> setDowloadCount(Artifactory artifactory) {
        return executor.submit(() -> {
            Integer downloadCount = getDownloadcount(artifactory);
            artifactory.setDownloadCount(downloadCount);
            return artifactory;
        });
    }

    @Override
    public Integer getDownloadcount(Artifactory artifactory) {
        String urlBuilder = String.format("%s/artifactory/api/storage/jcenter-cache/%s/%s?stats", host, artifactory.getPath(), artifactory.getName());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder))
                .timeout(Duration.ofSeconds(3))
                .header("X-JFrog-Art-Api", "AKCp5ekHUv8J6Eq349Gz5cRtSTGCzEijhdNPVHH1iNXNy21ntax2stCsmPjWsPEcRNrCpareW")
                .GET()
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode rootNode = objectMapper.readTree(response.body());
            Integer downloadCount = rootNode.get("downloadCount").asInt();
            return downloadCount;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private List<Artifactory> parseArtifactories(String responeString) throws JsonProcessingException {
        ArrayList<Artifactory>  myObjects = new ArrayList<>();
        JsonNode rootNode = objectMapper.readTree(responeString);
        JsonNode artficatoryNode = rootNode.get("results");

        List<Future<Artifactory>> artifactoryFutures = new ArrayList<Future<Artifactory>>();
        artficatoryNode.forEach(node -> {
            Artifactory artifactory = objectMapper.convertValue(node, Artifactory.class);
            Future<Artifactory> artifactoryFuture = setDowloadCount(artifactory);
            artifactoryFutures.add(artifactoryFuture);
        });
        artifactoryFutures.forEach(artficatorFuture -> {
            try {
                myObjects.add(artficatorFuture.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return myObjects;
    }
}