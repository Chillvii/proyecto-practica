import { Routes } from '@angular/router';

import { EmployeeListComponent } from './features/employees/pages/employee-list/employee-list.component';
import { EmployeeDetailComponent } from './features/employees/pages/employee-detail/employee-detail.component';
import { EmployeeAddComponent } from './features/employees/pages/employee-add/employee-add.component';

export const routes: Routes = [
  { path: '', component: EmployeeListComponent },
  { path: 'employees/new', component: EmployeeAddComponent},
  { path: 'employees/:id', component: EmployeeDetailComponent},
  { path: '**', redirectTo: '' },
];
