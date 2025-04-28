export interface User {
  id: number;
  username: string;
  password: string;
  role: string;
  token?: string;
  created_at?: string;
}
