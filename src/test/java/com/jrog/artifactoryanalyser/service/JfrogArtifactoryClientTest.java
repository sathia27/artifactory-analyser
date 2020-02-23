package com.jrog.artifactoryanalyser.service;

import com.github.paweladamski.httpclientmock.HttpClientMock;
import com.jfrog.artifactoryanalyser.Application;
import com.jfrog.artifactoryanalyser.model.Artifactory;
import com.jfrog.artifactoryanalyser.model.request.ArtifactoryRequest;
import com.jfrog.artifactoryanalyser.service.ArtifactoryClient;
import com.jfrog.artifactoryanalyser.service.JfrogArtifactoryClient;
import io.specto.hoverfly.junit.core.SimulationSource;
import io.specto.hoverfly.junit.dsl.HoverflyDsl;
import io.specto.hoverfly.junit.dsl.ResponseCreators;
import io.specto.hoverfly.junit.rule.HoverflyRule;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@TestPropertySource(locations="classpath:test.properties")
public class JfrogArtifactoryClientTest {


    @Autowired
    private ArtifactoryClient artifactoryClient;

    @Mock
    private HttpClient httpClient;

    @ClassRule
    public static HoverflyRule hoverflyRule1 = HoverflyRule.inSimulationMode(SimulationSource.dsl(
            HoverflyDsl.service("http://jrog.com")
                    .post("/artifactory/api/search/aql").anyBody()
                    .willReturn(ResponseCreators.success("" +
                            "{\n" +
                            "\"results\" : [ {\n" +
                            "  \"repo\" : \"jcenter-cache\",\n" +
                            "  \"path\" : \"asm/asm-parent/3.3\",\n" +
                            "  \"name\" : \"asm-parent-3.3.pom\",\n" +
                            "  \"type\" : \"file\",\n" +
                            "  \"size\" : 4330,\n" +
                            "  \"created\" : \"2019-04-22T22:25:38.975Z\",\n" +
                            "  \"created_by\" : \"anonymous\",\n" +
                            "  \"modified\" : \"2010-10-06T13:06:48.000Z\",\n" +
                            "  \"modified_by\" : \"anonymous\",\n" +
                            "  \"updated\" : \"2019-04-22T22:25:38.976Z\"\n" +
                            "}]}", "application/json")),

            HoverflyDsl.service("http://jrog.com")
                    .get("/artifactory/api/storage/jcenter-cache/asm/asm-parent/3.3/asm-parent-3.3.pom").queryParam("stats")
                    .willReturn(ResponseCreators.success("" +
                            "{\n" +
                            "  \"downloadCount\" : 4,\n" +
                            "  \"lastDownloaded\" : 14550050503,\n" +
                            "  \"lastDownloadedBh\" : \"xray\",\n" +
                            "  \"remoteDownloadCount\" : 2\n" +
                            "}", "application/json"))
    ));



    @Test
    public void fetchArtifactResponse(){
        ArtifactoryRequest artifactoryRequest = new ArtifactoryRequest();
        artifactoryRequest.setRepo("jcenter");

        List<Artifactory> artifactories = artifactoryClient.listArtifacts(artifactoryRequest);
        Assertions.assertEquals(artifactories.toArray().length, 1);
        Assertions.assertEquals(artifactories.get(0).getDownloadCount(), 4);
    }

}
