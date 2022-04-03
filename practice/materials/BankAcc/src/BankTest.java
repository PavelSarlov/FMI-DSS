
public class BankTest {

	/*
	BankAcc. Problem description.

	We have a bank and its customers.

	double accounts[]

	:- number of clients
	:- each client accounts[i], initial balance
	:- what bank does with account - transfers money, i.e. .transfer(from,to,amount)
	:- gives info about its total balance

	Each Client i we represent as - Thread i (Runnable i)

	:- what client does with his/her/its :) account - 'll make a simulation.

	- given number of times for example 64;
	- thinks for an number (destination account);
	- thinks for amount (of $$);
	- if has enough money, .transfer() to another bank client;

	*/

	private static final int NUM_ACCOUNTS = 100;
	private static final double INITIAL_ACCOUNT_BALANCE = 1000.0;
	
	public static void main(String[] args) {
		
		Bank b = new Bank(NUM_ACCOUNTS, INITIAL_ACCOUNT_BALANCE);
		
		Thread jobs[] = new Thread[NUM_ACCOUNTS];

		System.out.printf("%d %s: Initial bank balance: %.2f\n", System.nanoTime(), Thread.currentThread().getName(), b.getTotalBalance());
		// FORK.step
		for(int i=0; i < NUM_ACCOUNTS; i++) {
			
			TransferRunnable r = new TransferRunnable(b, i, INITIAL_ACCOUNT_BALANCE);
			Thread t = new Thread(r);
			t.start();
			jobs[i] = t;
			
		}

		// JOIN.step
		for(int i = 0; i < NUM_ACCOUNTS; i++) {
			
			try {
				
				jobs[i].join();
				
			} catch (InterruptedException e) {
				
			}
			
		}

		System.out.printf("%d %s: Final bank balance: %.2f\n", System.nanoTime(), Thread.currentThread().getName(), b.getTotalBalance());
		
	}

}
