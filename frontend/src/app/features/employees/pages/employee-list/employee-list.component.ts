import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';

import { RouterModule } from '@angular/router';
import { Employee, EmployeeAdd } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';

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
  errorMessage: string | null = null;
  submitted = false;

  getTodayDate(): string {
    return new Date().toISOString().substring(0, 10);
  }

  employeeForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    position: ['', Validators.required],
    hiredAt: [this.getTodayDate()]
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
    this.submitted = true;
    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }
    const data = this.employeeForm.getRawValue() as EmployeeAdd;
    this.service.update(id, data).subscribe({
      next: () => {
        this.editingId = null;
        this.errorMessage = null;
        this.submitted = false;
        this.loadEmployees();
      },

      error: (err) => {
        if (err.status === 409) {
          this.errorMessage = 'Ya existe un empleado con ese email.';
        } else {
          this.errorMessage = 'Error al actualizar el empleado.';
        }
      }

    });
  }

  cancelEdit() {
    this.employeeForm.reset();
    this.editingId = null;
    this.submitted = false;
    this.errorMessage = null;
  }
}
