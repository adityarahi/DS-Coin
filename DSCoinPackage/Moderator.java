package DSCoinPackage;

import HelperClasses.Pair;

public class Moderator
 {

  public void initializeDSCoin(DSCoin_Honest DSObj, int coinCount) {
   Members Mod = new Members();
   Mod.UID = "Moderator";
   Transaction[] trArray = new Transaction[coinCount];
   int i;
   int j;
   for(i = 0; i < coinCount; i++) {
    j = i % (DSObj.memberlist.length);
    trArray[i] = new Transaction();
    trArray[i].coinID = String.valueOf(100000 + i);
    trArray[i].coinsrc_block = null;
    trArray[i].Source = Mod;
    trArray[i].Destination = DSObj.memberlist[j];
   } // transactions are created
   Transaction[] refArray = new Transaction[DSObj.bChain.tr_count];
   TransactionBlock[] tB = new TransactionBlock[coinCount/DSObj.bChain.tr_count];
   for(i=0; i < (coinCount/DSObj.bChain.tr_count) ; i++) { // 0 <= i < no. of TB
    for(int k = 0; k < DSObj.bChain.tr_count; k++) {
     refArray[k] = trArray[i*(DSObj.bChain.tr_count) + k];
    }
    tB[i] = new TransactionBlock(refArray);
    DSObj.bChain.InsertBlock_Honest(tB[i]);// TB inserted in block-chain
   }// Transaction blocks are created
   for(i = 0; i < coinCount; i++) {
    j = i % (DSObj.memberlist.length);
    DSObj.memberlist[j].mycoins.add(new Pair<>(String.valueOf(100000 + i),tB[i/(DSObj.bChain.tr_count)] ) );
   }
   DSObj.latestCoinID = String.valueOf(100000 + coinCount - 1);
  } // works fine
    
  public void initializeDSCoin(DSCoin_Malicious DSObj, int coinCount) {
   Members Mod = new Members();
   Mod.UID = "Moderator";
   Transaction[] trArray = new Transaction[coinCount];
   int i;
   int j;
   for(i = 0; i < coinCount; i++) {
    j = i % (DSObj.memberlist.length);
    trArray[i] = new Transaction();
    trArray[i].coinID = String.valueOf(100000 + i);
    trArray[i].coinsrc_block = null;
    trArray[i].Source = Mod;
    trArray[i].Destination = DSObj.memberlist[j];
   } // transactions are created
   Transaction[] refArray = new Transaction[DSObj.bChain.tr_count];
   TransactionBlock[] tB = new TransactionBlock[coinCount/DSObj.bChain.tr_count];
   for(i=0; i < (coinCount/DSObj.bChain.tr_count) ; i++) { // 0 <= i < no. of TB
    for(int k = 0; k < DSObj.bChain.tr_count; k++) {
     refArray[k] = trArray[i*(DSObj.bChain.tr_count) + k];
    }
    tB[i] = new TransactionBlock(refArray);
    DSObj.bChain.InsertBlock_Malicious(tB[i]);// TB inserted in block-chain
   }// Transaction blocks are created
   for(i = 0; i < coinCount; i++) {
    j = i % (DSObj.memberlist.length);
    DSObj.memberlist[j].mycoins.add(new Pair<>(String.valueOf(100000 + i),tB[i/(DSObj.bChain.tr_count)] ) );
   }
   DSObj.latestCoinID = String.valueOf(100000 + coinCount - 1);
  }
}
