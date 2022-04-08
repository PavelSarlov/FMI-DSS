
public class ClientRunnable implements Runnable {

	private Bank bank;
	private int fromAccount;
	private double maxAmount;
	// private int DELAY = 16;
	
	public ClientRunnable(Bank b, int from, double max)
	{
		bank = b;
		fromAccount = from;
		maxAmount = max;
	}
	
	public void run() {
		
		try {
			
			// for(int i = 0; i < 64; i++) {
			while (true) {
				
				int toAccount = (int) (bank.size() * Math.random());
				double amount = maxAmount * Math.random();
				
				bank.transfer(fromAccount, toAccount, amount);

				System.out.printf(
					"%s: total_bank_balance: %.2f\n", 
					Thread.currentThread().getName(), 
					bank.getTotalBalance()
				);
				
				// Thread.sleep((int) (DELAY * Math.random()));
				
			}
			
		} catch (Exception e) {
			
		}

	}

}
