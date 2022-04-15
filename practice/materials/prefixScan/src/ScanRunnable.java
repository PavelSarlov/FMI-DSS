
public class ScanRunnable implements Runnable {

	long o[];
	long a[];
	
	long r[];
	long er[];
	
	int size;
	int th_num;
	int th_count;
	
	Step s1;
	Step s2; 
	
	public ScanRunnable(Step s1, Step s2, long o[], long a[], long r[], long er[], int size, int th_num, int th_count) {
		
		this.o = o; // original values;
		this.a = a; // summed values;
		
		this.r = r; // partial result;
		this.er = er;  // partial result (exclusive prefix scan)
		
		this.size = size; // chunk size;
		this.th_num = th_num; // thread number;
		this.th_count = th_count; // total thread count;
		
		this.s1 = s1; // step 1 notification object;
		this.s2 = s2; // step 2 notification object;
		
	}
	
	public void run() {
		
		// calculate chunk boundaries;
		int start = th_num * size;
		int end = (th_num + 1) * size - 1;
		if (th_num == (th_count - 1)) end = a.length - 1;

		// making partial chunk summing;
		System.out.println(Thread.currentThread().getName() + ": step 1 begin!");
		
		for(int i = start + 1; i <= end; i++) a[i] += a[i-1];
		r[th_num] = a[end];

		System.out.println(Thread.currentThread().getName() + ": step 1 end!");
		
		System.out.println(Thread.currentThread().getName() + ": step 2 waiting for part calcs!");
		
		//
		// notification for step 1 completion;
		// made inside step 2 lock because of possible disorder of thread timing;
		//
		synchronized(s2) {

			// step 2 lock acquired;
			
			synchronized (s1) {
				
				// step 1 lock acquired;
				System.out.println(Thread.currentThread().getName() + ": notifying for step 1 end!");
				
				// one more thread has done its work;
				s1.done++;
				// notify master thread fo that;
				s1.notify();
				
			}
			
			// wait for master thread to do its job (under step 2 lock);
			try {
				
				s2.wait();
				
			} catch (InterruptedException e) {
				
			}
			
		}

		//
		// finally apply exclusive results from previous steps;
		//
		System.out.println(Thread.currentThread().getName() + ": step 2 begin!");

		for(int i = start; i <= end; i++)
			a[i] += er[th_num];
		
		System.out.println(Thread.currentThread().getName() + ": step 2 end!");
		
	}

}
