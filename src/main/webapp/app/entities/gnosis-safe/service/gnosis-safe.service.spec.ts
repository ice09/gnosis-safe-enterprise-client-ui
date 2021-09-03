import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGnosisSafe, GnosisSafe } from '../gnosis-safe.model';

import { GnosisSafeService } from './gnosis-safe.service';

describe('Service Tests', () => {
  describe('GnosisSafe Service', () => {
    let service: GnosisSafeService;
    let httpMock: HttpTestingController;
    let elemDefault: IGnosisSafe;
    let expectedResult: IGnosisSafe | IGnosisSafe[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(GnosisSafeService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        address: 'AAAAAAA',
        signatures: 0,
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

      it('should create a GnosisSafe', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new GnosisSafe()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a GnosisSafe', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            address: 'BBBBBB',
            signatures: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a GnosisSafe', () => {
        const patchObject = Object.assign({}, new GnosisSafe());

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of GnosisSafe', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            address: 'BBBBBB',
            signatures: 1,
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

      it('should delete a GnosisSafe', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addGnosisSafeToCollectionIfMissing', () => {
        it('should add a GnosisSafe to an empty array', () => {
          const gnosisSafe: IGnosisSafe = { id: 123 };
          expectedResult = service.addGnosisSafeToCollectionIfMissing([], gnosisSafe);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(gnosisSafe);
        });

        it('should not add a GnosisSafe to an array that contains it', () => {
          const gnosisSafe: IGnosisSafe = { id: 123 };
          const gnosisSafeCollection: IGnosisSafe[] = [
            {
              ...gnosisSafe,
            },
            { id: 456 },
          ];
          expectedResult = service.addGnosisSafeToCollectionIfMissing(gnosisSafeCollection, gnosisSafe);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a GnosisSafe to an array that doesn't contain it", () => {
          const gnosisSafe: IGnosisSafe = { id: 123 };
          const gnosisSafeCollection: IGnosisSafe[] = [{ id: 456 }];
          expectedResult = service.addGnosisSafeToCollectionIfMissing(gnosisSafeCollection, gnosisSafe);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(gnosisSafe);
        });

        it('should add only unique GnosisSafe to an array', () => {
          const gnosisSafeArray: IGnosisSafe[] = [{ id: 123 }, { id: 456 }, { id: 1338 }];
          const gnosisSafeCollection: IGnosisSafe[] = [{ id: 123 }];
          expectedResult = service.addGnosisSafeToCollectionIfMissing(gnosisSafeCollection, ...gnosisSafeArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const gnosisSafe: IGnosisSafe = { id: 123 };
          const gnosisSafe2: IGnosisSafe = { id: 456 };
          expectedResult = service.addGnosisSafeToCollectionIfMissing([], gnosisSafe, gnosisSafe2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(gnosisSafe);
          expect(expectedResult).toContain(gnosisSafe2);
        });

        it('should accept null and undefined values', () => {
          const gnosisSafe: IGnosisSafe = { id: 123 };
          expectedResult = service.addGnosisSafeToCollectionIfMissing([], null, gnosisSafe, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(gnosisSafe);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
