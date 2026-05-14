import { ChangeDetectionStrategy, Component, OnInit, inject, signal } from "@angular/core";
import { ActivatedRoute, RouterModule } from '@angular/router';

import { Employee } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';

@Component({
  selector: 'app-employee-detail',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [RouterModule],
  templateUrl: './employee-detail.component.html',
  styleUrl: './employee-detail.component.scss',
})
export class EmployeeDetailComponent {
  route = inject(ActivatedRoute)

  id = this.route.snapshot.paramMap.get('id');

  private readonly service = inject(EmployeesService);

  readonly employee = signal<Employee | null>(null);
  readonly loading = signal<boolean>(true);
  readonly error = signal<string | null>(null);
  readonly notFound = signal<boolean>(false);

  ngOnInit(): void {
    if (!this.id) return;

    this.service.getById(this.id).subscribe({
      next: (data) => {
        this.loading.set(false);

        if (!data) {
          this.notFound.set(true);
        } else {
          this.employee.set(data);
        }
      },
      error: () => {
        this.error.set(
          'No se pudo cargar el empleado. ¿Está arrancado el backend?'
        );
        this.loading.set(false);
      },
    });

  }
}
