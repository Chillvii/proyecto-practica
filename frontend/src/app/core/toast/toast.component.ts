import { Component, inject } from '@angular/core';
import { NgFor } from '@angular/common';
import { ToastService } from './toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [NgFor],
  template: `
    <div class="toast-container">
      @for (toast of toastService.toasts(); track toast.id) {
        <div class="toast toast--{{ toast.type }}" (click)="toastService.remove(toast.id)">
          {{ toast.message }}
        </div>
      }
    </div>
  `,
  styles: [`
    .toast-container { position: fixed; bottom: 1rem; right: 1rem; display: flex; flex-direction: column; gap: .5rem; z-index: 9999; }
    .toast { padding: .75rem 1.25rem; border-radius: 6px; color: white; cursor: pointer; min-width: 200px; }
    .toast--success { background: #22c55e; }
    .toast--error   { background: #ef4444; }
    .toast--info    { background: #3b82f6; }
  `]
})
export class ToastComponent {
  readonly toastService = inject(ToastService);
}