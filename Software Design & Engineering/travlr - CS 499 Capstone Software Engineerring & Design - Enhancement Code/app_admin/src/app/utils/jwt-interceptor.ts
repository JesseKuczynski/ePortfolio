import { Injectable, Provider } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HTTP_INTERCEPTORS
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../services/authentication';

@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  constructor(
    private authenticationService: AuthenticationService
  ) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    let isAuthAPI: boolean;

    // Prevent intercepting login/register requests
    if (request.url.startsWith('login') || request.url.startsWith('register')) {
      isAuthAPI = true;
    } else {
      isAuthAPI = false;
    }

    if (this.authenticationService.isLoggedIn() && !isAuthAPI) {
      const token = this.authenticationService.getToken();
      const authReq = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      return next.handle(authReq);
    }

    return next.handle(request);
  }
}

// This is the exported provider to be used in app.config.ts
export const authInterceptProvider: Provider = {
  provide: HTTP_INTERCEPTORS,
  useClass: JwtInterceptor,
  multi: true
};
