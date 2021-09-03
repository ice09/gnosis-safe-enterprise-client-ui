import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { SaltedUserComponent } from './list/salted-user.component';
import { SaltedUserDetailComponent } from './detail/salted-user-detail.component';
import { SaltedUserUpdateComponent } from './update/salted-user-update.component';
import { SaltedUserDeleteDialogComponent } from './delete/salted-user-delete-dialog.component';
import { SaltedUserRoutingModule } from './route/salted-user-routing.module';

@NgModule({
  imports: [SharedModule, SaltedUserRoutingModule],
  declarations: [SaltedUserComponent, SaltedUserDetailComponent, SaltedUserUpdateComponent, SaltedUserDeleteDialogComponent],
  entryComponents: [SaltedUserDeleteDialogComponent],
})
export class SaltedUserModule {}
