import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  Card,
  CardRequest,
  CardService,
  CardType
} from '../../services/card.service';

import {
  CardRequestItem,
  CardRequestService,
  CardRequestStatus
} from '../../services/card-request.service';

import { User, UserService } from '../../services/user.service';

import {
  getCurrentSession,
  isAdmin as checkIsAdmin
} from '../../services/auth.utils';

@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cards.html',
  styleUrl: './cards.css'
})
export class Cards implements OnInit {
  cards: Card[] = [];
  users: User[] = [];
  requests: CardRequestItem[] = [];

  isAdmin = false;
  selectedUserId: number | null = null;
  loadingCards = false;
  loadingRequests = false;
  submitting = false;
  feedback = '';
  errorMessage = '';

  newCard: CardRequest = {
    cardType: 'COMMON'
  };

  constructor(
    private readonly cardService: CardService,
    private readonly requestService: CardRequestService,
    private readonly userService: UserService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.isAdmin = checkIsAdmin();

    const session = getCurrentSession();

    if (!session) {
      return;
    }

    if (!this.isAdmin) {
      this.selectedUserId = session.id;
      this.loadMyRequests();
    } else {
      this.loadUsers();
    }

    this.loadCards();
  }

  loadCards(): void {
    this.loadingCards = true;

    this.cardService.findAll().subscribe({
      next: cards => {
        this.cards = cards;
        this.loadingCards = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.loadingCards = false;
        this.errorMessage = 'Não foi possível carregar os cartões.';
        console.error('Erro ao carregar cartões:', error);
        this.cdr.detectChanges();
      }
    });
  }

  loadUsers(): void {
    if (!this.isAdmin) {
      return;
    }

    this.userService.findAll().subscribe({
      next: users => {
        this.users = users;
        this.cdr.detectChanges();
      },
      error: error => {
        console.error('Erro ao carregar usuários:', error);
      }
    });
  }

  loadMyRequests(): void {
    if (this.isAdmin) {
      return;
    }

    this.loadingRequests = true;

    this.requestService.findMine().subscribe({
      next: requests => {
        this.requests = requests;
        this.loadingRequests = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.loadingRequests = false;
        this.errorMessage = 'Não foi possível carregar suas solicitações.';
        console.error('Erro ao carregar solicitações:', error);
        this.cdr.detectChanges();
      }
    });
  }

  createCard(): void {
    if (this.submitting) {
      return;
    }

    this.feedback = '';
    this.errorMessage = '';

    if (this.isAdmin) {
      if (this.selectedUserId === null) {
        return;
      }

      this.submitting = true;

      this.cardService.create(this.selectedUserId, this.newCard).subscribe({
        next: card => {
          this.cards = [...this.cards, card];
          this.newCard = { cardType: 'COMMON' };
          this.selectedUserId = null;
          this.feedback = 'Cartão cadastrado com sucesso.';
          this.submitting = false;
          this.cdr.detectChanges();
        },
        error: error => {
          this.submitting = false;
          this.errorMessage = 'Não foi possível cadastrar o cartão.';
          console.error('Erro ao cadastrar cartão:', error);
          this.cdr.detectChanges();
        }
      });

      return;
    }

    const session = getCurrentSession();

    if (!session) {
      this.errorMessage = 'Faça login novamente para solicitar um cartão.';
      return;
    }

    this.submitting = true;

    this.requestService.create(session.id, this.newCard.cardType).subscribe({
      next: request => {
        this.requests = [request, ...this.requests];
        this.newCard = { cardType: 'COMMON' };
        this.feedback = 'Solicitação enviada! Aguarde a análise do administrador.';
        this.submitting = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.submitting = false;
        this.errorMessage = 'Não foi possível enviar sua solicitação.';
        console.error('Erro ao solicitar cartão:', error);
        this.cdr.detectChanges();
      }
    });
  }

  toggleCardStatus(card: Card): void {
    if (!this.isAdmin) {
      return;
    }

    this.cardService.updateStatus(card.id, !card.active).subscribe({
      next: updatedCard => {
        this.cards = this.cards.map(current =>
          current.id === updatedCard.id ? updatedCard : current
        );
        this.cdr.detectChanges();
      },
      error: error => {
        this.errorMessage = 'Não foi possível atualizar o cartão.';
        console.error(error);
      }
    });
  }

  deleteCard(id: number): void {
    if (!this.isAdmin) {
      return;
    }

    this.cardService.delete(id).subscribe({
      next: () => {
        this.cards = this.cards.filter(card => card.id !== id);
        this.cdr.detectChanges();
      },
      error: error => {
        this.errorMessage = 'Não foi possível excluir o cartão.';
        console.error(error);
      }
    });
  }

  statusLabel(status: CardRequestStatus): string {
    const labels: Record<CardRequestStatus, string> = {
      PENDING: 'Pendente',
      APPROVED: 'Aprovada',
      REJECTED: 'Rejeitada'
    };

    return labels[status];
  }

  cardTypeLabel(cardType: CardType): string {
    const labels: Record<CardType, string> = {
      COMMON: 'Comum',
      STUDENT: 'Estudante',
      WORKER: 'Trabalhador'
    };

    return labels[cardType];
  }

  getCardClass(cardType: CardType): string {
    const classes: Record<CardType, string> = {
      COMMON: 'card-common',
      STUDENT: 'card-student',
      WORKER: 'card-worker'
    };

    return classes[cardType];
  }
}
