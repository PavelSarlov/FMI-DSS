
import java.math.BigDecimal;
import java.util.concurrent.locks.ReentrantLock;

public class BubbleRunnable implements Runnable {
	
	GlobalNum g;
	ReentrantLock cl[];
	long a[];
	
	int size;
	
	boolean done;
	boolean exch;
	
	public BubbleRunnable(GlobalNum g, ReentrantLock cl[], long a[], int size) {
		
		this.g = g; // global "gave to sort" elements count
		this.cl = cl; // chunk lock (critical section locks)
		this.a = a; // array of elements to sort;
		
		this.size = size; // chunk size;
		
		this.done = false; // have we done?
		this.exch = false; // do we have exchange?
		
	}

	public long _gts() {
		return System.nanoTime();
	}
	
	public void run() {
		
		int i, j, k, rel_pt;
		long temp;
		
		String _name = Thread.currentThread().getName();

		long _lt_total = 0;

        long _ts_b = _gts();

        while (!done) {
			
			k = 0; // every thread starts from critical section 0;
			exch = false; // no exchange

			long _ts_k_b = _gts();
			cl[k].lock(); // taking lock for current critical section;			
			long _ts_k_e = _gts();

            _lt_total += (_ts_k_e - _ts_k_b);

            i = g.n; // in this iteration we'll take one element on top;
			rel_pt = size; // what is our release point?

//			if (i <=0 ) { // if no elements to sort, we are done.
//				done = true;
//				cl[k].unlock();
//				break;
//			}
			
			for(j = 0; j < i; j++) { // making internal iteration
				
				if (a[j] > a[j+1]) { // do we have to change these two?
					temp = a[j]; a[j] = a[j+1]; a[j+1] = temp;
                    // BigDecimal r = new BigDecimal(a[j]).multiply(new BigDecimal(a[j+1])) ;
					exch = true;
				}
			
				if (j == (rel_pt - 1) ) { // are we over release point?
					cl[k].unlock();
					k++; // moving one step further?

					_ts_k_b = _gts();
					cl[k].lock(); // acquiring critical section lock.
					_ts_k_e = _gts();

                    _lt_total += (_ts_k_e - _ts_k_b);

					rel_pt += size; // generating next release point.

				}
				
			}

			cl[k].unlock(); // unlocking final critical section;
			if (!exch) done = true; // if no exchange is done, then we are done
			
		}

		long _ts_e = _gts();

        double _total_time = (_ts_e - _ts_b) / 1000000.0;
        double _total_lock_time = (double) _lt_total / 1000000.0;

		System.out.print(
            _name +
                    ".done: total time (millis) -> " + _total_time +
                    ", spin time (millis) -> " + (_total_time  - _total_lock_time) +
                    " + lock time (millis) -> " +  _total_lock_time  + "\n"
        );

	}


}
