import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';  // Import FormsModule
import { CommonModule } from '@angular/common';  // Import CommonModule here

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],  // Use CommonModule here
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';

  constructor(private http: HttpClient, private router: Router) {}

  login(): void {
    localStorage.removeItem('token');
    const body = { username: this.username, password: this.password };
    console.log("Sending request with body:", body);  // Debugging log

    this.http.post<any>('http://localhost:8081/api/auth/login', body)
      .subscribe({
        next: (response) => {
          console.log("Received response:", response);  // Debugging log

          // Store all relevant data in localStorage
          const { token, role, username, id, created_at } = response;

          // Store token, role, and user details (username, id, created_at)
          localStorage.setItem('token', token);
          localStorage.setItem('role', role);
          localStorage.setItem('username', username);
          localStorage.setItem('id', id);
          localStorage.setItem('created_at', created_at);

          // Redirect based on role
          if (role === 'ADMIN') {
            this.router.navigate(['/admin/dashboard']);
          } else if (role === 'USER') {
            this.router.navigate(['/user/dashboard']);
          }
        },
        error: (err) => {
          console.error("Login error:", err);  // Debugging log
          this.errorMessage = 'Invalid credentials';
        }
      });
  }

}
