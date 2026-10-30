package com.gitpulse.resource;

import com.gitpulse.dao.TrackedRepositoryDao;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
@QuarkusTestResource(GitHubWiremockResource.class)
class MetricsResourceTest {

    @InjectMock
    TrackedRepositoryDao repositoryDao;

    @BeforeEach
    void setup() {
        Mockito.doNothing().when(repositoryDao).upsertRepoLookup(anyString(), anyString());
    }

    @Test
    void testGetRepoMetrics_IdentifiesStuckPrs() {
        given()
          .when().get("/api/v1/metrics/testowner/testrepo")
          .then()
             .statusCode(200)
             .body("owner", is("testowner"))
             .body("repo", is("testrepo"))
             .body("stuckPrs", hasSize(1))
             .body("stuckPrs[0].number", is(1))
             .body("stuckPrs[0].title", is("Stuck PR"))
             .body("stuckPrs[0].author", is("dev1"));
    }
}
