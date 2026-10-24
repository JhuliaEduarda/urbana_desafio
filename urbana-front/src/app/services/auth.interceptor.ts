import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.endsWith('/auth/login')) {
    return next(req);
  }

  const userJson = localStorage.getItem('user');

  if (!userJson) {
    return next(req);
  }

  try {
    const user = JSON.parse(userJson);
    const token = user.token;

    if (!token) {
      return next(req);
    }

    const requestWithToken = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    return next(requestWithToken);
  } catch {
    return next(req);
  }
};
