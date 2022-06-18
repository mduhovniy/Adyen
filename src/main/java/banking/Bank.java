package banking;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Private Variables:<br>
 * {@link #accounts}: List&lt;Long, Account&gt;
 */
public class Bank implements BankInterface {
    private final ConcurrentHashMap<Long, Account> accounts;
    private final AtomicLong id;

    public Bank() {
        this.accounts = new ConcurrentHashMap<>();
        this.id = new AtomicLong(0);
    }

    private Account getAccount(Long accountNumber) {
        return this.accounts.get(accountNumber);
    }

    public Long openCommercialAccount(Company company, int pin, double startingDeposit) {
        long accountNum = id.incrementAndGet();
        this.accounts.put(accountNum, new CommercialAccount(company, accountNum, pin, startingDeposit));
        return accountNum;
    }

    public Long openConsumerAccount(Person person, int pin, double startingDeposit) {
        long newAccountNumber = id.incrementAndGet();
        this.accounts.put(newAccountNumber, new ConsumerAccount(person, newAccountNumber, pin, startingDeposit));
        return newAccountNumber;
    }

    public boolean authenticateUser(Long accountNumber, int pin) {
        final Account account = this.accounts.get(accountNumber);
        return account != null && account.validatePin(pin);
    }

    public double getBalance(Long accountNumber) {
        return this.accounts.get(accountNumber).getBalance();
    }

    public void credit(Long accountNumber, double amount) {
        this.accounts.get(accountNumber).creditAccount(amount);
    }

    public boolean debit(Long accountNumber, double amount) {
        return accounts.get(accountNumber).debitAccount(amount);
    }
}
