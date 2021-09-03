import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IGnosisSafe } from '../gnosis-safe.model';

@Component({
  selector: 'jhi-gnosis-safe-detail',
  templateUrl: './gnosis-safe-detail.component.html',
})
export class GnosisSafeDetailComponent implements OnInit {
  gnosisSafe: IGnosisSafe | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gnosisSafe }) => {
      this.gnosisSafe = gnosisSafe;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
