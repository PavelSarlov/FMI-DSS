#include "worker_pool.h"

worker_pool::worker_pool() {}

worker_pool::worker_pool(int p, int g) : m_p(p), m_g(g) {}

worker_pool::~worker_pool() {
    delete[] m_result;
}

void worker_pool::start(int *arr, int n) {
    m_result = new int[n];
    int tn = n / (m_p * m_g);
    worker_pool::start_worker(0, m_result, arr, n, tn, m_p, m_g);
}
