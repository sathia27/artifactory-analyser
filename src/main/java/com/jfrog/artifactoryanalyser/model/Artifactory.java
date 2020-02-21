package com.jfrog.artifactoryanalyser.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jfrog.artifactoryanalyser.service.ArtifactoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifactory {

    @JsonProperty("repo")
    private String repo;

    @JsonProperty("path")
    private String path;

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("size")
    private Integer size;

    @JsonProperty("download_count")
    private Integer downloadCount;

    public void  setDownloadCount(Integer downloadCount){
        this.downloadCount = downloadCount;
    }

    public Integer getDownloadCount(){
        return this.downloadCount;
    }

    public String getPath(){
        return this.path;
    }

    public String getName(){
        return this.name;
    }
}
