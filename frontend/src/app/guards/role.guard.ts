import { inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const roles: string[] = route.data['roles'];
  if (!roles || roles.includes(auth.getRole()!)) return true;
  const role = auth.getRole();
  if (role === 'ADMIN') router.navigate(['/admin/dashboard']);
  else if (role === 'STAFF') router.navigate(['/staff/dashboard']);
  else router.navigate(['/auth/login']);
  return false;
};
