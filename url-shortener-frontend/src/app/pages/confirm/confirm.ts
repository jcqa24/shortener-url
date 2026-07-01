import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService } from '../../core/services/auth.service';

type ConfirmStatus = 'loading' | 'success' | 'error';

@Component({
  selector: 'app-confirm',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './confirm.html'
})
export class ConfirmComponent implements OnInit {
  readonly status = signal<ConfirmStatus>('loading');
  readonly message = signal('Confirmando tu cuenta...');

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    const token = this.route.snapshot.queryParamMap.get('token');

    if (!token) {
      this.status.set('error');
      this.message.set('Falta el token de confirmación en el enlace.');
      return;
    }

    this.authService.confirm(token).subscribe({
      next: () => {
        this.status.set('success');
        this.message.set('Tu cuenta fue confirmada. Ya puedes iniciar sesión.');
      },
      error: (err: HttpErrorResponse) => {
        this.status.set('error');
        this.message.set(err.error?.error ?? 'El token es inválido o ya expiró.');
      }
    });
  }
}