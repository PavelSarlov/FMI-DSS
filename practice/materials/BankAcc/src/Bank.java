
public class Bank 
{

	private final double accounts[];
	
	public Bank(int n, double initBalance)
	{
				
		accounts = new double[n];
		for(int i = 0; i < accounts.length; i++)
			accounts[i] = initBalance;
	
	}
	
	public void transfer(int from, int to, double amnt) 
	{
		
		if (accounts[from] < amnt) return;		
		accounts[from] -= amnt;
		accounts[to] += amnt;
		
		System.out.printf("%d %s: %.2f from %d to %d\n", System.nanoTime(), Thread.currentThread().getName(), amnt, from, to);
		System.out.printf("%d %s: Total bank balance: %.2f\n", System.nanoTime(), Thread.currentThread().getName(), getTotalBalance());

	}
	
	public double getTotalBalance() 
	{
		
		double res = 0.0;
		
		for(int i = 0; i < accounts.length; i++)
			res += accounts[i];

		return res;
		
	}
	
	public int size()
	{
		return accounts.length;
	}
	
}
