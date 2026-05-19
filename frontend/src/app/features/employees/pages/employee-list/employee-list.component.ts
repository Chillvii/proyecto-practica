import { ChangeDetectionStrategy, Component, OnInit, inject, signal, computed, effect } from '@angular/core';

import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Employee, EmployeeAdd, PageResponse, EmployeeFilters } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { debounceTime, distinctUntilChanged, take } from 'rxjs';

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
  private readonly fb = inject(FormBuilder);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  //editar
  editingId: string | null = null;
  errorMessage: string | null = null;
  submitted = false;
  readonly saving = signal(false);

  //borrar
  showDeleteModal = false;
  selectedId: string | null = null;

  //filtros
  readonly firstNameControl = new FormControl('');   // para debounce
  readonly lastNameControl = new FormControl('');
  readonly positionFilter = signal('');
  readonly fromFilter = signal('');
  readonly toFilter = signal('');
  readonly currentPage = signal(0);
  readonly pageSize = 5;

  readonly pageResponse = signal<PageResponse | null>(null);
  readonly employees = computed(() => this.pageResponse()?.content ?? []);
  readonly totalPages = computed(() => this.pageResponse()?.totalPages ?? 0);
  readonly totalElements = computed(() => this.pageResponse()?.totalElements ?? 0);

  readonly employeeForm = this.fb.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    position: ['', Validators.required],
    hiredAt: [new Date().toISOString().substring(0, 10)],
  });

  ngOnInit(): void {
    this.route.queryParams.pipe(take(1)).subscribe(params => {
      if (params['firstName']) this.firstNameControl.setValue(params['firstName']);
      if (params['lastName']) this.lastNameControl.setValue(params['lastName']);
      if (params['position']) this.positionFilter.set(params['position']);
      if (params['from']) this.fromFilter.set(params['from']);
      if (params['to']) this.toFilter.set(params['to']);
      if (params['page']) this.currentPage.set(+params['page']);
      this.loadEmployees();
    });
    // Debounce en el input de nombre
    this.firstNameControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(() => {
      this.currentPage.set(0);
      this.loadEmployees();
    });
    this.lastNameControl.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
    ).subscribe(() => {
      this.currentPage.set(0);
      this.loadEmployees();
    });
  }

  loadEmployees(): void {
    const filters: EmployeeFilters = {
      firstName: this.firstNameControl.value?.trim() || undefined,
      lastName: this.lastNameControl.value?.trim() || undefined,
      position: this.positionFilter() || undefined,
      from: this.fromFilter() || undefined,
      to: this.toFilter() || undefined,
    };

    this.service.getFiltered(filters, this.currentPage(), this.pageSize).subscribe({
      next: (data) => {
        this.pageResponse.set(data);
        this.loading.set(false);
        this.syncUrl();
      },
      error: () => {
        this.error.set('No se pudo cargar la lista de empleados. ¿Está arrancado el backend?');
        this.loading.set(false);
      },
    });
  }

  onPositionChange(value: string): void {
    this.positionFilter.set(value);
    this.currentPage.set(0);
    this.loadEmployees();
  }

  onDateChange(): void {
    this.currentPage.set(0);
    this.loadEmployees();
  }

  prevPage(): void {
    if (this.currentPage() > 0) {
      this.currentPage.update(p => p - 1);
      this.loadEmployees();
    }
  }

  nextPage(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update(p => p + 1);
      this.loadEmployees();
    }
  }

  edit(employee: Employee): void {
    this.editingId = employee.id;
    this.employeeForm.patchValue(employee);
  }

  save(id: string): void {
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
        this.errorMessage = err.status === 409
          ? 'Ya existe un empleado con ese email.'
          : 'Error al actualizar el empleado.';
      },
    });
  }

  cancelEdit(): void {
    this.editingId = null;
    this.employeeForm.reset();
    this.submitted = false;
    this.errorMessage = null;
    this.saving.set(false);
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
        this.loadEmployees();
      },
    });
  }

  private syncUrl(): void {
    this.router.navigate([], {
      queryParams: {
        firstName: this.firstNameControl.value || null,
        lastName: this.lastNameControl.value || null,
        position: this.positionFilter() || null,
        from: this.fromFilter() || null,
        to: this.toFilter() || null,
        page: this.currentPage(),
      },
      queryParamsHandling: 'merge',
    });
  }
}