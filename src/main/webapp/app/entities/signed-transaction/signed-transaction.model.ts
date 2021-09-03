import { IUser } from 'app/entities/user/user.model';
import { ISafeTransaction } from 'app/entities/safe-transaction/safe-transaction.model';

export interface ISignedTransaction {
  id?: number;
  signedTx?: string;
  salt?: string | null;
  signer?: IUser;
  safeTransaction?: ISafeTransaction;
}

export class SignedTransaction implements ISignedTransaction {
  constructor(
    public id?: number,
    public signedTx?: string,
    public salt?: string | null,
    public signer?: IUser,
    public safeTransaction?: ISafeTransaction
  ) {}
}

export function getSignedTransactionIdentifier(signedTransaction: ISignedTransaction): number | undefined {
  return signedTransaction.id;
}
