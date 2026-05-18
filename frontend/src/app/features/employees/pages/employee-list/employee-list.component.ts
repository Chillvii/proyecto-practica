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
  readonly employees = signal<Employee[]>([]);
  readonly loading = signal<boolean>(true);
  readonly error = signal<string | null>(null);

  //modificar
  editingId: string | null = null;
  private fb = inject(FormBuilder);
  errorMessage: string | null = null;
  submitted = false;
  saving = signal(false);

  //borrar
  showDeleteModal = false;
  selectedId: string | null = null;

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

    this.saving.set(true);

    const data = this.employeeForm.getRawValue() as EmployeeAdd;
    this.service.update(id, data).subscribe({
      next: () => {
        this.editingId = null;
        this.errorMessage = null;
        this.submitted = false;
        this.saving.set(false);
        this.loadEmployees();
      },

      error: (err) => {
        this.saving.set(false);
        if (err.status === 409) {
          this.errorMessage = 'Ya existe un empleado con ese email.';
        } else {
          this.errorMessage = 'Error al actualizar el empleado.';
        }
      }

    });
  }

  cancelEdit() {
    this.editingId = null;
    this.employeeForm.reset();
    this.submitted = false;
    this.errorMessage = null;
    this.saving.set(false);
  }

  delete(employeeId: string) {
  /*   if (!confirm('¿Seguro que quieres borrarlo?')) {
      return;
    }
    this.service.delete(employeeId).subscribe(() => {
      this.loadEmployees();
    }); */
    
  this.selectedId = employeeId;
  this.showDeleteModal = true;
  }

  confirmDelete() {
  if (!this.selectedId) return;

  this.service.delete(this.selectedId).subscribe({
    next: () => {
      this.loadEmployees();
      this.showDeleteModal = false;
      this.selectedId = null;
    }
  });
  }
}
