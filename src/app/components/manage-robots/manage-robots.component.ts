// manage-robots.component.ts
import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { RobotService } from '../../services/robot.service';
import {FormsModule} from '@angular/forms';
import {DatePipe, NgForOf} from '@angular/common';
import {Robot} from '../../models/robot.model';
import {AuthService} from '../../services/auth.service';
import { RobotUpdate } from '../../models/robot-update.model';

@Component({
  selector: 'app-manage-robots',
  templateUrl: './manage-robots.component.html',
  imports: [
    FormsModule,
    NgForOf,
    DatePipe
  ],
  styleUrls: ['./manage-robots.component.css']
})
export class ManageRobotsComponent implements OnInit {
  robots: Robot[] = [];
  currentUserRole: string = '';
  currentUsername: string = '';
  currentUserId: number = 0;

  constructor(
    private robotService: RobotService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.authService.currentUser.subscribe(user => {
      if (user) {
        this.currentUserRole = user.role;
        this.currentUsername = user.username;
        this.currentUserId = user.id;
        this.refreshList();
      }
    });
    console.log(this.currentUserRole);
  }


  refreshList(): void {
    if (this.currentUserRole === 'ADMIN') {
      this.robotService.getAllRobots().subscribe(data => this.robots = data);
    } else {
      this.robotService.getRobotsByOwnerId(this.currentUserId).subscribe(data => this.robots = data);
    }
  }

  addRobot(): void {
    const name = prompt("Enter robot name:");
    if (!name) return;

    let observable;

    if (this.currentUserRole === 'ADMIN') {
      const input = prompt("Enter owner (User ID or Username):");
      if (!input) return;

      const id = Number(input);
      observable = isNaN(id)
        ? this.robotService.createRobotAdmin(name, input)     // by username
        : this.robotService.createRobotAdmin(name, id.toString()); // by user ID
    } else {
      observable = this.robotService.createRobot(name, this.currentUserId); // uses current user's ID from backend
    }

    observable.subscribe(() => this.refreshList());
  }


  editRobot(robot: Robot): void {
    let ownerId = robot.owner.id;

    if (this.currentUserRole === 'ADMIN') {
      const newOwnerId = prompt("Enter new User ID:", ownerId.toString());
      if (newOwnerId) {
        ownerId = Number(newOwnerId);
      }
    }

    const newName = prompt("Enter new name:", robot.name);
    if (newName) {
      const updatedRobot: RobotUpdate = {
        name: newName,
        owner: {
          id: ownerId,
          role: robot.owner.role  // or just 'USER' if fixed
        }
      };

      this.robotService.updateRobot(robot.id, updatedRobot).subscribe(() => this.refreshList());
    }
  }


  deleteRobot(robot: Robot): void {
    const confirmDelete = confirm("Are you sure you want to delete this robot?");
    if (confirmDelete) {
      this.robotService.deleteRobot(robot.id).subscribe(() => this.refreshList());
    }
  }
}
