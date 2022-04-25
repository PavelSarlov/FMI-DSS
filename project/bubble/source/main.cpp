#include <stdio.h>
#include <stdlib.h>
#include <chrono>
#include "bubble_sort.h"
#include "worker_pool.h"

int main() {
    srand(time(NULL));

    int n = 10;
    int *arr = new int[n];
    for(int i = 0; i < n; i++) {
        arr[i] = rand();
    }

    auto start = std::chrono::high_resolution_clock::now();

    /* bubble_sort::bubble(len, arr, 0); */

    worker_pool pool(4, 1);
    pool.start(arr, n);

    auto end = std::chrono::high_resolution_clock::now();

    printf("%lld\n", std::chrono::duration_cast<std::chrono::microseconds>(end - start).count());

    for(int i = 0; i < n; i++) {
        printf("%d ", arr[i]);
    }
    printf("\n");

    delete[] arr;
    return 0;
}
