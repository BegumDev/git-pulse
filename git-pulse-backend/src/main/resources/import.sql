CREATE TABLE IF NOT EXISTS repo_lookups (
    owner VARCHAR(255) NOT NULL,
    repo VARCHAR(255) NOT NULL,
    last_accessed TIMESTAMP NOT NULL,
    PRIMARY KEY (owner, repo)
);
