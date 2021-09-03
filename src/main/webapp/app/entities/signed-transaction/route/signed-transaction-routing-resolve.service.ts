import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISignedTransaction, SignedTransaction } from '../signed-transaction.model';
import { SignedTransactionService } from '../service/signed-transaction.service';

@Injectable({ providedIn: 'root' })
export class SignedTransactionRoutingResolveService implements Resolve<ISignedTransaction> {
  constructor(protected service: SignedTransactionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISignedTransaction> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((signedTransaction: HttpResponse<SignedTransaction>) => {
          if (signedTransaction.body) {
            return of(signedTransaction.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SignedTransaction());
  }
}
