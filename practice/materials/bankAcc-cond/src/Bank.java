
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank 
{

	private final double accounts[];
	
	private final Lock _cs = new ReentrantLock();
	private final Condition _cond = _cs.newCondition();
	
	public Bank(int n, double initBalance)
	{
				
		accounts = new double[n];
		for(int i = 0; i < accounts.length; i++)
			accounts[i] = initBalance;
	
	}
	
	// synchronized
	public void transfer(int from, int to, double amnt) 
	{
		
		_cs.lock();
		
		try {
			
			// if (accounts[from] < amnt) return;
			
			while (accounts[from] < amnt) {
				System.out.printf("[WAIT] %s: %.2f < %.2f !\n", Thread.currentThread().getName(), accounts[from], amnt);
				_cond.await();
				// wait();
				System.out.printf("[RESUME] %s: %.2f ? %.2f !\n", Thread.currentThread().getName(), accounts[from], amnt);
			}
			
			accounts[from] -= amnt;
			System.out.printf("[TRASFER] %s: %.2f from %d to %d\n", Thread.currentThread().getName(), amnt, from, to);
			accounts[to] += amnt;
			
			_cond.signalAll();
			// notifyAll();
			
		} catch (InterruptedException e) {
			
		} finally {
			
			_cs.unlock();
			
		}
		
	}
	
	// synchronized
	public double getTotalBalance() 
	{
		
		_cs.lock();
		
		try {
		
			double res = 0.0;
			
			for(int i = 0; i < accounts.length; i++)
				res += accounts[i];
			
			return res;
		
		} finally {
			
			_cs.unlock();
			
		}
		
	}
	
	public int size()
	{
		return accounts.length;
	}
	
}
