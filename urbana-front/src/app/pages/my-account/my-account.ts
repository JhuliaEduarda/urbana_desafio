import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';

import {
  User,
  UserService
} from '../../services/user.service';

@Component({
  selector: 'app-my-account',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './my-account.html',
  styleUrl: './my-account.css'
})
export class MyAccount implements OnInit {

  user: User | null = null;

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  loading = true;
  saving = false;

  error = '';
  success = '';

  constructor(
    private readonly userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadAccount();
  }

  loadAccount(): void {
    this.loading = true;
    this.error = '';

    this.userService.getMyAccount().subscribe({
      next: user => {
        this.user = user;
        this.loading = false;
      },
      error: error => {
        console.error(
          'Erro ao carregar a conta:',
          error
        );

        this.error = 'Não foi possível carregar seus dados.';
        this.loading = false;
      }
    });
  }

  changePassword(): void {
    this.error = '';
    this.success = '';

    if (
      !this.currentPassword.trim()
      || !this.newPassword
      || !this.confirmPassword
    ) {
      this.error = 'Preencha todos os campos de senha.';
      return;
    }

    if (this.newPassword.length < 6) {
      this.error =
        'A nova senha deve ter pelo menos 6 caracteres.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.error =
        'A confirmação não corresponde à nova senha.';
      return;
    }

    if (this.currentPassword === this.newPassword) {
      this.error =
        'A nova senha deve ser diferente da senha atual.';
      return;
    }

    this.saving = true;

    this.userService.changePassword(
      this.currentPassword,
      this.newPassword
    ).subscribe({
      next: () => {
        this.success = 'Senha alterada com sucesso.';

        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';

        this.saving = false;
      },
      error: error => {
        console.error(
          'Erro ao alterar a senha:',
          error
        );

        this.error =
          error.error?.detail
          || error.error?.message
          || 'Não foi possível alterar a senha. Confira a senha atual.';

        this.saving = false;
      }
    });
  }
}