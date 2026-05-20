import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from "@angular/core";
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { Employee, EmployeeAdd } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule, FormsModule, ReactiveFormsModule, DatePipe],
  templateUrl: './employee-detail.component.html',
  styleUrl: './employee-detail.component.scss',
})
export class EmployeeDetailComponent implements OnInit{
  private router = inject(Router);
  route = inject(ActivatedRoute)

  id = this.route.snapshot.paramMap.get('id');

  private readonly service = inject(EmployeesService);

  readonly employee = signal<Employee | null>(null);
  readonly loading = signal<boolean>(true);
  readonly error = signal<string | null>(null);
  readonly notFound = signal<boolean>(false);

  //modificar
  isEditing = false;
  private fb = inject(FormBuilder);
  errorMessage: string | null = null;
  submitted = false;
  saving = signal(false);

  //borrar
  showDeleteModal = false;

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

  loadEmployee() {
    if (!this.id) return;

    this.service.getById(this.id).subscribe({
      next: (data) => {
        this.loading.set(false);
        if (!data) {
          this.notFound.set(true);
        } else {
          this.employee.set(data);
          this.employeeForm.patchValue(data);
        }
      },
      error: () => {
        this.error.set(
          'No se pudo cargar el empleado. ¿Está arrancado el backend?'
        );
        this.loading.set(false);
      }
    });
  }

  ngOnInit(): void {
    this.loadEmployee();
  }

  edit() {
    if (!this.employee()) return;
    this.isEditing = true;
    this.employeeForm.patchValue(this.employee()!);
  }

  save() {
    this.submitted = true;
    if (this.employeeForm.invalid) {
      this.employeeForm.markAllAsTouched();
      return;
    }
    const id = this.employee()?.id;
    if (!id) return;

    this.saving.set(true);

    const data = this.employeeForm.getRawValue() as EmployeeAdd;

    this.service.update(id, data).subscribe({
      next: (updated) => {
        this.employee.set(updated);
        this.errorMessage = null;
        this.submitted = false;
        this.saving.set(false);
        this.isEditing = false;
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
    if (this.employee()) {
      this.employeeForm.patchValue(this.employee()!);
    }
    this.isEditing = false;
    this.submitted = false;
    this.errorMessage = null;
    this.saving.set(false);
  }

  delete() {
    this.showDeleteModal = true;
  }

  confirmDelete() {
    
const id = this.employee()?.id;
  if (!id) return;

  this.service.delete(id).subscribe({
    next: () => {
      this.showDeleteModal = false;
      this.router.navigate(['/employees']);
    }
  });
  }
}
