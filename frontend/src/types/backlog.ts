export type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';

export interface Task {
  id: string;
  title: string;
  description: string;
  priority: Priority;
}

export interface UserStory {
  id: string;
  title: string;
  description: string;
  priority: Priority;
  storyPoints: number;
  acceptanceCriteria: string[];
  tasks: Task[];
}

export interface Epic {
  id: string;
  title: string;
  description: string;
  userStories: UserStory[];
}

export interface Sprint {
  id: string;
  name: string;
  goal: string;
  userStoryIds: string[];
}

export interface ProductBacklog {
  projectName: string;
  summary: string;
  suggestedTechnologies: string[];
  epics: Epic[];
  sprints: Sprint[];
}

export interface GenerateBacklogRequest {
  projectName: string;
  sprintCount: number;
  sprintDurationWeeks: number;
  teamSize: number;
  technologies: string[];
  suggestTechnologies: boolean;
  additionalText?: string;
}
