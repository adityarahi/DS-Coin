package DSCoinPackage;

import HelperClasses.MerkleTree;
import HelperClasses.CRF;

public class TransactionBlock {

  public Transaction[] trarray;
  public TransactionBlock previous;
  public MerkleTree Tree;
  public String trsummary;
  public String nonce;
  public String dgst;

  public TransactionBlock(Transaction[] t) {
    this.trarray = new Transaction[t.length];
    for(int i=0; i < t.length; i++) {
      this.trarray[i] = new Transaction();
      this.trarray[i] = t[i];
    } // array copied from t to trarray

    this.previous = null;
    this.Tree = new MerkleTree();
    this.Tree.Build(t);
    this.trsummary = this.Tree.rootnode.val;
    this.dgst = null;
  }

  public TransactionBlock() {}

  public boolean checkTransaction (Transaction t) {
    int flag = 0;
    if(t.coinsrc_block == null) {return true;}
    for(int i = 0 ; i < t.coinsrc_block.trarray.length ; i++) {
      if( t.coinsrc_block.trarray[i].coinID .equals(t.coinID) && (t.Source.UID .equals( t.coinsrc_block.trarray[i].Destination.UID))) {
        flag = 1; // coinID found in coin source block, switch on flag
        break;
      }
    } // t'.coinID = t.coinID and t'.Destination = t.Source checked
    if(flag == 0) {return false;}
    TransactionBlock blockPointer = this;
    while(blockPointer != t.coinsrc_block) {
      for(int i = 0; i < blockPointer.trarray.length; i++) {
        if(blockPointer.trarray[i].coinID .equals(t.coinID)) {return false;}
      }
      blockPointer = blockPointer.previous;
    } // t.coinID searched in the intermediate blocks
    return true;
  }
}
