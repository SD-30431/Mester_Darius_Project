export interface Task {
  id: number;
  name: string;
  description: string;
  status: 'PENDING' | 'COMPLETED';
  robot: {
    id: number;
    name?: string;
  };
  created_at?: string;
}
