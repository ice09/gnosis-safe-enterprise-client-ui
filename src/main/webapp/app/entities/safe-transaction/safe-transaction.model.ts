import * as dayjs from 'dayjs';
import { ISignedTransaction } from 'app/entities/signed-transaction/signed-transaction.model';
import { IUser } from 'app/entities/user/user.model';
import { IGnosisSafe } from 'app/entities/gnosis-safe/gnosis-safe.model';

export interface ISafeTransaction {
  id?: number;
  comment?: string | null;
  token?: string | null;
  value?: number;
  receiver?: string;
  created?: dayjs.Dayjs;
  signedTransactions?: ISignedTransaction[] | null;
  creator?: IUser;
  gnosisSafe?: IGnosisSafe;
}

export class SafeTransaction implements ISafeTransaction {
  constructor(
    public id?: number,
    public comment?: string | null,
    public token?: string | null,
    public value?: number,
    public receiver?: string,
    public created?: dayjs.Dayjs,
    public signedTransactions?: ISignedTransaction[] | null,
    public creator?: IUser,
    public gnosisSafe?: IGnosisSafe
  ) {}
}

export function getSafeTransactionIdentifier(safeTransaction: ISafeTransaction): number | undefined {
  return safeTransaction.id;
}
