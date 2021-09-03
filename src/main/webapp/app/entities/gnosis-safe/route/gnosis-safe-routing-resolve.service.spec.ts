jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IGnosisSafe, GnosisSafe } from '../gnosis-safe.model';
import { GnosisSafeService } from '../service/gnosis-safe.service';

import { GnosisSafeRoutingResolveService } from './gnosis-safe-routing-resolve.service';

describe('Service Tests', () => {
  describe('GnosisSafe routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: GnosisSafeRoutingResolveService;
    let service: GnosisSafeService;
    let resultGnosisSafe: IGnosisSafe | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(GnosisSafeRoutingResolveService);
      service = TestBed.inject(GnosisSafeService);
      resultGnosisSafe = undefined;
    });

    describe('resolve', () => {
      it('should return IGnosisSafe returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultGnosisSafe = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultGnosisSafe).toEqual({ id: 123 });
      });

      it('should return new IGnosisSafe if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultGnosisSafe = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultGnosisSafe).toEqual(new GnosisSafe());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultGnosisSafe = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultGnosisSafe).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
