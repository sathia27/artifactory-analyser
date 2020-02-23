package com.jfrog.artifactoryanalyser.model.request;

import lombok.Data;
import org.apache.tomcat.util.buf.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
public class ArtifactoryRequest {
    private String repo;
    private String path;

    public String toString(){
        List<String> queryParams = new ArrayList<>();
        if(this.repo != null){
            queryParams.add(String.format("    {\n" +
                    "        \"repo\":{\"$eq\":\"%s\"}\n}", repo));
        }
        if(this.path != null){
            queryParams.add(String.format("    {\n" +
                    "        \"path\":{\"$eq\":\"%s\"}\n}", path));
        }

        String queryParam  = StringUtils.join(queryParams);
        return String.format("items.find(\n" + queryParam +  ")");
    }
}
