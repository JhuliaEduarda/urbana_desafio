import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {   
  NavigationEnd,
  Router,
  RouterLink,
  RouterLinkActive,
  RouterOutlet
} from '@angular/router';
import { filter, Subscription } from 'rxjs';

import { isAdmin } from './services/auth.utils';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    RouterLink,
    RouterLinkActive
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit, OnDestroy {
  isLoginPage = true;
  isAdmin = false;

  private routerSubscription?: Subscription;

  constructor(private readonly router: Router) { }

  ngOnInit(): void {
    this.updatePageState();

    this.routerSubscription = this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(() => this.updatePageState());
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }


  logout(): void {
    localStorage.removeItem('user');
    this.isAdmin = false;
    this.isLoginPage = true;

    void this.router.navigateByUrl('/login');
  }


  private updatePageState(): void {
    const url = this.router.url.split('?')[0];

    this.isLoginPage =
      url === '/login' || url === '/' || url === '';

    this.isAdmin = isAdmin();
  }
}
