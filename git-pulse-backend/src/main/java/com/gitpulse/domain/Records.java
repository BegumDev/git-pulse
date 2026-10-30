package com.gitpulse.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Records {
    // API Responses
    public record RepoMetricsDto(String owner, String repo, List<StuckPrDto> stuckPrs) {}
    public record StuckPrDto(Long id, int number, String title, String author, String createdAt) {}

    // Stale Branch Response
    public record BranchHealthDto(String owner, String repo, int totalBranches, int staleBranches, List<BranchDto> branches) {}
    public record BranchDto(String name, String lastCommitDate, boolean isStale) {}

    // GitHub API Payloads
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubPr(
        Long id, 
        int number, 
        String title, 
        @JsonProperty("created_at") String createdAt, 
        @JsonProperty("head") GitHubHead head,
        GitHubUser user
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubHead(String ref) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubUser(String login) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubReview(String state) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubBranch(String name, GitHubCommitObject commit) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubCommitObject(@JsonProperty("commit") GitHubCommitDetailsContainer innerCommit) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubCommitDetailsContainer(GitHubCommitDetails committer) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GitHubCommitDetails(String date) {}
}
