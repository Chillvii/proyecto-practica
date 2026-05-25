import { TestBed } from '@angular/core/testing';
import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from '../auth/auth.service';

describe('authInterceptor', () => {
  let http: HttpClient;
  let httpMock: HttpTestingController;
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
      ],
    });
    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
    authService = TestBed.inject(AuthService);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('añade Authorization header cuando hay token', () => {
    authService.setToken('mi-token-jwt');

    http.get('/api/employees').subscribe();

    const req = httpMock.expectOne('/api/employees');
    expect(req.request.headers.get('Authorization')).toBe('Bearer mi-token-jwt');
    req.flush([]);
  });

  it('no añade Authorization header cuando no hay token', () => {
    http.get('/api/employees').subscribe();

    const req = httpMock.expectOne('/api/employees');
    expect(req.request.headers.get('Authorization')).toBeNull();
    req.flush([]);
  });
});