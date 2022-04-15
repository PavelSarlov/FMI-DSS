
import java.math.BigInteger;

public class TaskRunner {

	public static void main(String[] args) {

		long _nano_time_1 = System.nanoTime();
		String _thread_name = Thread.currentThread().getName();

		if (args.length != 2) {
			System.out.println("TaskRunner <num of elements> <num of threads>");
			System.exit(1);
		}
		
		int el_count = Integer.valueOf(args[0]);
		int thread_count = Integer.valueOf(args[1]);

		long _nano_time_2 = System.nanoTime();

		System.out.printf(
				"[%d %-9s] n of elements: %d, threads: %d\n",
				_nano_time_2,
				_thread_name,
				el_count,
				thread_count
		);

		// NOTE: just to show that even single assign operator matters.
		System.out.printf(
				"[%d %-9s] time spent in assign(): %f (millis)\n",
				_nano_time_2,
				_thread_name,
				(_nano_time_2 - _nano_time_1) / 1000000.0
		);

		// NOTE: number of chunks/portions/amounts of work for our threads.

		int chunk_size = el_count / thread_count;

		// NOTE: all the holders that we need.

		long a[] = new long[el_count];
		BigInteger res[] = new BigInteger[thread_count];
	
		Thread tr[] = new Thread[thread_count];

		long _nano_time_3 = System.nanoTime();

		System.out.printf(
				"[%d %-9s] time spent in declare(): %f (millis)\n",
				_nano_time_3,
				_thread_name,
				(_nano_time_3 - _nano_time_2) / 1000000.0
		);

		long _nano_time_4 = System.nanoTime();

		for(int i = 0; i < thread_count; i++) {

			SumRunnable r = new SumRunnable(a, res, chunk_size, i, thread_count);
			Thread t = new Thread(r);
			tr[i] = t;
			t.start();
		
		}

		long _nano_time_5 = System.nanoTime();

		System.out.printf(
				"[%d %-9s] time spent in start(): %f (millis)\n",
				_nano_time_5,
				_thread_name,
				(_nano_time_5 - _nano_time_4) / 1000000.0
		);


		BigInteger sum = BigInteger.valueOf(0);
		// BigInteger sum = BigInteger.valueOf(1);

		long _nano_time_6 = System.nanoTime();

		for(int i = 0; i < thread_count; i++) {
			
			try {
				
				tr[i].join();

				// sum = sum.multiply(res[i]);
			} catch (InterruptedException e) {
				
			}
			
		}

		long _nano_time_7 = System.nanoTime();

		System.out.printf(
				"[%d %-9s] time spent in join(): %f (millis)\n",
				_nano_time_7,
				_thread_name,
				(_nano_time_7 - _nano_time_6) / 1000000.0
		);

		long _nano_time_8 = System.nanoTime();
		for(int i = 0; i < thread_count; i++) {
			sum = sum.add(res[i]);
		}
		long _nano_time_9 = System.nanoTime();

		System.out.printf(
				"[%d %-9s] time spent in sum(): %f (millis)\n",
				_nano_time_9,
				_thread_name,
				(_nano_time_9 - _nano_time_8) / 1000000.0
		);

		long _nano_time_10 = System.nanoTime();

		System.out.printf(
				"[%d %-9s] Result: %d\n",
				_nano_time_10,
				_thread_name,
				sum
		);

		System.out.printf(
				"[%d %-9s] Total time spent: %f (millis)\n",
				_nano_time_10,
				_thread_name,
				(_nano_time_10 - _nano_time_1) / 1000000.0
		);

	}

}
