import {
    ChangeDetectorRef,
    Component,
    OnInit
} from '@angular/core';

import { CommonModule } from '@angular/common';

import { FormsModule } from '@angular/forms';

import {
    Card,
    CardRequest,
    CardService,
    CardType
} from '../../services/card.service';

import {
    User,
    UserService
} from '../../services/user.service';

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

    selectedUserId: number | null = null;

    newCard: CardRequest = {
        cardType: 'COMMON'
    };

    constructor(
        private cardService: CardService,
        private userService: UserService,
        private cdr: ChangeDetectorRef
    ) { }

    ngOnInit(): void {
        this.loadCards();
        this.loadUsers();
    }

    loadCards(): void {
        this.cardService.findAll().subscribe({
            next: (cards) => {
                this.cards = cards;
                this.cdr.detectChanges();
            },

            error: (error) => {
                console.error('Error loading cards:', error);
            }
        });
    }

    loadUsers(): void {
        this.userService.findAll().subscribe({
            next: (users) => {
                this.users = users;
                this.cdr.detectChanges();
            },

            error: (error) => {
                console.error('Error loading users:', error);
            }
        });
    }

    createCard(): void {

        console.log('Clique no botão');
        console.log('selectedUserId:', this.selectedUserId);
        console.log('newCard:', this.newCard);

        if (this.selectedUserId === null) {
            console.log('Usuário não selecionado');
            return;
        }

        this.cardService
            .create(this.selectedUserId, this.newCard)
            .subscribe({
                next: (createdCard) => {

                    console.log('Cartão criado:', createdCard);

                    this.cards = [
                        ...this.cards,
                        createdCard
                    ];

                    this.newCard = {
                        cardType: 'COMMON'
                    };

                    this.selectedUserId = null;

                    this.cdr.detectChanges();
                },

                error: (error) => {
                    console.error('Error creating card:', error);
                }
            });
    }

    toggleCardStatus(card: Card): void {

        const newStatus = !card.active;

        this.cardService
            .updateStatus(card.id, newStatus)
            .subscribe({
                next: (updatedCard) => {

                    this.cards = this.cards.map(currentCard =>
                        currentCard.id === updatedCard.id
                            ? updatedCard
                            : currentCard
                    );

                    this.cdr.detectChanges();
                },

                error: (error) => {
                    console.error('Error updating card status:', error);
                }
            });
    }

    deleteCard(id: number): void {

        this.cardService.delete(id).subscribe({
            next: () => {

                this.cards = this.cards.filter(
                    card => card.id !== id
                );

                this.cdr.detectChanges();
            },

            error: (error) => {
                console.error('Error deleting card:', error);
            }
        });
    }

    getCardTypeLabel(cardType: CardType): string {
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