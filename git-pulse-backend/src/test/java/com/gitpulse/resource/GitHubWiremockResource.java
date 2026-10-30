package com.gitpulse.resource;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GitHubWiremockResource implements QuarkusTestResourceLifecycleManager {
    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();

        String threeDaysAgo = Instant.now().minus(3, ChronoUnit.DAYS).toString();
        String oneDayAgo = Instant.now().minus(1, ChronoUnit.DAYS).toString();

        wireMockServer.stubFor(get(urlEqualTo("/repos/testowner/testrepo/pulls?state=open"))
            .willReturn(aResponse()
                .withHeader("Content-Type", "application/json")
                .withBody("""
                [
                    {"id": 101, "number": 1, "title": "Stuck PR", "created_at": "%s", "user": {"login": "dev1"}},
                    {"id": 102, "number": 2, "title": "Approved PR", "created_at": "%s", "user": {"login": "dev2"}},
                    {"id": 103, "number": 3, "title": "New PR", "created_at": "%s", "user": {"login": "dev3"}}
                ]
                """.formatted(threeDaysAgo, threeDaysAgo, oneDayAgo))));

        wireMockServer.stubFor(get(urlEqualTo("/repos/testowner/testrepo/pulls/1/reviews"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json").withBody("[]")));

        wireMockServer.stubFor(get(urlEqualTo("/repos/testowner/testrepo/pulls/2/reviews"))
            .willReturn(aResponse().withHeader("Content-Type", "application/json")
                .withBody("[{\"state\": \"APPROVED\"}]")));

        return Map.of("quarkus.rest-client.github-api.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
