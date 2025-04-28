import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { WebSocketService } from '../services/web-socket.service';
import {DatePipe, NgForOf} from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { DomSanitizer } from '@angular/platform-browser';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  imports: [
    NgForOf
  ],
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  currentUsername: string = '';
  userId: number = 0;
  loginMessages: any[] = [];

  constructor(
    private router: Router,
    private wsService: WebSocketService,
    private http: HttpClient,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const username = localStorage.getItem('username');
    const id = localStorage.getItem('id');

    if (username && id) {
      this.currentUsername = username;
      this.userId = parseInt(id, 10);
    }

    this.wsService.loginActivity$.subscribe(messages => {
      this.loginMessages = messages;
    });
  }

  goToManageUsers(): void {
    this.router.navigate(['/manage-users']);
  }

  goToManageRobots(): void {
    this.router.navigate(['/manage-robots', this.userId]);
  }

  goToManageTasks(): void {
    this.router.navigate(['/manage-tasks', this.userId]);
  }

  exportXml(): void {
    const token = localStorage.getItem('token');

    this.http.get('http://localhost:8081/api/admin/export/xml', {
      headers: {
        Authorization: `Bearer ${token}`
      },
      responseType: 'blob'
    }).subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = 'data.xml';
      link.click();
      window.URL.revokeObjectURL(url);
    });
  }

}
