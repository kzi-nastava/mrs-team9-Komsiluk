import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MESSAGE_REGISTRY, MessageId } from './message-registry';

@Component({
  selector: 'app-message-page',
  imports: [],
  templateUrl: './message-page.component.html',
  styleUrl: './message-page.component.css',
})
export class MessagePageComponent {
  title = '';
  description = '';
  doneUrl = '/';
  buttonText = 'Done';

  constructor(private route: ActivatedRoute, private router: Router) {
    const id = this.route.snapshot.paramMap.get('id') as MessageId | null;

    if (!id || !(id in MESSAGE_REGISTRY)) {
      this.router.navigate(['/']);
      return;
    }

    const msg = MESSAGE_REGISTRY[id];
    this.title = msg.title;
    this.description = msg.description;
    this.doneUrl = msg.doneUrl;
    this.buttonText = msg.buttonText ?? 'Done';
  }

  done() {
    this.router.navigateByUrl(this.doneUrl);
  }
}
