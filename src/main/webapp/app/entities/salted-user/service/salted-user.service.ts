import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISaltedUser, getSaltedUserIdentifier } from '../salted-user.model';

export type EntityResponseType = HttpResponse<ISaltedUser>;
export type EntityArrayResponseType = HttpResponse<ISaltedUser[]>;

@Injectable({ providedIn: 'root' })
export class SaltedUserService {
  public resourceUrl = this.applicationConfigService.getEndpointFor('api/salted-users');

  constructor(protected http: HttpClient, private applicationConfigService: ApplicationConfigService) {}

  create(saltedUser: ISaltedUser): Observable<EntityResponseType> {
    return this.http.post<ISaltedUser>(this.resourceUrl, saltedUser, { observe: 'response' });
  }

  update(saltedUser: ISaltedUser): Observable<EntityResponseType> {
    return this.http.put<ISaltedUser>(`${this.resourceUrl}/${getSaltedUserIdentifier(saltedUser) as number}`, saltedUser, {
      observe: 'response',
    });
  }

  partialUpdate(saltedUser: ISaltedUser): Observable<EntityResponseType> {
    return this.http.patch<ISaltedUser>(`${this.resourceUrl}/${getSaltedUserIdentifier(saltedUser) as number}`, saltedUser, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISaltedUser>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISaltedUser[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSaltedUserToCollectionIfMissing(
    saltedUserCollection: ISaltedUser[],
    ...saltedUsersToCheck: (ISaltedUser | null | undefined)[]
  ): ISaltedUser[] {
    const saltedUsers: ISaltedUser[] = saltedUsersToCheck.filter(isPresent);
    if (saltedUsers.length > 0) {
      const saltedUserCollectionIdentifiers = saltedUserCollection.map(saltedUserItem => getSaltedUserIdentifier(saltedUserItem)!);
      const saltedUsersToAdd = saltedUsers.filter(saltedUserItem => {
        const saltedUserIdentifier = getSaltedUserIdentifier(saltedUserItem);
        if (saltedUserIdentifier == null || saltedUserCollectionIdentifiers.includes(saltedUserIdentifier)) {
          return false;
        }
        saltedUserCollectionIdentifiers.push(saltedUserIdentifier);
        return true;
      });
      return [...saltedUsersToAdd, ...saltedUserCollection];
    }
    return saltedUserCollection;
  }
}
