package com.jfrog.artifactoryanalyser.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Artifactory {

    @JsonProperty("repo")
    private String repo;

    @Getter
    @JsonProperty("path")
    private String path;

    @Getter
    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("size")
    private Integer size;

    @Setter
    @Getter
    @JsonProperty("download_count")
    private Integer downloadCount;
}
