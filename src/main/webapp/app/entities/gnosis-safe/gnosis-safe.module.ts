import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { GnosisSafeComponent } from './list/gnosis-safe.component';
import { GnosisSafeDetailComponent } from './detail/gnosis-safe-detail.component';
import { GnosisSafeUpdateComponent } from './update/gnosis-safe-update.component';
import { GnosisSafeSetupDialogComponent } from './setup/gnosis-safe-setup-dialog.component';
import { GnosisSafeDeleteDialogComponent } from './delete/gnosis-safe-delete-dialog.component';
import { GnosisSafeRoutingModule } from './route/gnosis-safe-routing.module';

@NgModule({
  imports: [SharedModule, GnosisSafeRoutingModule],
  declarations: [GnosisSafeComponent, GnosisSafeDetailComponent, GnosisSafeUpdateComponent, GnosisSafeSetupDialogComponent, GnosisSafeDeleteDialogComponent, ],
  entryComponents: [GnosisSafeDeleteDialogComponent],
})
export class GnosisSafeModule {}
