package com.gitpulse.service;

import com.gitpulse.client.GitHubRestClient;
import com.gitpulse.domain.Records.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class MetricsService {

    @Inject
    @RestClient
    GitHubRestClient gitHubClient;

    public RepoMetricsDto calculateMetrics(String owner, String repo) {
        List<GitHubPr> openPrs = gitHubClient.getPullRequests(owner, repo, "open");
        List<StuckPrDto> stuckPrs = new ArrayList<>();
        
        Instant threshold48HoursAgo = Instant.now().minus(48, ChronoUnit.HOURS);

        for (GitHubPr pr : openPrs) {
            Instant createdAt = Instant.parse(pr.createdAt());
            
            if (createdAt.isBefore(threshold48HoursAgo)) {
                List<GitHubReview> reviews = gitHubClient.getPullRequestReviews(owner, repo, pr.number());
                
                boolean hasApproval = reviews.stream()
                    .anyMatch(r -> "APPROVED".equalsIgnoreCase(r.state()));

                if (!hasApproval) {
                    stuckPrs.add(new StuckPrDto(
                        pr.id(), 
                        pr.number(), 
                        pr.title(), 
                        pr.user().login(), 
                        pr.createdAt()
                    ));
                }
            }
        }

        return new RepoMetricsDto(owner, repo, stuckPrs);
    }

    public BranchHealthDto calculateBranchHygiene(String owner, String repo) {
        List<GitHubBranch> branches = gitHubClient.getBranches(owner, repo);
        List<GitHubPr> openPrs = gitHubClient.getPullRequests(owner, repo, "open");
        
        Set<String> activePrBranches = openPrs.stream()
            .map(pr -> pr.head().ref())
            .collect(Collectors.toSet());

        Instant threshold30DaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        List<BranchDto> branchDtos = new ArrayList<>();
        int staleCount = 0;

        for (GitHubBranch branch : branches) {
            GitHubBranch detailedBranch = gitHubClient.getBranch(owner, repo, branch.name());
            String commitDateStr = detailedBranch.commit().innerCommit().committer().date();
            Instant commitDate = Instant.parse(commitDateStr);

            boolean isOld = commitDate.isBefore(threshold30DaysAgo);
            boolean hasOpenPr = activePrBranches.contains(branch.name());
            boolean isStale = isOld && !hasOpenPr;

            if (isStale) {
                staleCount++;
            }

            branchDtos.add(new BranchDto(branch.name(), commitDateStr, isStale));
        }

        return new BranchHealthDto(owner, repo, branches.size(), staleCount, branchDtos);
    }
}
