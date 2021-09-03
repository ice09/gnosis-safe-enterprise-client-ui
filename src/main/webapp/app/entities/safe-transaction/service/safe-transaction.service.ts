import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISafeTransaction, getSafeTransactionIdentifier } from '../safe-transaction.model';

export type EntityResponseType = HttpResponse<ISafeTransaction>;
export type EntityArrayResponseType = HttpResponse<ISafeTransaction[]>;

@Injectable({ providedIn: 'root' })
export class SafeTransactionService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/safe-transactions');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(safeTransaction: ISafeTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(safeTransaction);
    return this.http
      .post<ISafeTransaction>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(safeTransaction: ISafeTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(safeTransaction);
    return this.http
      .put<ISafeTransaction>(`${this.resourceUrl}/${getSafeTransactionIdentifier(safeTransaction) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(safeTransaction: ISafeTransaction): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(safeTransaction);
    return this.http
      .patch<ISafeTransaction>(`${this.resourceUrl}/${getSafeTransactionIdentifier(safeTransaction) as number}`, copy, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ISafeTransaction>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISafeTransaction[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  submit(id: number): Observable<HttpResponse<{}>> {
    return this.http.get(`${this.resourceUrl}/submit/${id}`, { observe: 'response' });
  }

  addSafeTransactionToCollectionIfMissing(
    safeTransactionCollection: ISafeTransaction[],
    ...safeTransactionsToCheck: (ISafeTransaction | null | undefined)[]
  ): ISafeTransaction[] {
    const safeTransactions: ISafeTransaction[] = safeTransactionsToCheck.filter(isPresent);
    if (safeTransactions.length > 0) {
      const safeTransactionCollectionIdentifiers = safeTransactionCollection.map(
        safeTransactionItem => getSafeTransactionIdentifier(safeTransactionItem)!
      );
      const safeTransactionsToAdd = safeTransactions.filter(safeTransactionItem => {
        const safeTransactionIdentifier = getSafeTransactionIdentifier(safeTransactionItem);
        if (safeTransactionIdentifier == null || safeTransactionCollectionIdentifiers.includes(safeTransactionIdentifier)) {
          return false;
        }
        safeTransactionCollectionIdentifiers.push(safeTransactionIdentifier);
        return true;
      });
      return [...safeTransactionsToAdd, ...safeTransactionCollection];
    }
    return safeTransactionCollection;
  }

  protected convertDateFromClient(safeTransaction: ISafeTransaction): ISafeTransaction {
    return Object.assign({}, safeTransaction, {
      created: safeTransaction.created?.isValid() ? safeTransaction.created.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.created = res.body.created ? dayjs(res.body.created) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((safeTransaction: ISafeTransaction) => {
        safeTransaction.created = safeTransaction.created ? dayjs(safeTransaction.created) : undefined;
      });
    }
    return res;
  }
}
