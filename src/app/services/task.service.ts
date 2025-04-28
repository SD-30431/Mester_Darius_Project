import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task } from '../models/task.model';
import { TaskUpdate } from '../models/task-update.model';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private apiUrl = 'http://localhost:8081/api/tasks';

  constructor(private http: HttpClient) {}

  // Helper method to include the JWT Authorization header
  private getAuthHeaders(): HttpHeaders {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('Token not found!');
      return new HttpHeaders(); // fallback to empty headers
    }
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  simulateCode(taskId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/simulate/${taskId}`, {
      headers: this.getAuthHeaders()
    });
  }

  getAllTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl, { headers: this.getAuthHeaders() });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  // Create a task (with auth header added)
  createTask(task: TaskUpdate): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, task, { headers: this.getAuthHeaders() });
  }

  // Generate code from OpenAI (with auth header added)
  generateCode(description: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/generate-code`, { description }, { headers: this.getAuthHeaders() });
  }

  updateTask(id: number, task: TaskUpdate): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task, { headers: this.getAuthHeaders() });
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getAuthHeaders() });
  }

  openCode(id: number): Observable<string> {
    return this.http.get(`${this.apiUrl}/open/${id}`, {
      headers: this.getAuthHeaders(),
      responseType: 'text', // Important if you expect plain text back
    });
  }

  getTasksForUserRobots(userId: number): Observable<Task[]> {
    return this.http.get<Task[]>(`${this.apiUrl}/user/${userId}`, {
      headers: this.getAuthHeaders(),
    });
  }
}
