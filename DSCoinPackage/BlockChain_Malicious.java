package DSCoinPackage;

import HelperClasses.CRF;
import HelperClasses.MerkleTree;

public class BlockChain_Malicious {

  public int tr_count;
  public static final String start_string = "DSCoin";
  public TransactionBlock[] lastBlocksList;

  public static boolean checkTransactionBlock (TransactionBlock tB) {
    CRF newobj = new CRF(64);
    MerkleTree checkSum = new MerkleTree();
    if(!tB.dgst.startsWith("0000")) {return false;}
    if(tB.previous == null) {
      if(!tB.dgst.equals(newobj.Fn(start_string + "#" + tB.trsummary + "#" + tB.nonce)))
        return false;
    }
    else {
      if(!tB.dgst.equals(newobj.Fn(tB.previous.dgst + "#" + tB.trsummary + "#" + tB.nonce)))
        return false;
    }
    // dgst is verified
    checkSum.Build(tB.trarray);
    if(!tB.trsummary .equals(checkSum.rootnode.val) ) {return false;}
    // trsummary computation is checked
    for(int i = 0 ; i < tB.trarray.length; i++) {
      if( !tB.checkTransaction(tB.trarray[i]) ) {return false;}
    }
    // every transaction in trarray is checked
    return true;
  }

  public TransactionBlock FindLongestValidChain () {
    int maxChainLength = 0;
    int l;
    int maxCLIndex = 0;
    TransactionBlock curr; // this is used for chain traversing
    TransactionBlock curr1; // this will be the last TB of the longest BC
    for(int i = 0; i < this.lastBlocksList.length; i++) {
      curr = lastBlocksList[i];
      l = 0;
      while(curr != null) {
        if(checkTransactionBlock(curr)) l++;
        else l = 0;
        curr = curr.previous;
      }
      if(l > maxChainLength) {
        maxChainLength = l;
        maxCLIndex = i;
      }
    }
    curr = lastBlocksList[maxCLIndex];
    curr1 = lastBlocksList[maxCLIndex];
    while(curr!=null){
       if(!checkTransactionBlock(curr)) {curr1 = curr;}
       curr = curr.previous;
    }
    return curr1.previous;
  }

  public void InsertBlock_Malicious (TransactionBlock newBlock) {
    CRF newobj = new CRF(64);
    String s;
    long i = 1000000000L;
    if(lastBlocksList == null) {
      while(true) {
        s = newobj.Fn(start_string + "#" + newBlock.trsummary + "#" + i);
        if(s.startsWith("0000")) { break; }
        i++;
      }
    }
    else {
      TransactionBlock lastBlock = this.FindLongestValidChain();
      newBlock.previous = lastBlock;
      while (true) {
        s = newobj.Fn(lastBlock.dgst + "#" + newBlock.trsummary + "#" + i);
        if(s.startsWith("0000")) {break;}
        i++;
      }
    }
    newBlock.nonce = String.valueOf(i);
    newBlock.dgst = s;
    TransactionBlock[] newList = new TransactionBlock[lastBlocksList.length + 1];
    int j;
    for(j = 0 ; lastBlocksList[j]!=null ; j++) {
      newList[j] = new TransactionBlock();
      newList[j] = lastBlocksList[j];
    }
    newList[j] = newBlock;
    this.lastBlocksList = newList;
  }
} // re-check this code
