import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';

function getUserRole(): string | null {
  const userJson = localStorage.getItem('user');

  if (!userJson) {
    return null;
  }

  try {
    const user = JSON.parse(userJson);
    const token = user.token;

    if (!token) {
      return null;
    }

    const payload = token.split('.')[1];
    const base64 = payload
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    const claims = JSON.parse(atob(base64));

    return claims.role ?? null;
  } catch {
    return null;
  }
}

export const authGuard: CanActivateFn = (_route, state) => {
  const router = inject(Router);
  const userJson = localStorage.getItem('user');

  if (!userJson || !getUserRole()) {
    return router.createUrlTree(['/login'], {
      queryParams: { returnUrl: state.url }
    });
  }

  return true;
};

export const adminGuard: CanActivateFn = () => {
  const router = inject(Router);
  const role = getUserRole();

  if (!role) {
    return router.createUrlTree(['/login']);
  }

  if (role !== 'ADMIN') {
    return router.createUrlTree(['/cards']);
  }

  return true;
};
