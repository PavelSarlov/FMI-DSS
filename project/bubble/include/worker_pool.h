#ifndef WORKER_POOL_H
#define WORKER_POOL_H

#include <thread>
#include <chrono>
#include <atomic>
#include <stdio.h>
#include "bubble_sort.h"

class worker_pool {

public:
    worker_pool();
    worker_pool(int p, int g);
    ~worker_pool();

    void start(int *arr, int n);
    static void start_worker(int i, int *result, int *arr, int n, int tn, int p, int g) {
        worker_pool::ready++;
        std::thread worker;

        if (i < p - 1) {
            worker = std::thread(worker_pool::start_worker, i + 1, result, arr, n, tn, p, g);
        }

        while(worker_pool::ready < p) {
            std::this_thread::yield();
        }

        for (int j = 0; j < g + 1; j++) {
            int start = tn * i + p * tn * j;
            if (start < n) {
                bubble_sort::bubble(tn, arr, start);
            }
        }

        if (i < p - 1) {
            worker.join();
        }

        for (int j = 0; j < g + 1; j++) {
            int s1 = tn * i + p * tn * j;
            int s2 = tn * (i + 1) + p * tn * j;
            int k = s1;
            int s = s1;
            int e = s2 + tn;

            while (k < n && k < e) {
                if (s1 < s + tn && s2 < s + 2 * tn && s2 < n) {
                    if (arr[s1] <= arr[s2]) 
                        result[k++] = arr[s1++];
                    else if (arr[s2] < arr[s1])
                        result[k++] = arr[s2++];
                }
                else {
                    if (s1 < s + tn) 
                        result[k++] = arr[s1++];
                    else
                        result[k++] = arr[s2++];
                }
            }

            while(--k < n && k >= s) {
                arr[k] = result[k];
            }
        }
    }

private:
    int m_p = 1;
    int m_g = 1;
    int *m_result = nullptr;
    inline static int ready = 0;
};

#endif
