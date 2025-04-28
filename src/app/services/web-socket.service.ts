import { Injectable, NgZone } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private socket!: WebSocket;
  private loginActivitySubject = new BehaviorSubject<string[]>([]);
  loginActivity$ = this.loginActivitySubject.asObservable();

  private messages: string[] = [];

  constructor(private ngZone: NgZone) {
    this.connect();
  }

  private connect(): void {
    this.socket = new WebSocket('ws://localhost:8081/ws-login-activity');

    this.socket.onopen = () => {
      console.log('[WebSocket] Connected');
    };

    this.socket.onmessage = (event) => {
      this.ngZone.run(() => {
        const msg = event.data; // plain string
        this.messages.unshift(msg);
        this.loginActivitySubject.next([...this.messages]);
      });
    };

    this.socket.onerror = (error) => {
      console.error('[WebSocket] Error:', error);
    };

    this.socket.onclose = () => {
      console.warn('[WebSocket] Disconnected. Reconnecting in 5s...');
      setTimeout(() => this.connect(), 5000);
    };
  }
}
