import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';  // Make sure CommonModule is imported

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet],  // Import RouterOutlet for routing
  template: `<router-outlet></router-outlet>`  // This is where the routed components will display
})
export class AppComponent {}
