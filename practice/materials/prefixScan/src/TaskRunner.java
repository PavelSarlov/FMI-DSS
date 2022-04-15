import java.util.Calendar;

/**
 * @author lisp
 *

why ?

- interesting algorithm. serial implementation:

prefixScan[0] = a[0];
for (int i = 1; i < n; i++)
  prefixScan[i] = prefixScan[i-1] + a[i];

- any associative, combining operation:

+ addition
+ multiplication
+ max
+ min
+ logical AND
+ logical OR
+ logical XOR

 */

public class TaskRunner {

	static final int MAX_RANDOM_NUM = 1000000;
	
	public static void main(String[] args) {
		
		int i;
		boolean dump = false;
		
		if (args.length < 2) {
			
			System.out.println("TaskRunner <num of elements> <num of threads>");
			System.exit(1);
			
		}
		
		if ( args.length == 3 && args[2].compareTo("-dump") == 0 ) dump = true;
		
		int el_count = Integer.valueOf(args[0]);
		int thread_count = Integer.valueOf(args[1]);
		
		int chunk_size = el_count / thread_count;

		long o[] = new long[el_count];
		long a[] = new long[el_count];
		
		// partial prefix scan results;
		long res[] = new long[thread_count];
		
		// exclusive prefix scan results; (exclusive prefix scan is applied on res);
		long excl_res[] = new long[thread_count];
	
		Thread tr[] = new Thread[thread_count];
		
		Step s1 = new Step(); // step 1 signaling object; 
		Step s2 = new Step(); // step 2 signaling object;

		long ts_b, ts_e;

		// populating initial array(s) with values;
		ts_b = Calendar.getInstance().getTimeInMillis();
		for(i = 0; i < el_count; i++) {
			o[i] = a[i] = (int) (Math.random() * MAX_RANDOM_NUM);
		}
		ts_e = Calendar.getInstance().getTimeInMillis();
		
		System.out.println("populating a[], o[] finished in: " + (ts_e - ts_b) + " ms");
		
		
		// making actual work. starting workers.
		ts_b = Calendar.getInstance().getTimeInMillis();
		
		for(i = 0; i < thread_count; i++) {

			ScanRunnable r = new ScanRunnable(s1, s2, o, a, res, excl_res, chunk_size, i, thread_count);
			tr[i] = new Thread(r);
			tr[i].start();
			
		}
		
		// waiting on step 1 completion;
		synchronized (s1) { 
			while (s1.done < thread_count)
				try {
					s1.wait();
				} catch (InterruptedException e) {
				}
		}

		// step 1 done; let's do exclusive prefix scan on results;
		System.out.println(Thread.currentThread().getName() + ": step 1 for all ended!");
		
		excl_res[0] = 0;
		for (i = 1; i < thread_count; i++)
			excl_res[i] = excl_res[i - 1] + res[i - 1];

		System.out.println(Thread.currentThread().getName() + ": sending notification for step2!");
		
		// step2 notification. 
		// every particular thread should apply proper exclusive prefix scan result
		// for its chunk;
		
		synchronized(s2) {
			s2.notifyAll();
		}
		
		// waiting step 2 to end;
		for(i = 0; i < thread_count; i++) {
			
			try {
				
				tr[i].join();
				
			} catch (InterruptedException e) {
				
			}
			
		}

		ts_e = Calendar.getInstance().getTimeInMillis();

		// dumping total jop time;
		System.out.println("job finished in: " + (ts_e - ts_b) + " ms");
		System.out.println("a[" + (el_count - 1) + "] = " + a[el_count - 1]);

		// do we have a data dump request?
		if (dump) {
			for (i = 0; i < el_count; i++)
				System.out.printf("%16d", o[i]);
			System.out.printf("\n");
			for (i = 0; i < el_count; i++)
				System.out.printf("%16d", a[i]);
			System.out.printf("\n");
		}
		
	}

}
