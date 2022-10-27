package DSCoinPackage;

public class TransactionQueue {

  public Transaction firstTransaction;
  public Transaction lastTransaction;
  public int numTransactions;

  public void AddTransactions (Transaction transaction) {
    if(this.firstTransaction == null) {
      transaction.previous = null;
      transaction.next = null;
      this.firstTransaction = transaction;
      this.lastTransaction = transaction;
      this.numTransactions = 1;
    }
    else {
      this.lastTransaction.next = transaction;
      transaction.previous = this.lastTransaction;
      transaction.next = null;
      this.lastTransaction = transaction;
      this.numTransactions++;
    }
  }
  
  public Transaction RemoveTransaction () throws EmptyQueueException {
    if(this.firstTransaction == null) {
      throw new EmptyQueueException();
    }
    else if(this.firstTransaction.next == null){
      this.numTransactions = 0;
      Transaction tr = new Transaction();
      tr = firstTransaction;
      firstTransaction = null;
      lastTransaction = null;
      return tr;
    }
    else {
      Transaction tr = new Transaction();
      tr = firstTransaction;
      this.firstTransaction = this.firstTransaction.next;
      this.firstTransaction.previous.next = null;
      this.firstTransaction.previous = null;
      this.numTransactions--;
      return tr;
    }
  }

  public int size() {
    if(this.firstTransaction == null) {numTransactions = 0;}
    return this.numTransactions;
  }
}
