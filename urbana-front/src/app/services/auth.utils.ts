export interface UserSession {
  id: number;
  name: string;
  email: string;
  token: string;
}

export interface TokenClaims {
  sub?: string;
  role?: string;
  exp?: number;
}

export function getCurrentSession(): UserSession | null {
  try {
    const value = localStorage.getItem('user');

    if (!value) {
      return null;
    }

    const session = JSON.parse(value) as UserSession;

    if (
      !session.token ||
      !Number.isFinite(Number(session.id))
    ) {
      return null;
    }

    return session;
  } catch {
    return null;
  }
}

export function getTokenClaims(): TokenClaims | null {
  const session = getCurrentSession();

  if (!session) {
    return null;
  }

  try {
    const payload = session.token.split('.')[1];

    if (!payload) {
      return null;
    }

    const base64 = payload
      .replace(/-/g, '+')
      .replace(/_/g, '/');

    const padded = base64.padEnd(
      base64.length + (4 - base64.length % 4) % 4,
      '='
    );

    const claims = JSON.parse(
      atob(padded)
    ) as TokenClaims;

    if (claims.exp && claims.exp * 1000 <= Date.now()) {
      return null;
    }

    return claims;
  } catch {
    return null;
  }
}

export function isAdmin(): boolean {
  return getTokenClaims()?.role === 'ADMIN';
}
