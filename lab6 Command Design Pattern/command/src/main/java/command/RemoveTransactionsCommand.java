package command;

import model.Account;
import model.Transaction;

import java.util.ArrayList;

public class RemoveTransactionsCommand implements Command{
    private ArrayList<Transaction> transactionsToRemove;
    private Account account;

    public RemoveTransactionsCommand(ArrayList<Transaction> transactionsToRemove, Account account) {
        this.transactionsToRemove = transactionsToRemove;
        this.account = account;
    }

    @Override
    public void execute(){
        //account.addTransaction(transactionsToRemove);
        for(Transaction transaction : transactionsToRemove){
            account.removeTransaction(transaction);
        }
    }
    @Override
    public void undo(){
        for(Transaction transaction : transactionsToRemove){
            account.addTransaction(transaction);
        }
    }

    @Override
    public void redo(){
        for(Transaction transaction : transactionsToRemove){
            account.removeTransaction(transaction);
        }
    }

    @Override
    public String getName(){
        return "New transaction: " + transactionsToRemove.toString();
    }
}
