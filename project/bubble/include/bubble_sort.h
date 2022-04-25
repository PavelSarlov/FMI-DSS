#ifndef BUBBLE_SORT_H
#define BUBBLE_SORT_H

class bubble_sort {
    static void swap(int &a, int &b) {
        int temp = a;
        a = b;
        b = temp;
    }

    static void odd_phase(int len, int *arr, int start, int &swaps) {
        for (int i = start; i < start + len - 1; i += 2) {
            if (arr[i] > arr[i + 1]) {
                swap(arr[i], arr[i+1]);
                swaps++;
            }
        }
    }

    static void even_phase(int len, int *arr, int start, int &swaps) {
        for (int i = start + 1; i < start + len - 1; i += 2) {
            if (arr[i] > arr[i + 1]) {
                swap(arr[i], arr[i+1]);
                swaps++;
            }
        }
    }

public:
    static void bubble(int len, int *arr, int start) {
        int swaps = 1;

        while(swaps) {
            swaps = 0;

            bubble_sort::odd_phase(len, arr, start, swaps);
            bubble_sort::even_phase(len, arr, start, swaps);
        }
    }
};

#endif
