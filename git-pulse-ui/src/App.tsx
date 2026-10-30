import { useState } from 'react';
import { fetchRepoMetrics, fetchStaleBranches, type RepoMetrics, type BranchHealth } from './api';
import './App.css';

function App() {
  const [owner, setOwner] = useState('quarkusio');
  const [repo, setRepo] = useState('quarkus');
  const [metrics, setMetrics] = useState<RepoMetrics | null>(null);
  const [branchHealth, setBranchHealth] = useState<BranchHealth | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleAnalyze = async () => {
    setLoading(true);
    setError('');
    setMetrics(null);
    setBranchHealth(null);
    try {
      const m = await fetchRepoMetrics(owner, repo);
      const b = await fetchStaleBranches(owner, repo);
      setMetrics(m);
      setBranchHealth(b);
    } catch (err) {
      setError('Failed to fetch data from backend. Make sure Quarkus is running on port 8080 and CORS is enabled.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <header>
        <h1>⚡ GitPulse Dashboard</h1>
        <p>Engineering Intelligence & Repository Health</p>
      </header>

      <div className="controls">
        <input value={owner} onChange={(e) => setOwner(e.target.value)} placeholder="Owner" />
        <input value={repo} onChange={(e) => setRepo(e.target.value)} placeholder="Repo" />
        <button onClick={handleAnalyze} disabled={loading}>
          {loading ? 'Analyzing...' : 'Analyze Repo'}
        </button>
      </div>

      {error && <div className="error">{error}</div>}

      {metrics && (
        <div className="card">
          <h2>🚨 Stuck PR Bottlenecks ({'>'}48h unapproved)</h2>
          <p className="summary">Total Stuck PRs: <strong>{metrics.stuckPrs.length}</strong></p>
          <ul className="pr-list">
            {metrics.stuckPrs.map(pr => (
              <li key={pr.id}>
                <strong>#{pr.number}</strong>: {pr.title} 
                <span className="meta"> (Author: {pr.author}) - {new Date(pr.createdAt).toLocaleString()}</span>
              </li>
            ))}
          </ul>
        </div>
      )}

      {branchHealth && (
        <div className="card">
          <h2>🌱 Branch Hygiene ({'>'}30 days stale)</h2>
          <p className="summary">
            Total Branches: {branchHealth.totalBranches} | Stale Branches: <strong>{branchHealth.staleBranches}</strong>
          </p>
          <table className="branch-table">
            <tbody>
              {branchHealth.branches.map(b => (
                <tr key={b.name}>
                  <td className="branch-name"><code>{b.name}</code></td>
                  <td className="branch-status">
                    {b.isStale ? <span className="tag stale">⚠️ Stale</span> : <span className="tag fresh">✅ Active</span>}
                  </td>
                  <td className="branch-date">Last Commit: {new Date(b.lastCommitDate).toLocaleDateString()}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default App;
