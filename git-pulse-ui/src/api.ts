import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api/v1/metrics';

export type StuckPr = {
  id: number;
  number: number;
  title: string;
  author: string;
  createdAt: string;
};

export type Branch = {
  name: string;
  lastCommitDate: string;
  isStale: boolean;
};

export type RepoMetrics = {
  owner: string;
  repo: string;
  stuckPrs: StuckPr[];
};

export type BranchHealth = {
  owner: string;
  repo: string;
  totalBranches: number;
  staleBranches: number;
  branches: Branch[];
};

export const fetchRepoMetrics = async (owner: string, repo: string): Promise<RepoMetrics> => {
  const response = await axios.get(`${API_BASE_URL}/${owner}/${repo}`);
  return response.data;
};

export const fetchStaleBranches = async (owner: string, repo: string): Promise<BranchHealth> => {
  const response = await axios.get(`${API_BASE_URL}/${owner}/${repo}/stale-branches`);
  return response.data;
};
