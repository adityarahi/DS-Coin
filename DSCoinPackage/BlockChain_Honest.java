package DSCoinPackage;

import HelperClasses.CRF;

public class BlockChain_Honest {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock lastBlock;

  public void InsertBlock_Honest (TransactionBlock newBlock) {
    CRF newobj = new CRF(64);
    newBlock.previous = this.lastBlock;
    String s;
    long i = 1000000000L;
    while(true) {
      if(lastBlock == null) {
        s = newobj.Fn( start_string + "#" + newBlock.trsummary + "#" + i);
      }
      else {
        s = newobj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + i);
      }
      if(s.startsWith("0000")) { break; }
      i++;
    }
    newBlock.nonce = String.valueOf(i);
    newBlock.dgst = s;
    this.lastBlock = newBlock;
  }
}
