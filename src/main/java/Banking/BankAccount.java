package Banking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final int id;
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccount(int id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId(){
        return  id;
    }
    public int getBalance() {
        // we are just reading, we do not strictly need locking here; but for consistency, we will use it
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }


    public void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();

        }
    }

    public void withdraw(int amount) {
        lock.lock();
        try {
            balance -= amount;
        } finally {
            lock.unlock();

        }
    }

    public void transfer(BankAccount target, int amount) {
        // TODO: Safely make the changes
        // HINT: Both accounts need to be locked, while the changes are being made
        // HINT: Be cautious of potential deadlocks.
        //To prevent deadlocks, we will always lock accounts in a consistent order
        //We will use account ID to determine  the locking order

        BankAccount first = this.id < target.id ? this : target;
        BankAccount second = this.id < target.id ? target : this;

        first.lock.lock();
        try {
            second.lock.lock();
            try {
                this.balance -= amount;
                target.balance += amount;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }


    }
}
