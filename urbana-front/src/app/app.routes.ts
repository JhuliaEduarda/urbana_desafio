import { Routes } from '@angular/router';

import { Users } from './pages/users/users';
import { Cards } from './pages/cards/cards';
import { Login } from './pages/login/login';
import { Requests } from './pages/requests/requests';
import { MyAccount } from './pages/my-account/my-account';

import {
  authGuard,
  adminGuard
} from './auth.guard';

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
    component: Users,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'cards',
    component: Cards,
    canActivate: [authGuard]
  },
  {
    path: 'requests',
    component: Requests,
    canActivate: [authGuard, adminGuard]
  },
  {
    path: 'my-account',
    component: MyAccount,
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: 'login'
  }
];