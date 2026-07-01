import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { applyServerErrors } from '../../core/utils/http-error.util';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html'
})
export class RegisterComponent {

 readonly form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService
  ) {
    this.form = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  readonly submitting = signal(false);
  readonly registered = signal(false);


  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    const { email, password } = this.form.getRawValue();

    this.authService.register(email!, password!).subscribe({
      next: () => {
        this.submitting.set(false);
        this.registered.set(true);
        this.toastService.show('Cuenta creada, revisa tu correo', 'success');
      },
      error: (err: HttpErrorResponse) => {
        this.submitting.set(false);
        const message = applyServerErrors(this.form, err);
        this.toastService.show(message, 'error');
      }
    });
  }
}