#include <iostream>
#include <algorithm>
#include <thread>
#include <chrono>
#include <vector>
#include <future>
#include <exception>
#include <regex>
#include <string.h>
#include "image.h"

const float MLBT_XMIN = -2.00f;
const float MLBT_XMAX = 0.47f;
const float MLBT_YMIN = -1.12f;
const float MLBT_YMAX = 1.12f;
const float WIDTH = (MLBT_XMAX - MLBT_XMIN);
const float HEIGHT = (MLBT_YMAX - MLBT_YMIN);


float* createColor(int i) {
    float values[] = {0.0f, 64.0f, 128.0f, 196.0f};
    float r = -(values[i % 4] / 255.0f - 1.0f);
    float g = -(values[(i / 4) % 4] / 255.0f - 1.0f);
    float b = -(values[(i / 16) % 4] / 255.0f - 1.0f);
    return new float[] {r, g, b};
}

void threadCalc(image* img, DIM xStart, DIM yStart, DIM xEnd, DIM yEnd) {

    for (DIM px=xStart; px<xEnd; px++) {
        for (DIM py=yStart; py<yEnd; py++) {
            float x0 = (float)px / img->get_width() * WIDTH + MLBT_XMIN;
            float y0 = (float)py / img->get_height() * HEIGHT + MLBT_YMIN;
            float x = 0.0f;
            float y = 0.0f;
            uint16_t iteration = 0;
            uint16_t max_iteration = 256;

            while (x * x + y * y <= 4 && iteration < max_iteration) {
                float xtemp = x * x - y * y + x0;
                y = 2 * x * y + y0;
                x = xtemp;
                iteration++;
            }

            float* _color = createColor(iteration);
            color clr(_color[0], _color[1], _color[2]);
            delete _color;
            img->set_color(clr, px, py);
        }
    }
}

int main(int argc, char* argv[]) {
    DIM width = 640;
    DIM height = 480;

    try {
        for (int i = 1; i < argc; i++) {
            if (strcmp("-s", argv[i]) == 0) {
                char *arg = argv[++i];
                if (!arg || !std::regex_match(arg, std::regex("^\\d{1,4}x\\d{1,4}$"))) {
                    throw std::invalid_argument("invalid image dimensions");
                }

                width = std::strtoul(arg, &arg, 10);
                height = std::strtoul(++arg, nullptr, 10);
            }
        }
    } catch (std::exception& e) {
        printf("%s\n", e.what());
        return 1;
    }

    image img(width, height);

    int p = 4;
    DIM step_width = img.get_width() / p;
    std::vector<std::future<void>> pool(p);

    for (int i = 0; i < p; i++) {
        pool[i] = std::async(threadCalc, &img, i * step_width, 0, std::min((i + 1) * step_width + 1, (int)img.get_width()), img.get_height());
    }

    for (int i = 0; i < p; i++) {
        pool[i].wait();
    }

    img.export_to_file("img.bmp");

    return 0;
}
