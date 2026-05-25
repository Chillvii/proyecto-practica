import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { AuthService } from '../../../core/auth/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {
  private readonly authApi = inject(LoginService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  error: string | null = null;

  form = new FormGroup({
    username: new FormControl('', Validators.required),
    password: new FormControl('', Validators.required),
  });

  login() {
    if (this.form.invalid) return;
    this.error = null;

    this.authApi.login(this.form.value.username!, this.form.value.password!)
      .subscribe({
        next: ({ token }) => {
          this.auth.setToken(token);
          this.router.navigate(['/']);
        },
        error: () => this.error = 'Usuario o contraseña incorrectos'
      });
  }
}