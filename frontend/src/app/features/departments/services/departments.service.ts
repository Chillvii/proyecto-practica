import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL } from '../../../core/tokens/api-url.token';
import { Department } from '../../../shared/models/department.model';


@Injectable({ providedIn: 'root' })
export class DepartmentsService {
    private readonly http = inject(HttpClient);
    private readonly apiUrl = inject(API_URL);

    getAll(): Observable<Department[]> {
        return this.http.get<Department[]>(`${this.apiUrl}/departments`);
    }

    create(name: string): Observable<Department> {
        return this.http.post<Department>(`${this.apiUrl}/departments`, { name });
    }

    update(id: string, name: string): Observable<Department> {
        return this.http.put<Department>(`${this.apiUrl}/departments/${id}`, name, {
            headers: { 'Content-Type': 'text/plain' }
        });
    }

    delete(id: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/departments/${id}`);
    }
}