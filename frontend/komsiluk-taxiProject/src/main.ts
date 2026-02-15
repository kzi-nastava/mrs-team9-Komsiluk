import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));

(globalThis as any).global = globalThis;
(globalThis as any).process = (globalThis as any).process ?? { env: {} };
