import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type CardType =
  | 'COMMON'
  | 'STUDENT'
  | 'WORKER';

export interface Card {
  id: number;
  cardNumber: number;
  cardName: string;
  active: boolean;
  cardType: CardType;
  userId: number;
}

export interface CardRequest {
  cardType: CardType;
}

@Injectable({
  providedIn: 'root'
})
export class CardService {

  private readonly apiUrl = 'http://localhost:8080/cards';

  constructor(private http: HttpClient) {}

  findAll(): Observable<Card[]> {
    return this.http.get<Card[]>(this.apiUrl);
  }

  create(
    userId: number,
    card: CardRequest
  ): Observable<Card> {

    return this.http.post<Card>(
      `${this.apiUrl}/user/${userId}`,
      card
    );
  }

  updateStatus(
    id: number,
    active: boolean
  ): Observable<Card> {

    return this.http.patch<Card>(
      `${this.apiUrl}/${id}/status?active=${active}`,
      {}
    );
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );
  }
}