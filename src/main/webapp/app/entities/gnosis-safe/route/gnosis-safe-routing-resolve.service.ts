import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IGnosisSafe, GnosisSafe } from '../gnosis-safe.model';
import { GnosisSafeService } from '../service/gnosis-safe.service';

@Injectable({ providedIn: 'root' })
export class GnosisSafeRoutingResolveService implements Resolve<IGnosisSafe> {
  constructor(protected service: GnosisSafeService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IGnosisSafe> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((gnosisSafe: HttpResponse<GnosisSafe>) => {
          if (gnosisSafe.body) {
            return of(gnosisSafe.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new GnosisSafe());
  }
}
