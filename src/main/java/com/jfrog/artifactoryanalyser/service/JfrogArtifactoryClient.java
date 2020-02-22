package com.jfrog.artifactoryanalyser.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfrog.artifactoryanalyser.model.Artifactory;
import com.jfrog.artifactoryanalyser.model.request.ArtifactRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class JfrogArtifactoryClient implements ArtifactoryClient {

    @Value("${jfrog.host}")
    private String host;

    @Value("${jfrog.apiKey}")
    private String apiKey;

    @Value("${jfrog.apiToken}")
    private String apiToken;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HttpClient httpClient;

    @Autowired
    private ExecutorService executor;

    @Override
    public List<Artifactory> listArtifacts(ArtifactRequest artifactRequest) {
        String listUrl = String.format("%s/artifactory/api/search/aql", host);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(listUrl))
                .timeout(Duration.ofSeconds(3))
                .header(apiKey, apiToken)
                .POST(HttpRequest.BodyPublishers.ofString(artifactRequest.toString())).build();
        List<Artifactory> artifacts = new ArrayList<>();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            artifacts = parseArtifacts(response.body());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return artifacts;
    }

    private List<Artifactory> parseArtifacts(String responseString) {
        ArrayList<Artifactory>  myObjects = new ArrayList<>();
        try {
            JsonNode rootNode = objectMapper.readTree(responseString);
            JsonNode artifactNode = rootNode.get("results");
            List<Future<Artifactory>> artifactsFutures = new ArrayList();
            artifactNode.forEach(node -> {
                Artifactory artifactory = objectMapper.convertValue(node, Artifactory.class);
                Future<Artifactory> artifactFuture = setDownloadCount(artifactory);
                artifactsFutures.add(artifactFuture);
            });
            artifactsFutures.forEach(artifactFuture -> {
                try {
                    myObjects.add(artifactFuture.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return myObjects;
    }

    private Future<Artifactory> setDownloadCount(Artifactory artifactory) {
        return executor.submit(() -> {
            Integer downloadCount = getDownloadCount(artifactory);
            artifactory.setDownloadCount(downloadCount);
            return artifactory;
        });
    }

    private Integer getDownloadCount(Artifactory artifactory) {
        String urlBuilder = String.format("%s/artifactory/api/storage/jcenter-cache/%s/%s?stats", host, artifactory.getPath(), artifactory.getName());
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(urlBuilder))
                .timeout(Duration.ofSeconds(30))
                .header(apiKey, apiToken)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
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

    @Bean
    private HttpClient httpClient(){
        return HttpClient.newHttpClient();
    }

    @Bean
    private ExecutorService executorService() {
        return Executors.newSingleThreadExecutor();
    }

}