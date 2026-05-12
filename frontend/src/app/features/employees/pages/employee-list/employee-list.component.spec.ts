import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';

import { Employee } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { EmployeeListComponent } from './employee-list.component';

describe('EmployeeListComponent', () => {
  let fixture: ComponentFixture<EmployeeListComponent>;

  const mockEmployees: Employee[] = [
    {
      id: '1',
      firstName: 'Ana',
      lastName: 'García',
      email: 'ana@example.com',
      position: 'Backend Developer',
      hiredAt: '2022-03-14',
    },
    {
      id: '2',
      firstName: 'Bruno',
      lastName: 'López',
      email: 'bruno@example.com',
      position: 'Frontend Developer',
      hiredAt: '2021-07-01',
    },
  ];

  function setup(serviceStub: Partial<EmployeesService>) {
    TestBed.configureTestingModule({
      imports: [EmployeeListComponent],
      providers: [{ provide: EmployeesService, useValue: serviceStub }],
    });
    fixture = TestBed.createComponent(EmployeeListComponent);
    fixture.detectChanges();
  }

  it('renderiza una fila por empleado devuelto por el servicio', () => {
    setup({ getAll: () => of(mockEmployees) });

    const rows = fixture.debugElement.queryAll(By.css('tbody tr'));
    expect(rows.length).toBe(2);

    const firstRowText = rows[0].nativeElement.textContent as string;
    expect(firstRowText).toContain('Ana');
    expect(firstRowText).toContain('García');
    expect(firstRowText).toContain('ana@example.com');
  });

  it('muestra mensaje de error cuando el servicio falla', () => {
    setup({ getAll: () => throwError(() => new Error('boom')) });

    const status = fixture.debugElement.query(By.css('.employee-list__status--error'));
    expect(status).toBeTruthy();
    expect((status.nativeElement.textContent as string).trim().length).toBeGreaterThan(0);
  });
});
