/**
 * Contrato del empleado tal como lo expone la API.
 *
 * `hiredAt` viaja como string ISO `YYYY-MM-DD` porque el backend lo serializa así
 * desde `LocalDate`. Si necesitas un `Date` en el front, parsea sólo donde lo uses.
 */
export interface Employee {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  position: string;
  hiredAt: string;
}

export interface EmployeeAdd {
  firstName: string;
  lastName: string;
  email: string;
  position: string;
  hiredAt: string;
}

//dtos filtros:
export interface EmployeeFilters {
  firstName?: string;
  lastName?: string;
  position?: string;
  from?: string;
  to?: string;
}

export interface PageResponse {
  content: Employee[];
  number: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

