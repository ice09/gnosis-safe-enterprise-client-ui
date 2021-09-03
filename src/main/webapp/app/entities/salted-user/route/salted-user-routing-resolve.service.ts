import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISaltedUser, SaltedUser } from '../salted-user.model';
import { SaltedUserService } from '../service/salted-user.service';

@Injectable({ providedIn: 'root' })
export class SaltedUserRoutingResolveService implements Resolve<ISaltedUser> {
  constructor(protected service: SaltedUserService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISaltedUser> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((saltedUser: HttpResponse<SaltedUser>) => {
          if (saltedUser.body) {
            return of(saltedUser.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SaltedUser());
  }
}
