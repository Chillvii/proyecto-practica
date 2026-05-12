import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';

/**
 * Interceptor mínimo que registra errores HTTP en consola y los re-emite.
 *
 * Para mejorarlo: muestra un toast, redirige a una pantalla de error, intenta
 * refresh de token si recibes 401, etc. De momento es un placeholder didáctico
 * que enseña dónde poner la lógica transversal.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error(
        `[HTTP ${error.status}] ${req.method} ${req.url}`,
        error.error
      );
      return throwError(() => error);
    })
  );
};
