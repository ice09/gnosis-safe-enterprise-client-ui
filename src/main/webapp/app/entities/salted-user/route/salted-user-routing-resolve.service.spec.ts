jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ISaltedUser, SaltedUser } from '../salted-user.model';
import { SaltedUserService } from '../service/salted-user.service';

import { SaltedUserRoutingResolveService } from './salted-user-routing-resolve.service';

describe('Service Tests', () => {
  describe('SaltedUser routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: SaltedUserRoutingResolveService;
    let service: SaltedUserService;
    let resultSaltedUser: ISaltedUser | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(SaltedUserRoutingResolveService);
      service = TestBed.inject(SaltedUserService);
      resultSaltedUser = undefined;
    });

    describe('resolve', () => {
      it('should return ISaltedUser returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSaltedUser = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSaltedUser).toEqual({ id: 123 });
      });

      it('should return new ISaltedUser if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSaltedUser = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultSaltedUser).toEqual(new SaltedUser());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        spyOn(service, 'find').and.returnValue(of(new HttpResponse({ body: null })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultSaltedUser = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultSaltedUser).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
