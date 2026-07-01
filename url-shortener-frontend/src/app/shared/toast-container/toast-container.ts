import { Component } from '@angular/core';
import { ToastService } from '../../core/services/toast.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  template: `
    <div class="fixed top-4 right-4 z-50 flex flex-col gap-2">
      @for (toast of toastService.toasts(); track toast.id) {
        <div
          class="px-4 py-3 rounded-lg shadow-lg text-sm text-white min-w-[260px]"
          [class.bg-red-600]="toast.type === 'error'"
          [class.bg-green-600]="toast.type === 'success'"
          [class.bg-brand]="toast.type === 'info'"
        >
          {{ toast.message }}
        </div>
      }
    </div>
  `
})
export class ToastContainerComponent {
  constructor(public toastService: ToastService) {}
}