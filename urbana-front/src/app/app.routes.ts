import { Routes } from '@angular/router';

import { Users } from './pages/users/users';
import { Cards } from './pages/cards/cards';
import { Login } from './pages/login/login';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: Login
  },
  {
    path: 'users',
    component: Users
  },
  {
    path: 'cards',
    component: Cards
  }
];