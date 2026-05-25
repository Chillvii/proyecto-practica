import { Component, inject, OnInit, ChangeDetectorRef } from "@angular/core";
import { ReactiveFormsModule, Validators, FormBuilder } from "@angular/forms";

import { Employee, EmployeeAdd } from '../../models/employee.model';
import { EmployeesService } from '../../services/employees.service';
import { Router, RouterModule } from "@angular/router";
import { Department } from '../../../../shared/models/department.model';
import { DepartmentsService } from "../../../departments/services/departments.service";
@Component({
    selector: 'app-employee-add',
    standalone: true,
    imports: [ReactiveFormsModule, RouterModule],
    templateUrl: './employee-add.component.html',
    styleUrl: './employee-add.component.scss',
})
export class EmployeeAddComponent implements OnInit {
    private fb = inject(FormBuilder);
    private service = inject(EmployeesService);
    private deptService = inject(DepartmentsService);
    private router = inject(Router);
    private cdr = inject(ChangeDetectorRef); //Carga los departamentos nada mas llegan

    errorMessage: string | null = null;
    departments: Department[] = [];

    ngOnInit() {
        this.deptService.getAll().subscribe(d => {
            this.departments = d;
            this.cdr.markForCheck();
        });
    }

    getTodayDate(): string {
        return new Date().toISOString().substring(0, 10);
    }

    employeeForm = this.fb.group({
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        email: ['', [Validators.required, Validators.email]],
        position: ['', Validators.required],
        departmentName: [null as string | null],
    });

    create() {
        if (this.employeeForm.invalid) {
            this.employeeForm.markAllAsTouched();
            return;
        }

        const data = this.employeeForm.value as EmployeeAdd;

        this.service.create(data).subscribe({
            next: (res: any) => {
                this.router.navigate(['/employees', res.id]);
            },

            error: (err) => {
                if (err.status === 409) {
                    this.errorMessage = 'Ya existe un empleado con ese email.';
                } else {
                    this.errorMessage = 'Error al crear el empleado.';
                }
            }
        })
    }
}