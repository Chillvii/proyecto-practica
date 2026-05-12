import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from '@angular/core';

import { Employee } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';

@Component({
  selector: 'app-employee-list',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './employee-list.component.html',
  styleUrl: './employee-list.component.scss',
})
export class EmployeeListComponent implements OnInit {
  private readonly service = inject(EmployeesService);

  readonly employees = signal<Employee[]>([]);
  readonly loading = signal<boolean>(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    this.service.getAll().subscribe({
      next: (data) => {
        this.employees.set(data);
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
}
