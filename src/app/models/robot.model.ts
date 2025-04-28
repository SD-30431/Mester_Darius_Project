import {User} from './user.model';

export interface Robot {
  id: number;
  name: string;
  created_at: Date;
  owner: User;
}
