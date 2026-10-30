package com.gitpulse.client;

import com.gitpulse.domain.Records.GitHubBranch;
import com.gitpulse.domain.Records.GitHubPr;
import com.gitpulse.domain.Records.GitHubReview;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "github-api")
@ClientHeaderParam(name = "Authorization", value = "Bearer ${github.token}")
@ClientHeaderParam(name = "Accept", value = "application/vnd.github.v3+json")
@ClientHeaderParam(name = "User-Agent", value = "GitPulse-API")
public interface GitHubRestClient {

    @GET
    @Path("/repos/{owner}/{repo}/pulls")
    List<GitHubPr> getPullRequests(
        @PathParam("owner") String owner, 
        @PathParam("repo") String repo, 
        @QueryParam("state") String state
    );

    @GET
    @Path("/repos/{owner}/{repo}/pulls/{pull_number}/reviews")
    List<GitHubReview> getPullRequestReviews(
        @PathParam("owner") String owner, 
        @PathParam("repo") String repo, 
        @PathParam("pull_number") int pullNumber
    );

    @GET
    @Path("/repos/{owner}/{repo}/branches")
    List<GitHubBranch> getBranches(
        @PathParam("owner") String owner, 
        @PathParam("repo") String repo
    );

    @GET
    @Path("/repos/{owner}/{repo}/branches/{branch}")
    GitHubBranch getBranch(
        @PathParam("owner") String owner, 
        @PathParam("repo") String repo,
        @PathParam("branch") String branch
    );
}
