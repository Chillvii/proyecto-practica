import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { API_URL } from '../../../core/tokens/api-url.token';
import { Employee } from '../models/employee.model';
import { EmployeesService } from './employees.service';

describe('EmployeesService', () => {
  const apiUrl = 'http://test-api';
  let service: EmployeesService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        provideHttpClient(),
        { provide: API_URL, useValue: apiUrl },
      ],
    });
    service = TestBed.inject(EmployeesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('getAll() hace GET a la URL configurada y emite la lista', (done) => {
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

    service.getAll().subscribe((employees) => {
      expect(employees).toEqual(mockEmployees);
      expect(employees.length).toBe(2);
      done();
    });

    const req = httpMock.expectOne(`${apiUrl}/employees`);
    expect(req.request.method).toBe('GET');
    req.flush(mockEmployees);
  });
});
