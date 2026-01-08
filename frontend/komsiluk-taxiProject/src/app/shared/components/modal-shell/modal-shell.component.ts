import { Component, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal-shell',
  imports: [CommonModule],
  templateUrl: './modal-shell.component.html',
  styleUrl: './modal-shell.component.css',
})
export class ModalShellComponent {
  @Input() open = false;
  @Output() closed = new EventEmitter<void>();

  close() {
    this.closed.emit();
  }

  onBackdropMouseDown(e: MouseEvent) {
    if ((e.target as HTMLElement).classList.contains('ms-backdrop')) {
      this.close();
    }
  }

  @HostListener('document:keydown.escape')
  onEsc() {
    if (this.open) this.close();
  }
}
