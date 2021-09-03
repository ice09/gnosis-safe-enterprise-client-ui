import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ISafeTransaction, SafeTransaction } from '../safe-transaction.model';

import { SafeTransactionService } from './safe-transaction.service';

describe('Service Tests', () => {
  describe('SafeTransaction Service', () => {
    let service: SafeTransactionService;
    let httpMock: HttpTestingController;
    let elemDefault: ISafeTransaction;
    let expectedResult: ISafeTransaction | ISafeTransaction[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(SafeTransactionService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        comment: 'AAAAAAA',
        token: 'AAAAAAA',
        value: 0,
        receiver: 'AAAAAAA',
        created: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            created: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a SafeTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            created: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.create(new SafeTransaction()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a SafeTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            comment: 'BBBBBB',
            token: 'BBBBBB',
            value: 1,
            receiver: 'BBBBBB',
            created: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a SafeTransaction', () => {
        const patchObject = Object.assign(
          {
            token: 'BBBBBB',
            value: 1,
            created: currentDate.format(DATE_TIME_FORMAT),
          },
          new SafeTransaction()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of SafeTransaction', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            comment: 'BBBBBB',
            token: 'BBBBBB',
            value: 1,
            receiver: 'BBBBBB',
            created: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            created: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a SafeTransaction', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addSafeTransactionToCollectionIfMissing', () => {
        it('should add a SafeTransaction to an empty array', () => {
          const safeTransaction: ISafeTransaction = { id: 123 };
          expectedResult = service.addSafeTransactionToCollectionIfMissing([], safeTransaction);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(safeTransaction);
        });

        it('should not add a SafeTransaction to an array that contains it', () => {
          const safeTransaction: ISafeTransaction = { id: 123 };
          const safeTransactionCollection: ISafeTransaction[] = [
            {
              ...safeTransaction,
            },
            { id: 456 },
          ];
          expectedResult = service.addSafeTransactionToCollectionIfMissing(safeTransactionCollection, safeTransaction);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a SafeTransaction to an array that doesn't contain it", () => {
          const safeTransaction: ISafeTransaction = { id: 123 };
          const safeTransactionCollection: ISafeTransaction[] = [{ id: 456 }];
          expectedResult = service.addSafeTransactionToCollectionIfMissing(safeTransactionCollection, safeTransaction);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(safeTransaction);
        });

        it('should add only unique SafeTransaction to an array', () => {
          const safeTransactionArray: ISafeTransaction[] = [{ id: 123 }, { id: 456 }, { id: 71577 }];
          const safeTransactionCollection: ISafeTransaction[] = [{ id: 123 }];
          expectedResult = service.addSafeTransactionToCollectionIfMissing(safeTransactionCollection, ...safeTransactionArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const safeTransaction: ISafeTransaction = { id: 123 };
          const safeTransaction2: ISafeTransaction = { id: 456 };
          expectedResult = service.addSafeTransactionToCollectionIfMissing([], safeTransaction, safeTransaction2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(safeTransaction);
          expect(expectedResult).toContain(safeTransaction2);
        });

        it('should accept null and undefined values', () => {
          const safeTransaction: ISafeTransaction = { id: 123 };
          expectedResult = service.addSafeTransactionToCollectionIfMissing([], null, safeTransaction, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(safeTransaction);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
