import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { API_URL } from '../../../core/tokens/api-url.token';
import { Employee , EmployeeAdd, EmployeeFilters, PageResponse } from '../models/employee.model';

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

  getById(id: string): Observable<Employee> {
    return this.http.get<Employee>(`${this.apiUrl}/employees/${id}`)
  }

  create(employee: EmployeeAdd){
    return this.http.post<EmployeeAdd>(`${this.apiUrl}/employees/new`,employee);
  }

  update(id: string , employee: EmployeeAdd){
    return this.http.put<Employee>(`${this.apiUrl}/employees/${id}`,employee);
  }

  delete(id: string){
    return this.http.delete(`${this.apiUrl}/employees/${id}`);
  }

  getFiltered(filters: EmployeeFilters, page: number, size: number): Observable<PageResponse> {
    const params: any = { page, size };
    if (filters.firstName) params['firstName'] = filters.firstName;
    if (filters.position)  params['position']  = filters.position;
    if (filters.from)      params['from']      = filters.from;
    if (filters.to)        params['to']        = filters.to;
      
    return this.http.get<PageResponse>(`${this.apiUrl}/employees/filter`, { params });
  }

}
