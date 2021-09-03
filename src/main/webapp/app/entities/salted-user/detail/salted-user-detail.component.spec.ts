import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { SaltedUserDetailComponent } from './salted-user-detail.component';

describe('Component Tests', () => {
  describe('SaltedUser Management Detail Component', () => {
    let comp: SaltedUserDetailComponent;
    let fixture: ComponentFixture<SaltedUserDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [SaltedUserDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ saltedUser: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(SaltedUserDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SaltedUserDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load saltedUser on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.saltedUser).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
