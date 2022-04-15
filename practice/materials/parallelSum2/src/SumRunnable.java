
import java.math.BigInteger;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class SumRunnable implements Runnable {

	long a[];
	BigInteger r[];
	int chunk_size;
	int num;
	int thread_count;
	
	public SumRunnable(long a[], BigInteger r[], int chunk_size, int num, int thread_count) {
		
		this.a = a;
		this.r = r;
		this.chunk_size = chunk_size;
		this.num = num;
		this.thread_count = thread_count;
		
	}
	
	public void run() {

		long _nano_time_1 = System.nanoTime();
		String _thread_name = Thread.currentThread().getName();

		int start = num * chunk_size;
		int end = (num+1) * chunk_size - 1;
		if (num == (thread_count - 1)) end = a.length - 1;
		
		a[start] = (int) (Math.random() * a.length);
		//a[start] = (int) ( (new Random()).nextInt(a.length) );
		//a[start] = (int) ThreadLocalRandom.current().nextInt(a.length);
		if (a[start] == 0) a[start] = 1;
		
		r[num] = BigInteger.valueOf(a[start]);
		
		for(int i = start + 1; i <= end; i++) {
			
			a[i] = (int) (Math.random() * a.length);
			//a[i] = (int) ( (new Random()).nextInt(a.length) );
			//a[i] = (int) ThreadLocalRandom.current().nextInt(a.length);
			if (a[i] == 0) a[i] = 1;
			
			r[num] = r[num].add(BigInteger.valueOf(a[i]));
			// r[num] = r[num].multiply(BigInteger.valueOf(a[i]));
			
		}

		long _nano_time_2 = System.nanoTime();

		System.out.printf(
				"[%d %-9s] Total time spent in run(): %f (millis)\n",
				_nano_time_2,
				_thread_name,
				(_nano_time_2 - _nano_time_1) / 1000000.0
		);


	}

}
