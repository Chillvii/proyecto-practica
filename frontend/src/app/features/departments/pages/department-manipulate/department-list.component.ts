import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { DepartmentsService } from '../../services/departments.service';
import { Department } from '../../../../shared/models/department.model';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-department-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [ReactiveFormsModule,RouterModule],
  templateUrl: './department-list.component.html',
  styleUrl: './department-list.component.scss',
})
export class DepartmentListComponent implements OnInit {
  private readonly service = inject(DepartmentsService);
  private readonly fb = inject(FormBuilder);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);
  readonly saving = signal(false);

  departments: Department[] = [];
  editingId: string | null = null;
  errorMessage: string | null = null;
  showDeleteModal = false;
  selectedId: string | null = null;

  // Formulario de alta
  addForm = this.fb.group({
    name: ['', Validators.required],
  });

  // Formulario de edición inline
  editForm = this.fb.group({
    name: ['', Validators.required],
  });

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.service.getAll().subscribe({
      next: (data) => {
        this.departments = data;
        this.loading.set(false);
        this.cdr.markForCheck();
      },
      error: () => {
        this.error.set('No se pudo cargar la lista de departamentos.');
        this.loading.set(false);
        this.cdr.markForCheck();
      },
    });
  }

  create(): void {
    if (this.addForm.invalid) {
      this.addForm.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const name = this.addForm.value.name!;
    this.service.create(name).subscribe({
      next: () => {
        this.addForm.reset();
        this.saving.set(false);
        this.load();
      },
      error: (err) => {
        this.saving.set(false);
        this.errorMessage = err.status === 409
          ? 'Ya existe un departamento con ese nombre.'
          : 'Error al crear el departamento.';
        this.cdr.markForCheck();
      },
    });
  }

  edit(dept: Department): void {
    this.editingId = dept.id;
    this.editForm.patchValue({ name: dept.name });
  }

  save(id: string): void {
    if (this.editForm.invalid) {
      this.editForm.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    const name = this.editForm.value.name!;
    this.service.update(id, name).subscribe({
      next: () => {
        this.editingId = null;
        this.saving.set(false);
        this.load();
      },
      error: (err) => {
        this.saving.set(false);
        this.errorMessage = err.status === 409
          ? 'Ya existe un departamento con ese nombre.'
          : 'Error al actualizar el departamento.';
        this.cdr.markForCheck();
      },
    });
  }

  cancelEdit(): void {
    this.editingId = null;
    this.editForm.reset();
  }

  delete(id: string): void {
    this.selectedId = id;
    this.showDeleteModal = true;
  }

  confirmDelete(): void {
    if (!this.selectedId) return;
    this.service.delete(this.selectedId).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.selectedId = null;
        this.load();
      },
      error: (err) => {
        this.showDeleteModal = false;
        this.errorMessage = err.status === 409
          ? 'No se puede borrar: hay empleados en este departamento.'
          : 'Error al borrar el departamento.';
        this.cdr.markForCheck();
      },
    });
  }
}