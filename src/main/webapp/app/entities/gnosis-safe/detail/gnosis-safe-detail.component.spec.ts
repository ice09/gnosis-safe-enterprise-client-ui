import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GnosisSafeDetailComponent } from './gnosis-safe-detail.component';

describe('Component Tests', () => {
  describe('GnosisSafe Management Detail Component', () => {
    let comp: GnosisSafeDetailComponent;
    let fixture: ComponentFixture<GnosisSafeDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [GnosisSafeDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ gnosisSafe: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(GnosisSafeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(GnosisSafeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load gnosisSafe on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.gnosisSafe).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
