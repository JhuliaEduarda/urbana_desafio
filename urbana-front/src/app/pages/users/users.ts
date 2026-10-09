import { ChangeDetectorRef, Component, OnInit, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';

export type UserFilter = 'all' | 'active' | 'inactive';

export interface User {
  id: number;
  name: string;
  email: string;
  status?: string;
}

export interface UserRequest {
  name: string;
  email: string;
  password?: string;
  status?: string;
}

@Component({
  selector: 'app-users',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class Users implements OnInit {

  users: User[] = [];
  filteredUsers: User[] = [];

  searchTerm = '';
  selectedFilter: UserFilter = 'all';

  isModalOpen = false;
  editingUserId: number | null = null;

  newUser: UserRequest = this.createEmptyUser();

  constructor(
    private readonly userService: UserService,
    private readonly cdr: ChangeDetectorRef,
    private readonly ngZone: NgZone
  ) { }

  ngOnInit(): void {
    this.loadUsers();
  }

  // =========================
  // USUÁRIOS
  // =========================

  loadUsers(): void {
    this.userService.findAll().subscribe({
      next: (users) => {
        this.users = users.map(user => ({
          ...user,
          status: this.normalizeStatus(user.status)
        }));

        this.applyFilter();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erro ao carregar usuários:', error);
      }
    });
  }

  createUser(): void {
    this.userService.create(this.newUser as any).subscribe({
      next: (createdUser) => {
        const user: User = {
          ...createdUser,
          status: this.normalizeStatus(createdUser.status || this.newUser.status)
        };

        this.users = [...this.users, user];

        this.applyFilter();
        this.closeModal();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erro ao criar usuário:', error);
      }
    });
  }

  editUser(user: User): void {
    this.editingUserId = user.id;

    this.newUser = {
      name: user.name,
      email: user.email,
      password: '',
      status: this.normalizeStatus(user.status)
    };

    this.openModal();
    this.cdr.detectChanges();
  }

  updateUser(): void {
  if (this.editingUserId === null) return;

  this.userService.update(this.editingUserId, this.newUser as any).subscribe({
    next: (updatedUser) => {
      this.ngZone.run(() => {
        this.users = this.users.map(user =>
          user.id === updatedUser.id
            ? { ...user, ...updatedUser }
            : user
        );

        this.applyFilter();
        this.closeModal();
        this.cdr.detectChanges();
      });
    },
    error: (error) => {
      console.error('Erro ao atualizar usuário:', error);
    }
  });
}

  toggleUserStatus(user: User): void {
    console.log('Usuário clicado:', user.id, user.name);

    this.userService.toggleStatus(user.id).subscribe({
      next: (updatedUser) => {
        console.log('Resposta do backend:', updatedUser);

        this.users = this.users.map(currentUser =>
          currentUser.id === updatedUser.id
            ? {
              ...currentUser,
              ...updatedUser,
              status: this.normalizeStatus(updatedUser.status || currentUser.status)
            }
            : currentUser
        );

        this.applyFilter();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erro ao alterar status do usuário:', error);
      }
    });
  }

  deleteUser(id: number): void {
    const confirmed = confirm(
      'Tem certeza de que deseja excluir este usuário?'
    );

    if (!confirmed) {
      return;
    }

    this.userService.delete(id).subscribe({
      next: () => {
        this.users = this.users.filter(user => user.id !== id);
        this.applyFilter();
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Erro ao excluir usuário:', error);
      }
    });
  }

  // MODAL

  openModal(): void {
    this.isModalOpen = true;
  }

  closeModal(): void {
    this.isModalOpen = false;
    this.resetForm();
  }

  cancelEdit(): void {
    this.closeModal();
  }

  // FILTROS

  setFilter(filter: UserFilter): void {
    this.selectedFilter = filter;
    this.applyFilter();
  }

  applyFilter(): void {
    let result = [...this.users];

    const term = this.searchTerm.trim().toLowerCase();

    if (term) {
      result = result.filter(user =>
        user.name.toLowerCase().includes(term) ||
        user.email.toLowerCase().includes(term)
      );
    }

    if (this.selectedFilter === 'active') {
      result = result.filter(user => this.normalizeStatus(user.status) === 'Ativo');
    } else if (this.selectedFilter === 'inactive') {
      result = result.filter(user => this.normalizeStatus(user.status) === 'Inativo');
    }

    this.filteredUsers = result;
  }

  // CONTADORES

  get totalCount(): number {
    return this.users ? this.users.length : 0;
  }

  get activeCount(): number {
    if (!this.users) return 0;
    return this.users.filter(u => this.normalizeStatus(u.status) === 'Ativo').length;
  }

  get inactiveCount(): number {
    if (!this.users) return 0;
    return this.users.filter(u => this.normalizeStatus(u.status) === 'Inativo').length;
  }

  // UTILITÁRIOS

  getInitials(name: string): string {
    if (!name) {
      return '';
    }

    const parts = name.trim().split(/\s+/);

    if (parts.length === 1) {
      return parts[0][0].toUpperCase();
    }

    return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
  }

  private normalizeStatus(status?: string): 'Ativo' | 'Inativo' {
    if (!status) return 'Ativo';
    return status.trim().toLowerCase() === 'inativo' ? 'Inativo' : 'Ativo';
  }

  private createEmptyUser(): UserRequest {
    return {
      name: '',
      email: '',
      password: '',
      status: 'Ativo'
    };
  }

  private resetForm(): void {
    this.editingUserId = null;
    this.newUser = this.createEmptyUser();
  }
}