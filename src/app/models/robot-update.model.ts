export interface RobotUpdate {
  id?: number;
  name: string;
  owner: {
    id: number;
    role: string;
  };
}
