import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';
import {Robot} from '../models/robot.model';
import { RobotUpdate } from '../models/robot-update.model';

@Injectable({
  providedIn: 'root',
})
export class RobotService {
  private apiUrl = `http://localhost:8081/api/robots`;

  constructor(private http: HttpClient) {}

  // Helper method to get the Authorization header
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('Token not found!');
      return new HttpHeaders(); // Return empty headers if token is missing
    }
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  // Get all robots
  getAllRobots(): Observable<Robot[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Robot[]>(this.apiUrl, { headers });
  }

  // Get robots by owner ID
  getRobotsByOwnerId(ownerId: number): Observable<Robot[]> {
    const headers = this.getAuthHeaders();
    return this.http.get<Robot[]>(`${this.apiUrl}/get/${ownerId}`, { headers });
  }

  // Get a single robot by ID
  getRobotById(id: number): Observable<Robot> {
    const headers = this.getAuthHeaders();
    return this.http.get<Robot>(`${this.apiUrl}/${id}`, { headers });
  }

  // Create a new robot
  createRobotAdmin(name: string, ownerId: string): Observable<Robot> {
    const robot = { name, ownerId };
    const headers = this.getAuthHeaders();
    return this.http.post<Robot>(`http://localhost:8081/api/robots/create`, robot, { headers });
  }

  createRobot(name: string, ownerId: number): Observable<Robot> {
    const robot = {
      name,
      owner: {
        role: 'USER',
        id: ownerId
      }
    };

    const headers = this.getAuthHeaders();
    return this.http.post<Robot>(`${this.apiUrl}/create`, robot, { headers });
  }


  // Update an existing robot
  updateRobot(id: number, robot: RobotUpdate): Observable<Robot> {
    const headers = this.getAuthHeaders();
    return this.http.put<Robot>(`${this.apiUrl}/update/${id}`, robot, { headers });
  }

  // Delete a robot by ID
  deleteRobot(id: number): Observable<void> {
    const headers = this.getAuthHeaders();
    return this.http.delete<void>(`${this.apiUrl}/delete/${id}`, { headers });
  }
}
