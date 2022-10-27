package DSCoinPackage;

import java.util.*;
import HelperClasses.Pair;
import HelperClasses.TreeNode;

public class Members
 {
  public String UID;
  public List<Pair<String, TransactionBlock>> mycoins;
  public Transaction[] in_process_trans;

  public void initiateCoinsend(String destUID, DSCoin_Honest DSobj) {
   int i;
   Transaction tobj = new Transaction();
   tobj.coinID = mycoins.get(0).get_first();
   tobj.Source = this;
   for( i = 0; !DSobj.memberlist[i].UID .equals(destUID); i++ ) {} // searching for destination member
   tobj.Destination = DSobj.memberlist[i];
   tobj.coinsrc_block = mycoins.get(0).get_second();
   for(i = 0; in_process_trans[i] != null ;i++) {}
   in_process_trans[i] = tobj;
   DSobj.pendingTransactions.AddTransactions(tobj);
   mycoins.remove(0);
  }

  public void initiateCoinsend(String destUID, DSCoin_Malicious DSobj) {
   int i;
   Transaction tobj = new Transaction();
   tobj.coinID = mycoins.get(0).get_first();
   tobj.Source = this;
   for( i = 0; !DSobj.memberlist[i].UID .equals(destUID); i++ ) {} // searching for destination member
   tobj.Destination = DSobj.memberlist[i];
   tobj.coinsrc_block = mycoins.get(0).get_second();
   for(i = 0; in_process_trans[i] != null ;i++) {}
   in_process_trans[i] = tobj;
   DSobj.pendingTransactions.AddTransactions(tobj);
   mycoins.remove(0);
  }

  public Pair<List<Pair<String, String>>, List<Pair<String, String>>> finalizeCoinsend (Transaction tobj, DSCoin_Honest DSObj) throws MissingTransactionException {
   TransactionBlock curr = DSObj.bChain.lastBlock; // used to find tobj containing block
   TransactionBlock curr2 = DSObj.bChain.lastBlock; // used for blockchain traversal at later stage
   ArrayList<Pair<String,String>> pathToRoot = new ArrayList<>();
   ArrayList<Pair<String,String>> proofBlocks = new ArrayList<>();
   int flag = 0;
   int i = 0;
   while(curr != null) {
    for(i = 0; i < curr.trarray.length; i++) {
      if(curr.trarray[i].coinID .equals(tobj.coinID)) {
        flag = 1;
        break;
      }
    }
    if(flag == 1) {break;} // flag turns 1 only when transaction is found
    curr = curr.previous;
   } // TB containing tobj
   if(flag == 0) {throw new MissingTransactionException();} // exception handling
   TreeNode curr1 = curr.Tree.rootnode; // curr1 is used for tree traversal and computing sibling-coupled path-to-root
   int k = i;
   int n = curr.trarray.length;
   boolean initial = true;
   while(n >= 2) {
    if(initial) {
     pathToRoot.add(0, new Pair<>(curr1.val, null));
     initial = false;
    }
    pathToRoot.add(0, new Pair<>(curr1.left.val, curr1.right.val));
    if((n/2) <= k) {
     curr1 = curr1.right;
     n = n/2;
     k = k - n;
    }
    else {
     curr1 = curr1.left;
     n = n/2;
    }
   }
   proofBlocks.add(0, new Pair<>(curr.previous.dgst, null));
   while(curr2 != curr) {
    proofBlocks.add(1, new Pair<>(curr2.dgst, curr2.previous.dgst + "#" + curr2.trsummary + "#" + curr2.nonce));
    curr2 = curr2.previous;
   }
   proofBlocks.add(1, new Pair<>(curr2.dgst, curr2.previous.dgst + "#" + curr2.trsummary + "#" + curr2.nonce));
   Transaction[] newTransList = new Transaction[100];
   int j = 0;
   for(i=0; i<100; i++) {
    newTransList[i] = new Transaction();
   }
   for(i = 0; i < in_process_trans.length; i++) {
    if(in_process_trans[i] == tobj) {
     i++;
    }
    newTransList[j] = in_process_trans[i];
    j++;
   }
   in_process_trans = newTransList;
   for(i = 0; i < tobj.Destination.mycoins.size(); i++) {
     if(Integer.parseInt(tobj.coinID) < Integer.parseInt(tobj.Destination.mycoins.get(i).get_first())) {
       tobj.Destination.mycoins.add(i,new Pair<>(tobj.coinID,curr));
       break;
     }
   }
   return new Pair<>(pathToRoot, proofBlocks);
  }

  public void MineCoin(DSCoin_Honest DSObj) throws EmptyQueueException {
   TransactionQueue pointer = DSObj.pendingTransactions;
   Transaction[] newArr = new Transaction[DSObj.bChain.tr_count];
   int i;
   for(i = 0 ; i < DSObj.bChain.tr_count; i++) {
    newArr[i] = new Transaction();
   }
   i = 0;
   while(i < DSObj.bChain.tr_count - 1) {
    if(!DSObj.bChain.lastBlock.checkTransaction(pointer.firstTransaction)) {
     pointer.RemoveTransaction();
     continue;
    }
    if(i == 0) {
     newArr[0] = pointer.firstTransaction;
     pointer.RemoveTransaction();
     i++;
    }
    else {
     for (int j = 0; j < i; j++) {
      if (pointer.firstTransaction.coinID.equals(newArr[j].coinID)) {
       pointer.RemoveTransaction();
       j = 0;
      }
     }
     newArr[i] = pointer.firstTransaction;
     i++;
     pointer.RemoveTransaction();
    }
   } // (tr_count - 1) transactions are recorded
   newArr[i].coinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
   newArr[i].Source = null;
   newArr[i].Destination = this;
   newArr[i].coinsrc_block = null; // mined coin is added in transaction array
   TransactionBlock tB = new TransactionBlock(newArr);
   DSObj.bChain.InsertBlock_Honest(tB);// Block inserted in BC
   this.mycoins.add(new Pair<>(newArr[i].coinID,tB)); // coin added in list of coins owned by Member
   DSObj.latestCoinID = newArr[i].coinID; // latest Coin ID updated
  }  

  public void MineCoin(DSCoin_Malicious DSObj) throws EmptyQueueException {

   TransactionQueue pointer = DSObj.pendingTransactions;
   Transaction[] newArr = new Transaction[DSObj.bChain.tr_count];
   int i;
   for(i = 0 ; i < DSObj.bChain.tr_count; i++) {
    newArr[i] = new Transaction();
   }
   i = 0;
   TransactionBlock lastBlock = DSObj.bChain.FindLongestValidChain();
   while(i < DSObj.bChain.tr_count - 1) {
    if(!lastBlock.checkTransaction(pointer.firstTransaction)) {
     pointer.RemoveTransaction();
     continue;
    }
    if(i == 0) {
     newArr[0] = pointer.firstTransaction;
     pointer.RemoveTransaction();
     i++;
    }
    else {
     for (int j = 0; j < i; j++) {
      if (pointer.firstTransaction.coinID.equals(newArr[j].coinID)) {
       pointer.RemoveTransaction();
       j = 0;
      }
     }
     newArr[i] = pointer.firstTransaction;
     i++;
     pointer.RemoveTransaction();
    }
   } // (tr_count - 1) transactions are recorded
   newArr[i].coinID = String.valueOf(Integer.parseInt(DSObj.latestCoinID) + 1);
   newArr[i].Source = null;
   newArr[i].Destination = this;
   newArr[i].coinsrc_block = null; // mined coin is added in transaction array
   TransactionBlock tB = new TransactionBlock(newArr);
   DSObj.bChain.InsertBlock_Malicious(tB);// Block inserted in BC
   this.mycoins.add(new Pair<>(newArr[i].coinID,tB)); // coin added in list of coins owned by Member
   DSObj.latestCoinID = newArr[i].coinID; // latest Coin ID updated
  }
}
