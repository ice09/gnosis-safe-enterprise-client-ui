import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISignedTransaction, getSignedTransactionIdentifier } from '../signed-transaction.model';

export type EntityResponseType = HttpResponse<ISignedTransaction>;
export type EntityArrayResponseType = HttpResponse<ISignedTransaction[]>;

@Injectable({ providedIn: 'root' })
export class SignedTransactionService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/signed-transactions');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(signedTransaction: ISignedTransaction): Observable<EntityResponseType> {
    return this.http.post<ISignedTransaction>(this.resourceUrl, signedTransaction, { observe: 'response' });
  }

  update(signedTransaction: ISignedTransaction): Observable<EntityResponseType> {
    return this.http.put<ISignedTransaction>(
      `${this.resourceUrl}/${getSignedTransactionIdentifier(signedTransaction) as number}`,
      signedTransaction,
      { observe: 'response' }
    );
  }

  partialUpdate(signedTransaction: ISignedTransaction): Observable<EntityResponseType> {
    return this.http.patch<ISignedTransaction>(
      `${this.resourceUrl}/${getSignedTransactionIdentifier(signedTransaction) as number}`,
      signedTransaction,
      { observe: 'response' }
    );
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISignedTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISignedTransaction[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSignedTransactionToCollectionIfMissing(
    signedTransactionCollection: ISignedTransaction[],
    ...signedTransactionsToCheck: (ISignedTransaction | null | undefined)[]
  ): ISignedTransaction[] {
    const signedTransactions: ISignedTransaction[] = signedTransactionsToCheck.filter(isPresent);
    if (signedTransactions.length > 0) {
      const signedTransactionCollectionIdentifiers = signedTransactionCollection.map(
        signedTransactionItem => getSignedTransactionIdentifier(signedTransactionItem)!
      );
      const signedTransactionsToAdd = signedTransactions.filter(signedTransactionItem => {
        const signedTransactionIdentifier = getSignedTransactionIdentifier(signedTransactionItem);
        if (signedTransactionIdentifier == null || signedTransactionCollectionIdentifiers.includes(signedTransactionIdentifier)) {
          return false;
        }
        signedTransactionCollectionIdentifiers.push(signedTransactionIdentifier);
        return true;
      });
      return [...signedTransactionsToAdd, ...signedTransactionCollection];
    }
    return signedTransactionCollection;
  }
}
