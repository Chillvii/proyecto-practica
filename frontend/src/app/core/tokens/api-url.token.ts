import { InjectionToken } from '@angular/core';

/**
 * URL base del backend para que los servicios no la hardcodeen.
 *
 * Se provee en `app.config.ts`. En tests se sobrescribe con un valor de prueba
 * para verificar exactamente qué URL piden los servicios.
 */
export const API_URL = new InjectionToken<string>('API_URL');
