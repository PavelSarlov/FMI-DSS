#include <stdio.h>
#include <stdlib.h>
#include <time.h>

void swap(int &a, int &b) {
    int temp = a;
    a = b;
    b = temp;
}

void odd_phase(int len, int *arr, int &swaps) {
    for (int i = 0; i < len - 1; i += 2) {
        if (arr[i] > arr[i + 1]) {
            swap(arr[i], arr[i+1]);
            swaps++;
        }
    }
}

void even_phase(int len, int *arr, int &swaps) {
    for (int i = 1; i < len - 1; i += 2) {
        if (arr[i] > arr[i + 1]) {
            swap(arr[i], arr[i+1]);
            swaps++;
        }
    }
}

void bubble(int len, int *arr) {
    int swaps = 1;

    while(swaps) {
        swaps = 0;

        odd_phase(len, arr, swaps);
        even_phase(len, arr, swaps);
    }
}

int main() {
    srand(time(NULL));

    int len = 100000;
    int *arr = new int[len];
    for(int i = 0; i < len; i++) {
        arr[i] = rand();
    }

    time_t start = time(NULL);
    bubble(len, arr);
    time_t end = time(NULL);

    printf("%lld", end - start);

    delete[] arr;
    return 0;
}
