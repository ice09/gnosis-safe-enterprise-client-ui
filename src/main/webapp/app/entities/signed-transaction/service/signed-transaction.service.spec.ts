import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISignedTransaction, SignedTransaction } from '../signed-transaction.model';

import { SignedTransactionService } from './signed-transaction.service';

describe('Service Tests', () => {
  describe('SignedTransaction Service', () => {
    let service: SignedTransactionService;
    let httpMock: HttpTestingController;
    let elemDefault: ISignedTransaction;
    let expectedResult: ISignedTransaction | ISignedTransaction[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SignedTransactionService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        signedTx: 'AAAAAAA',
        salt: 'AAAAAAA',
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

      it('should create a SignedTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new SignedTransaction()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a SignedTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            signedTx: 'BBBBBB',
            salt: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a SignedTransaction', () => {
        const patchObject = Object.assign({}, new SignedTransaction());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of SignedTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            signedTx: 'BBBBBB',
            salt: 'BBBBBB',
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

      it('should delete a SignedTransaction', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSignedTransactionToCollectionIfMissing', () => {
        it('should add a SignedTransaction to an empty array', () => {
          const signedTransaction: ISignedTransaction = { id: 123 };
          expectedResult = service.addSignedTransactionToCollectionIfMissing([], signedTransaction);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(signedTransaction);
        });

        it('should not add a SignedTransaction to an array that contains it', () => {
          const signedTransaction: ISignedTransaction = { id: 123 };
          const signedTransactionCollection: ISignedTransaction[] = [
            {
              ...signedTransaction,
            },
            { id: 456 },
          ];
          expectedResult = service.addSignedTransactionToCollectionIfMissing(signedTransactionCollection, signedTransaction);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a SignedTransaction to an array that doesn't contain it", () => {
          const signedTransaction: ISignedTransaction = { id: 123 };
          const signedTransactionCollection: ISignedTransaction[] = [{ id: 456 }];
          expectedResult = service.addSignedTransactionToCollectionIfMissing(signedTransactionCollection, signedTransaction);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(signedTransaction);
        });

        it('should add only unique SignedTransaction to an array', () => {
          const signedTransactionArray: ISignedTransaction[] = [{ id: 123 }, { id: 456 }, { id: 50403 }];
          const signedTransactionCollection: ISignedTransaction[] = [{ id: 123 }];
          expectedResult = service.addSignedTransactionToCollectionIfMissing(signedTransactionCollection, ...signedTransactionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const signedTransaction: ISignedTransaction = { id: 123 };
          const signedTransaction2: ISignedTransaction = { id: 456 };
          expectedResult = service.addSignedTransactionToCollectionIfMissing([], signedTransaction, signedTransaction2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(signedTransaction);
          expect(expectedResult).toContain(signedTransaction2);
        });

        it('should accept null and undefined values', () => {
          const signedTransaction: ISignedTransaction = { id: 123 };
          expectedResult = service.addSignedTransactionToCollectionIfMissing([], null, signedTransaction, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(signedTransaction);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
