import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: 'login', loadComponent: () => import('./pages/login/login').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./pages/register/register').then(m => m.RegisterComponent) },
    { path: 'confirm', loadComponent: () => import('./pages/confirm/confirm').then(m => m.ConfirmComponent) },

  // las demás rutas (landing, confirm, dashboard) las vamos agregando en los pasos siguientes
];