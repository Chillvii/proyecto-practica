import { Routes } from '@angular/router';

import { EmployeeListComponent } from './features/employees/pages/employee-list/employee-list.component';
import { EmployeeDetailComponent } from './features/employees/pages/employee-detail/employee-detail.component';

export const routes: Routes = [
  { path: '', component: EmployeeListComponent },
  { path: 'employees/:id', component: EmployeeDetailComponent},
  { path: '**', redirectTo: '' },
];
