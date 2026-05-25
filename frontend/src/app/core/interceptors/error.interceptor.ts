import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError, throwError } from 'rxjs';
import { inject } from '@angular/core';
import { ToastService } from '../toast/toast.service';

/**
 * Interceptor mínimo que registra errores HTTP en consola y los re-emite.
 *
 * Para mejorarlo: muestra un toast, redirige a una pantalla de error, intenta
 * refresh de token si recibes 401, etc. De momento es un placeholder didáctico
 * que enseña dónde poner la lógica transversal.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toast = inject(ToastService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      /* console.error(
        `[HTTP ${error.status}] ${req.method} ${req.url}`,
        error.error
      ); */
      const msg = error.error?.message ?? `Error ${error.status}`;
      toast.show(msg, 'error');
      return throwError(() => error);
    })
  );
};