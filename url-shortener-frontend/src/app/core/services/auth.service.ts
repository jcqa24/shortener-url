import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user.model';
import { catchError, map, of } from 'rxjs';


interface MessageResponse {
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = `${environment.apiUrl}/api/auth`;

  private readonly _currentUser = signal<User | null>(null);
  readonly currentUser = this._currentUser.asReadonly();
  readonly isAuthenticated = computed(() => this._currentUser() !== null);

  constructor(private http: HttpClient) {}

  register(email: string, password: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/register`, { email, password });
  }

  confirm(token: string): Observable<MessageResponse> {
    return this.http.get<MessageResponse>(`${this.apiUrl}/confirm`, { params: { token } });
  }

  login(email: string, password: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(() => this._currentUser.set({ email }))
    );
  }

  logout(): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.apiUrl}/logout`, {}).pipe(
      tap(() => this._currentUser.set(null))
    );
  }

  checkSession(): Observable<User | null> {
  return this.http.get<{ email: string }>(`${this.apiUrl}/me`).pipe(
    tap(res => this._currentUser.set({ email: res.email })),
    map(res => ({ email: res.email })),
    catchError(() => {
      this._currentUser.set(null);
      return of(null);
    })
  );
    }
}