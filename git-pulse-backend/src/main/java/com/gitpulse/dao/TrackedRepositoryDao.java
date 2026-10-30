package com.gitpulse.dao;

import io.agroal.api.AgroalDataSource;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;

@ApplicationScoped
public class TrackedRepositoryDao {

    @Inject
    AgroalDataSource dataSource;

    // Automates table creation when the backend starts
    void onStart(@Observes StartupEvent ev) {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS repo_lookups (
                owner VARCHAR(255) NOT NULL,
                repo VARCHAR(255) NOT NULL,
                last_accessed TIMESTAMP NOT NULL,
                PRIMARY KEY (owner, repo)
            )
        """;
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database schema", e);
        }
    }

    public void upsertRepoLookup(String owner, String repo) {
        // H2 compatible upsert syntax
        String sql = "MERGE INTO repo_lookups (owner, repo, last_accessed) KEY(owner, repo) VALUES (?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, owner);
            ps.setString(2, repo);
            ps.setTimestamp(3, Timestamp.from(Instant.now()));
            ps.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save repository lookup tracking", e);
        }
    }
}