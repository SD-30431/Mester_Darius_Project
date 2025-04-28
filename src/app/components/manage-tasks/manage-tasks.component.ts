import { Component, OnInit } from '@angular/core';
import { TaskService } from '../../services/task.service';
import { RobotService } from '../../services/robot.service';
import { SpeechService } from '../../services/speech.service';
import { AuthService } from '../../services/auth.service';
import { Task } from '../../models/task.model';
import { TaskUpdate } from '../../models/task-update.model';
import { Robot } from '../../models/robot.model';
import {FormsModule} from '@angular/forms';
import {DatePipe, NgForOf} from '@angular/common';
import {TaskPreviewComponent} from '../../task-preview/task-preview.component';

@Component({
  selector: 'app-manage-tasks',
  templateUrl: './manage-tasks.component.html',
  imports: [
    FormsModule,
    NgForOf,
    DatePipe,
    TaskPreviewComponent
  ],
  styleUrls: ['./manage-tasks.component.css']
})
export class ManageTasksComponent implements OnInit {
  tasks: Task[] = [];
  robots: Robot[] = [];

  description = '';
  selectedRobotId: number | null = null;

  currentUserRole = '';
  currentUserId = 0;

  constructor(
    private taskService: TaskService,
    private robotService: RobotService,
    private speechService: SpeechService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      if (user) {
        this.currentUserRole = user.role;
        this.currentUserId = user.id;
        this.loadTasks();
        this.loadRobots();
      }
    });
  }

  loadTasks(): void {
    if (this.currentUserRole === 'ADMIN') {
      this.taskService.getAllTasks().subscribe(data => this.tasks = data);
    } else {
      this.taskService.getTasksForUserRobots(this.currentUserId).subscribe(data => this.tasks = data);
    }
  }

  loadRobots(): void {
    this.robotService.getRobotsByOwnerId(this.currentUserId).subscribe(data => {
      this.robots = data;
      console.log(this.robots);
    });
  }

  async useMic() {
    try {
      this.description = await this.speechService.startListening();
    } catch (err) {
      alert('Speech recognition failed: ' + err);
    }
  }

  createTask(): void {
    if (!this.description || !this.selectedRobotId) {
      alert('Please provide a description and select a robot.');
      return;
    }

    const newTask: TaskUpdate = {
      name: '',
      description: this.description,
      status: 'PENDING',
      robot: {
        id: +this.selectedRobotId // Ensure it's a number
      }
    };

    this.taskService.createTask(newTask).subscribe({
      next: (response) => {
        alert(response); // This will show "Task created and code generated successfully."
        this.description = '';
        this.selectedRobotId = null;
        this.loadTasks();
      },
      error: (err) => {
        alert('Failed to create task: ' + JSON.stringify(err.error));
        console.error(err);
      }
    });
  }

  addTask(): void {
    const name = prompt('Task name?');
    const description = prompt('Description?');
    const robotId = Number(prompt('Robot ID?'));

    if (!name || !description || !robotId) return;

    const newTask: TaskUpdate = {
      name,
      description,
      status: 'PENDING',
      robot: { id: robotId }
    };

    this.taskService.createTask(newTask).subscribe({
      next: (response) => {
        console.log(response);
        //alert(response); // Will show backend's message
        this.loadTasks();
      },
      error: (err) => {
        alert('Failed to create task: ' + JSON.stringify(err.error));
        console.error(err);
      }
    });
  }

  openCode(task: Task): void {
    this.taskService.openCode(task.id).subscribe({
      next: (result) => {
        console.log('Code opened:', result);
        //alert(result); // Optionally show confirmation
      },
      error: (err) => {
        console.error('Failed to open code', err);
        alert('Failed to open code: ' + err.error);
      }
    });
  }

  editTask(task: Task): void {
    const name = prompt('New name:', task.name);
    const description = prompt('New description:', task.description);
    const status = prompt('Status (PENDING or COMPLETED):', task.status);
    const robotId = Number(prompt('Robot ID:', task.robot.id.toString()));

    if (!name || !description || !status || !robotId) return;

    const updatedTask: TaskUpdate = {
      name,
      description,
      status: status as 'PENDING' | 'COMPLETED',
      robot: { id: robotId }
    };

    this.taskService.updateTask(task.id, updatedTask).subscribe(() => this.loadTasks());
  }

  deleteTask(taskId: number): void {
    if (confirm('Delete this task?')) {
      this.taskService.deleteTask(taskId).subscribe(() => this.loadTasks());
    }
  }
}
