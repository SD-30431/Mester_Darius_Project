import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RobotService } from '../services/robot.service';
import { TaskService } from '../services/task.service';
import { NgForOf, NgIf } from '@angular/common';
import {HttpClient, HttpHeaders} from '@angular/common/http';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  standalone: true,
  imports: [NgIf, NgForOf],
  styleUrls: ['./user-dashboard.component.css']
})
export class UserDashboardComponent implements OnInit {
  currentUsername: string = '';
  userId: number = 0;
  robots: any[] = [];
  tasks: any[] = [];

  // Upload variables
  selectedFile!: File;

  constructor(
    private router: Router,
    private robotService: RobotService,
    private taskService: TaskService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    const username = localStorage.getItem('username');
    const id = localStorage.getItem('id');

    if (username && id) {
      this.currentUsername = username;
      this.userId = parseInt(id, 10);

      this.loadRobots();
      this.loadTasks();
    } else {
      console.error('User information is missing from localStorage');
    }
  }

  loadRobots() {
    this.robotService.getRobotsByOwnerId(this.userId).subscribe({
      next: (data) => this.robots = data,
      error: (error) => console.error('Error loading robots', error),
      complete: () => console.log('Robot loading completed'),
    });
  }

  loadTasks() {
    this.taskService.getTasksForUserRobots(this.userId).subscribe({
      next: (data) => this.tasks = data,
      error: (error) => console.error('Error loading tasks', error),
      complete: () => console.log('Task loading completed'),
    });
  }

  manageRobots() {
    this.router.navigate(['/manage-robots', this.userId]);
  }

  viewTasks() {
    this.router.navigate(['/manage-tasks', this.userId]);
  }

  // --- Upload functions ---
  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  onUpload() {
    if (!this.selectedFile) {
      alert('Please select a file first.');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.http.post('http://localhost:8081/api/upload', formData).subscribe({
      next: (response: any) => {
        console.log('Upload success', response);
        // Assuming the server returns a JSON with `uploadedUrl`
        alert('Upload successful! The file URL is: ' + response.uploadedUrl);
      },
      error: (error) => {
        console.error('Upload failed', error);
        alert('Upload failed.');
      }
    });
  }

}
