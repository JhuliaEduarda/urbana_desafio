import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import {
  CardRequestItem,
  CardRequestService,
  CardRequestStatus
} from '../../services/card-request.service';

@Component({
  selector: 'app-requests',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './requests.html',
  styleUrl: './requests.css'
})
export class Requests implements OnInit {
  requests: CardRequestItem[] = [];
  loading = false;
  processingId: number | null = null;
  message = '';
  errorMessage = '';

  constructor(
    private readonly requestService: CardRequestService,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  loadRequests(): void {
    this.loading = true;
    this.errorMessage = '';

    this.requestService.findAll().subscribe({
      next: requests => {
        this.requests = requests;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: error => {
        this.loading = false;
        this.errorMessage =
          error.status === 403
            ? 'Você não tem permissão para consultar solicitações.'
            : 'Não foi possível carregar as solicitações. Verifique se o backend está ativo.';

        console.error('Erro ao carregar solicitações:', error);
        this.cdr.detectChanges();
      }
    });
  }

  decide(request: CardRequestItem, approved: boolean): void {
    if (request.status !== 'PENDING' || this.processingId !== null) {
      return;
    }

    const action = approved ? 'aprovar' : 'rejeitar';

    if (!window.confirm(`Deseja realmente ${action} esta solicitação?`)) {
      return;
    }

    this.processingId = request.id;
    this.message = '';
    this.errorMessage = '';

    this.requestService.decide(request.id, approved).subscribe({
      next: updatedRequest => {
        this.requests = this.requests.map(item =>
          item.id === updatedRequest.id ? updatedRequest : item
        );

        this.processingId = null;
        this.message = approved
          ? 'Solicitação aprovada. O cartão foi criado e está ativo.'
          : 'Solicitação rejeitada. O solicitante verá o status atualizado.';

        this.cdr.detectChanges();
      },
      error: error => {
        this.processingId = null;

        if (error.status === 409) {
          this.errorMessage =
            'Esta solicitação já foi decidida. Atualize a lista.';
          this.loadRequests();
        } else {
          this.errorMessage =
            error.status === 403
              ? 'Somente administradores podem decidir solicitações.'
              : 'Não foi possível registrar a decisão. Tente novamente.';

          console.error('Erro ao decidir solicitação:', error);
        }

        this.cdr.detectChanges();
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

  cardTypeLabel(type: string): string {
    const labels: Record<string, string> = {
      COMMON: 'Comum',
      STUDENT: 'Estudante',
      WORKER: 'Trabalhador'
    };

    return labels[type] ?? type;
  }

  formatDate(value: string): string {
    return new Date(value).toLocaleString('pt-BR');
  }
}
