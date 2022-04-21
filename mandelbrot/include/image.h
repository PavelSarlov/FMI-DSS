#ifndef IMAGE_H
#define IMAGE_H

#include <vector>
#include <cstdint>

typedef uint16_t DIM;

struct color {
    float r, g, b;

    color();
    color(float c);
    color(float r, float g, float b);
    color(const color &other);
    color& operator=(const color &other);
    ~color();
};

class image {
public:
    image(DIM width, DIM height);
    image(const image &other);
    image& operator=(const image &other);
    ~image();

    DIM get_width();
    DIM get_height();
    color get_color(DIM x, DIM y) const;
    void set_color(const color &c, DIM x, DIM y);

    void export_to_file(const char *path);

private:
    DIM m_width;
    DIM m_height;
    std::vector<color> m_colors;
};

#endif
