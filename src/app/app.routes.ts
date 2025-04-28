import { Routes } from '@angular/router';
import { UserDashboardComponent } from './user-dashboard/user-dashboard.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './login/auth.guard';
import {ManageRobotsComponent} from "./components/manage-robots/manage-robots.component";
import {ManageTasksComponent} from "./components/manage-tasks/manage-tasks.component";
import {ManageUsersComponent} from './components/manage-users/manage-users.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'user/dashboard', component: UserDashboardComponent, canActivate: [AuthGuard] },
  { path: 'manage-robots/:userId', component: ManageRobotsComponent },
  { path: 'manage-tasks/:userId', component: ManageTasksComponent },
  { path: 'manage-users', component: ManageUsersComponent },
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'upload', component: LoginComponent },
  { path: '**', redirectTo: '/login' } // catch-all fallback
];
