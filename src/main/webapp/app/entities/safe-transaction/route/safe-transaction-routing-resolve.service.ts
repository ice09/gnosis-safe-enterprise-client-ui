import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISafeTransaction, SafeTransaction } from '../safe-transaction.model';
import { SafeTransactionService } from '../service/safe-transaction.service';

@Injectable({ providedIn: 'root' })
export class SafeTransactionRoutingResolveService implements Resolve<ISafeTransaction> {
  constructor(protected service: SafeTransactionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISafeTransaction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((safeTransaction: HttpResponse<SafeTransaction>) => {
          if (safeTransaction.body) {
            return of(safeTransaction.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SafeTransaction());
  }
}
