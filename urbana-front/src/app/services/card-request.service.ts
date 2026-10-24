import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { CardType } from './card.service';

export type CardRequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED';

export interface CardRequestItem {
  id: number;
  userId: number;
  userName: string;
  cardType: CardType;
  status: CardRequestStatus;
  createdAt: string;
  decidedAt: string | null;
}

@Injectable({ providedIn: 'root' })
export class CardRequestService {
  private readonly apiUrl = 'http://localhost:8080/card-requests';

  constructor(private http: HttpClient) {}

  create(userId: number, cardType: CardType): Observable<CardRequestItem> {
    return this.http.post<CardRequestItem>(
      `${this.apiUrl}/user/${userId}`,
      { cardType }
    );
  }

  findAll(): Observable<CardRequestItem[]> {
    return this.http.get<CardRequestItem[]>(this.apiUrl);
  }

  findMine(): Observable<CardRequestItem[]> {
    return this.http.get<CardRequestItem[]>(`${this.apiUrl}/mine`);
  }

  decide(
    id: number,
    approved: boolean
  ): Observable<CardRequestItem> {
    return this.http.patch<CardRequestItem>(
      `${this.apiUrl}/${id}/decision?approved=${approved}`,
      {}
    );
  }
}
