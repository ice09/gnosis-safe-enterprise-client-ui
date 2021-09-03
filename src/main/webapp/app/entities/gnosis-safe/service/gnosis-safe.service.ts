import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGnosisSafe, getGnosisSafeIdentifier } from '../gnosis-safe.model';

export type EntityResponseType = HttpResponse<IGnosisSafe>;
export type EntityArrayResponseType = HttpResponse<IGnosisSafe[]>;

@Injectable({ providedIn: 'root' })
export class GnosisSafeService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/gnosis-safes');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(gnosisSafe: IGnosisSafe): Observable<EntityResponseType> {
    return this.http.post<IGnosisSafe>(this.resourceUrl, gnosisSafe, { observe: 'response' });
  }

  update(gnosisSafe: IGnosisSafe): Observable<EntityResponseType> {
    return this.http.put<IGnosisSafe>(`${this.resourceUrl}/${getGnosisSafeIdentifier(gnosisSafe) as number}`, gnosisSafe, {
      observe: 'response',
    });
  }

  partialUpdate(gnosisSafe: IGnosisSafe): Observable<EntityResponseType> {
    return this.http.patch<IGnosisSafe>(`${this.resourceUrl}/${getGnosisSafeIdentifier(gnosisSafe) as number}`, gnosisSafe, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGnosisSafe>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGnosisSafe[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  setup(id: number): Observable<HttpResponse<{}>> {
    return this.http.get(`${this.resourceUrl}/setup/${id}`, { observe: 'response' });
  }

  addGnosisSafeToCollectionIfMissing(
    gnosisSafeCollection: IGnosisSafe[],
    ...gnosisSafesToCheck: (IGnosisSafe | null | undefined)[]
  ): IGnosisSafe[] {
    const gnosisSafes: IGnosisSafe[] = gnosisSafesToCheck.filter(isPresent);
    if (gnosisSafes.length > 0) {
      const gnosisSafeCollectionIdentifiers = gnosisSafeCollection.map(gnosisSafeItem => getGnosisSafeIdentifier(gnosisSafeItem)!);
      const gnosisSafesToAdd = gnosisSafes.filter(gnosisSafeItem => {
        const gnosisSafeIdentifier = getGnosisSafeIdentifier(gnosisSafeItem);
        if (gnosisSafeIdentifier == null || gnosisSafeCollectionIdentifiers.includes(gnosisSafeIdentifier)) {
          return false;
        }
        gnosisSafeCollectionIdentifiers.push(gnosisSafeIdentifier);
        return true;
      });
      return [...gnosisSafesToAdd, ...gnosisSafeCollection];
    }
    return gnosisSafeCollection;
  }
}
