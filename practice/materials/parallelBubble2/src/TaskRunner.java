import java.util.Calendar;
import java.util.concurrent.locks.ReentrantLock;

public class TaskRunner {

	static final int MAX_RANDOM_NUM = 1000000000;
	
	public static void main(String[] args) {
		
		// NOTE: local index variable;
		int i;
		
		// NOTE: do we have to dump our raw and sorted data?
		boolean dump = false;
		
		// NOTE: enough arguments in command line?
		if (args.length < 2) {
			
			System.out.println("TaskRunner <num of elements> <num of threads>");
			System.exit(1);
			
		}
		
		// NOTE: if we have three arguments then, may be the third one is a dump request?
		if ( args.length == 3 && args[2].compareTo("-dump") == 0 ) dump = true;

		long ts_b, ts_e;

		int el_count = Integer.valueOf(args[0]);
		int thread_count = Integer.valueOf(args[1]);

		// NOTE: every chunk of array has that many elements
		int chunk_size = el_count / thread_count;
	
		// NOTE: making thread pool, to wait for
		Thread tr[] = new Thread[thread_count];

		// NOTE: global number showing how mny elements we have to sort.
		GlobalNum gn = new GlobalNum(el_count - 1);
		
		// NOTE: making critical sections pool
		ReentrantLock cl[];
		if (el_count % thread_count == 0) { // if we have no reminder;
			cl = new ReentrantLock[thread_count];
	 		for(i = 0; i < thread_count; i++) cl[i] = new ReentrantLock(true);
		} else { // reminder makes one more critical section;
			cl = new ReentrantLock[thread_count + 1];
	 		for(i = 0; i < thread_count+1; i++) cl[i] = new ReentrantLock(true);
		}
		
		long a[] = new long[el_count];
		
		// NOTE: populating raw data if requested
		ts_b = Calendar.getInstance().getTimeInMillis();
		for(i = 0; i < el_count; i++) {
			a[i] = (int) (Math.random() * MAX_RANDOM_NUM);
		}
		ts_e = Calendar.getInstance().getTimeInMillis();
		
		System.out.println("populating a[] finished in: " + (ts_e - ts_b) + " millis");
		
		// NOTE: dumping raw data if requested
		if (dump) {
			for (i = 0; i < el_count; i++)
				System.out.printf("%16d", a[i]);
			System.out.printf("\n");
		}

		// System.out.println("chunk_size: " + chunk_size + "\n");

		// NOTE: making actual work
		ts_b = Calendar.getInstance().getTimeInMillis();

  		for(i = 0; i < thread_count; i++) {

			BubbleRunnable r = new BubbleRunnable(gn, cl, a, chunk_size);
			tr[i] = new Thread(r);
			tr[i].start();
			
		}
  		
  		// NOTE: waiting threads to finish
		for(i = 0; i < thread_count; i++) {
			
			try {
				
				tr[i].join();
				
			} catch (InterruptedException e) {
				
			}
			
		}

		ts_e = Calendar.getInstance().getTimeInMillis();

		System.out.println("job finished in: " + (ts_e - ts_b) + " millis");

		// NOTE: dumping sorted data if requested
		if (dump) {
			for (i = 0; i < el_count; i++)
				System.out.printf("%16d", a[i]);
			System.out.printf("\n");
		}
		
	}

}
