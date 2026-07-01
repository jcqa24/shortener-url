import { HttpErrorResponse } from '@angular/common/http';
import { FormGroup } from '@angular/forms';

const DEFAULT_MESSAGE = 'Ocurrió un error inesperado. Intenta de nuevo.';

/**
 * Aplica los errores del backend a los controles del FormGroup
 * y devuelve el mensaje general para el toast.
 */
export function applyServerErrors(form: FormGroup, error: HttpErrorResponse): string {
  const body = error.error;

  if (!body || typeof body !== 'object') {
    return DEFAULT_MESSAGE;
  }

  // Caso 1: excepción de negocio del backend -> { error: "mensaje" }
  if (typeof body.error === 'string') {
    if (error.status === 409 && form.contains('email')) {
      form.get('email')?.setErrors({ server: body.error }); // correo ya existe (registro)
    }
    if (error.status === 401 && form.contains('password')) {
      form.get('password')?.setErrors({ server: 'Verifica tu correo y contraseña' }); // login fallido
    }
    return body.error; // sirve también para el 403 (cuenta no confirmada): solo toast
  }

  // Caso 2: errores de validación de @Valid -> { campo: "mensaje", ... }
  let hasFieldErrors = false;
  Object.entries(body).forEach(([field, message]) => {
    if (form.contains(field) && typeof message === 'string') {
      form.get(field)?.setErrors({ server: message });
      hasFieldErrors = true;
    }
  });

  return hasFieldErrors ? 'Revisa los campos marcados' : DEFAULT_MESSAGE;
}