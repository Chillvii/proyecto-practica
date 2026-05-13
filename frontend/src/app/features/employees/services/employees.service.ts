import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../../../core/tokens/api-url.token';
import { Employee } from '../models/employee.model';

/**
 * Cliente HTTP del recurso /employees.
 *
 * Plantilla para crecer: cuando añadas POST/PUT/DELETE, suma los métodos aquí
 * en lugar de hacer llamadas HTTP directas desde los componentes.
 */
@Injectable({ providedIn: 'root' })
export class EmployeesService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = inject(API_URL);

  getAll(): Observable<Employee[]> {
    return this.http.get<Employee[]>(`${this.apiUrl}/employees`);
  }

  getById(id: String): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/employees/${id}`)
  }
}
