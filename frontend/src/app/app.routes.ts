import { Routes } from '@angular/router';

import { EmployeeListComponent } from './features/employees/pages/employee-list/employee-list.component';

export const routes: Routes = [
  { path: '', component: EmployeeListComponent },
  { path: '**', redirectTo: '' },
];
