export interface TaskUpdate {
  name: string;
  description: string;
  status: 'PENDING' | 'COMPLETED';
  robot: {
    id: number;
  };
}
