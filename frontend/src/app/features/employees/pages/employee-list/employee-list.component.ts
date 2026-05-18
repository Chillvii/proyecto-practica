import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';

import { RouterModule } from '@angular/router';
import { Employee, EmployeeAdd } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { FormBuilder, FormsModule, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule, FormsModule, ReactiveFormsModule],
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.scss',
})
export class EmployeeListComponent implements OnInit {
  private readonly service = inject(EmployeesService);
  editingId: string | null = null;
  readonly employees = signal<Employee[]>([]);
  readonly loading = signal<boolean>(true);
  readonly error = signal<string | null>(null);
  
  private fb = inject(FormBuilder);

  employeeForm = this.fb.group({
    firstName: [''],
    lastName: [''],
    email: [''],
    position: [''],
    hiredAt: ['']
  });

  loadEmployees() {
    this.service.getAll().subscribe({
      next: (data) => {
        this.employees.set(data);
      }
    });
  }

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (data) => {
        this.loadEmployees();
        this.loading.set(false);
      },
      error: () => {
        this.error.set(
          'No se pudo cargar la lista de empleados. ¿Está arrancado el backend?'
        );
        this.loading.set(false);
      },
    });
  }

  edit(employee: Employee) {
    this.editingId = employee.id;
    this.employeeForm.patchValue(employee);
  }

  save(id: string) {
  const data = this.employeeForm.getRawValue() as EmployeeAdd;
    this.service.update(id, data).subscribe({
      next: () => {
        this.editingId = null;

        this.loadEmployees();
      }
    });
  }

  cancelEdit() {
    this.employeeForm.reset();
    this.editingId = null;
  }
}
