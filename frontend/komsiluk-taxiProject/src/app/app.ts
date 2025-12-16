import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './core/layout/navbar/navbar.component';
import { LeftSidebarComponent } from './core/layout/leftsidebar/leftsidebar.component';
import { RightsidebarComponent } from './core/layout/rightsidebar.component/rightsidebar.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, LeftSidebarComponent,RightsidebarComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('komsiluk-taxiProject');
  isLeftSidebarOpen = false;
   rightOpen = false;
  toggleLeftSidebar() {
  this.isLeftSidebarOpen = !this.isLeftSidebarOpen;
}

closeLeftSidebar() {
  this.isLeftSidebarOpen = false;
}
toggleRightSidebar() {
    this.rightOpen = !this.rightOpen;
    if (this.rightOpen) this.isLeftSidebarOpen = false;
  }
closeRightSidebar() {
  this.rightOpen = false;
}
}
