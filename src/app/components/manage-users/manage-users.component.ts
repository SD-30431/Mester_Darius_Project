import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-manage-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css']
})
export class ManageUsersComponent implements OnInit {
  users: User[] = [];
  selectedUser: User = this.emptyUser();
  isLoading: boolean = false;
  error: string | null = null;  // Add error property

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  emptyUser(): User {
    return { id: 0, username: '', password: '', role: 'USER', created_at: '' };
  }

  loadUsers(): void {
    this.isLoading = true;
    this.userService.getAllUsers().subscribe(
      users => {
        this.users = users;
        this.isLoading = false;
        this.error = null;  // Clear any previous error
      },
      error => {
        this.isLoading = false;
        this.error = 'Failed to load users. Please try again later.';  // Set error message
      }
    );
  }

  editUser(user: User): void {
    this.selectedUser = { ...user, password: '' }; // Don't prefill password
  }

  saveUser(): void {
    this.isLoading = true;
    if (this.selectedUser.id === 0) {
      this.userService.createUser(this.selectedUser).subscribe(
        () => this.finishUpdate(),
        error => {
          this.isLoading = false;
          this.error = 'Failed to save user. Please try again later.';  // Set error message
        }
      );
    } else {
      this.userService.updateUser(this.selectedUser.id, this.selectedUser).subscribe(
        () => this.finishUpdate(),
        error => {
          this.isLoading = false;
          this.error = 'Failed to update user. Please try again later.';  // Set error message
        }
      );
    }
  }

  deleteUser(id: number): void {
    if (confirm('Are you sure you want to delete this user?')) {
      this.isLoading = true;
      this.userService.deleteUser(id).subscribe(
        () => this.loadUsers(),
        error => {
          this.isLoading = false;
          this.error = 'Failed to delete user. Please try again later.';  // Set error message
        }
      );
    }
  }

  resetForm(): void {
    this.selectedUser = this.emptyUser();
  }

  private finishUpdate(): void {
    this.resetForm();
    this.loadUsers();
  }
}
