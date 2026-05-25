import { Routes } from '@angular/router';

import { EmployeeListComponent } from './features/employees/pages/employee-list/employee-list.component';
import { EmployeeDetailComponent } from './features/employees/pages/employee-detail/employee-detail.component';
import { EmployeeAddComponent } from './features/employees/pages/employee-add/employee-add.component';
import { DepartmentListComponent } from './features/departments/pages/department-manipulate/department-list.component';
import { authGuard } from './core/auth/auth.guard';
import { LoginComponent } from './features/login/pages/login.component';

export const routes: Routes = [
  { path: '', component: EmployeeListComponent, canActivate: [authGuard]},
  { path: 'employees/filter', component: EmployeeListComponent, canActivate: [authGuard]},
  { path: 'employees/new', component: EmployeeAddComponent, canActivate: [authGuard]},
  { path: 'employees/:id', component: EmployeeDetailComponent, canActivate: [authGuard]},
  { path: 'departments',component: DepartmentListComponent, canActivate: [authGuard]},
  { path: 'login', component: LoginComponent },
  { path: '**', redirectTo: '' },
];
