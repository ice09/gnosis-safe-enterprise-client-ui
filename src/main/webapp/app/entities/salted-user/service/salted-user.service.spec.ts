import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISaltedUser, SaltedUser } from '../salted-user.model';

import { SaltedUserService } from './salted-user.service';

describe('Service Tests', () => {
  describe('SaltedUser Service', () => {
    let service: SaltedUserService;
    let httpMock: HttpTestingController;
    let elemDefault: ISaltedUser;
    let expectedResult: ISaltedUser | ISaltedUser[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SaltedUserService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        salt: 'AAAAAAA',
        address: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a SaltedUser', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new SaltedUser()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a SaltedUser', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            salt: 'BBBBBB',
            address: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a SaltedUser', () => {
        const patchObject = Object.assign(
          {
            salt: 'BBBBBB',
            address: 'BBBBBB',
          },
          new SaltedUser()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of SaltedUser', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            salt: 'BBBBBB',
            address: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a SaltedUser', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSaltedUserToCollectionIfMissing', () => {
        it('should add a SaltedUser to an empty array', () => {
          const saltedUser: ISaltedUser = { id: 123 };
          expectedResult = service.addSaltedUserToCollectionIfMissing([], saltedUser);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(saltedUser);
        });

        it('should not add a SaltedUser to an array that contains it', () => {
          const saltedUser: ISaltedUser = { id: 123 };
          const saltedUserCollection: ISaltedUser[] = [
            {
              ...saltedUser,
            },
            { id: 456 },
          ];
          expectedResult = service.addSaltedUserToCollectionIfMissing(saltedUserCollection, saltedUser);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a SaltedUser to an array that doesn't contain it", () => {
          const saltedUser: ISaltedUser = { id: 123 };
          const saltedUserCollection: ISaltedUser[] = [{ id: 456 }];
          expectedResult = service.addSaltedUserToCollectionIfMissing(saltedUserCollection, saltedUser);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(saltedUser);
        });

        it('should add only unique SaltedUser to an array', () => {
          const saltedUserArray: ISaltedUser[] = [{ id: 123 }, { id: 456 }, { id: 88338 }];
          const saltedUserCollection: ISaltedUser[] = [{ id: 123 }];
          expectedResult = service.addSaltedUserToCollectionIfMissing(saltedUserCollection, ...saltedUserArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const saltedUser: ISaltedUser = { id: 123 };
          const saltedUser2: ISaltedUser = { id: 456 };
          expectedResult = service.addSaltedUserToCollectionIfMissing([], saltedUser, saltedUser2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(saltedUser);
          expect(expectedResult).toContain(saltedUser2);
        });

        it('should accept null and undefined values', () => {
          const saltedUser: ISaltedUser = { id: 123 };
          expectedResult = service.addSaltedUserToCollectionIfMissing([], null, saltedUser, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(saltedUser);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
