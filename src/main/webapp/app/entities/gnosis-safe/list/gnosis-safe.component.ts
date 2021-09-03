import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IGnosisSafe } from '../gnosis-safe.model';
import { GnosisSafeService } from '../service/gnosis-safe.service';
import { GnosisSafeDeleteDialogComponent } from '../delete/gnosis-safe-delete-dialog.component';
import { GnosisSafeSetupDialogComponent } from '../setup/gnosis-safe-setup-dialog.component';

@Component({
  selector: 'jhi-gnosis-safe',
  templateUrl: './gnosis-safe.component.html',
})
export class GnosisSafeComponent implements OnInit {
  gnosisSafes?: IGnosisSafe[];
  isLoading = false;

  constructor(protected gnosisSafeService: GnosisSafeService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.gnosisSafeService.query().subscribe(
      (res: HttpResponse<IGnosisSafe[]>) => {
        this.isLoading = false;
        this.gnosisSafes = res.body ?? [];
      },
      () => {
        this.isLoading = false;
      }
    );
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IGnosisSafe): number {
    return item.id!;
  }

  delete(gnosisSafe: IGnosisSafe): void {
    const modalRef = this.modalService.open(GnosisSafeDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.gnosisSafe = gnosisSafe;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }

  setup(gnosisSafe: IGnosisSafe): void {
    const modalRef = this.modalService.open(GnosisSafeSetupDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.gnosisSafe = gnosisSafe;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      this.loadAll();
    });
  }
}
