import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface User {
  id: number;
  name: string;
  email: string;
  role?: string;
  status?: string;
}

export interface UserRequest {
  name: string;
  email: string;
  password: string;
}

export interface UserUpdateRequest {
  name: string;
  email: string;
  password?: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private readonly apiUrl = 'http://localhost:8080/users';

  constructor(private http: HttpClient) {}

  findAll(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getMyAccount(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/me`);
  }

  changePassword(
    currentPassword: string,
    newPassword: string
  ): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/me/password`,
      {
        currentPassword,
        newPassword
      }
    );
  }

  create(user: UserRequest): Observable<User> {
    return this.http.post<User>(this.apiUrl, user);
  }

  update(id: number, user: UserUpdateRequest): Observable<User> {
    return this.http.put<User>(
      `${this.apiUrl}/${id}`,
      user
    );
  }

  toggleStatus(id: number): Observable<User> {
    return this.http.patch<User>(
      `${this.apiUrl}/${id}/status`,
      {}
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}
