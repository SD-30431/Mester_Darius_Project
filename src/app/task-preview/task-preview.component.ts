import { Component, Input } from '@angular/core';
import { Task } from '../models/task.model';
import { TaskService } from '../services/task.service';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  imports: [CommonModule],  // Add this 👈
  selector: 'app-task-preview',
  templateUrl: './task-preview.component.html',
  styleUrls: ['./task-preview.component.css']
})
export class TaskPreviewComponent {
  @Input() task!: Task;
  previewSteps: string[] = [];
  isLoading = false;

  constructor(private taskService: TaskService) {}

  simulateTask(): void {
    this.isLoading = true;
    this.taskService.simulateCode(this.task.id).subscribe({
      next: (steps: string[]) => {
        this.previewSteps = steps;
        this.isLoading = false;
      },
      error: (err: any) => {
        console.error('Simulation failed', err);
        this.previewSteps = ['Simulation failed.'];
        this.isLoading = false;
      }
    });
  }
}
