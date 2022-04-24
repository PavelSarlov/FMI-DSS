#include "image.h"
#include <fstream>

color::color() : color(0, 0, 0) {}

color::color(float c) : color(c, c, c) {}

color::color(float _r, float _g, float _b) : r(_r), g(_g), b(_b) {}

color::color(const color &other) { *this = other; }

color& color::operator=(const color &other) {
    if (this != &other) {
        this->r = other.r;
        this->g = other.g;
        this->b = other.b;
    }
    return *this;
}

color::~color() {}

image::image(DIM width, DIM height) : m_width(width), m_height(height), m_colors(std::vector<color>(width * height)) {}

image::image(const image &other) { *this = other; }

image& image::operator=(const image &other) {
    if (this != &other) {
        this->m_width = other.m_width;
        this->m_height = other.m_height;
        this->m_colors = other.m_colors;
    }
    return *this;
}

image::~image() {}

color image::get_color(DIM x, DIM y) const { return this->m_colors[y * m_width + x]; }

void image::set_color(const color &c, DIM x, DIM y) { this->m_colors[y * m_width + x] = c; }

void image::export_to_file(const char *path) {
    std::ofstream f;
    f.open(path, std::ios::out | std::ios::binary);

    if(!f.is_open()) {
        printf("Failed to open file '%s'\n", path);
        return;
    }

    uint8_t bmp_pad[3] = {0,0,0};
    uint8_t padding_amount = (uint8_t) (4 - (this->m_width * 3) % 4) % 4;

    const uint8_t file_header_size = 14;
    const uint8_t info_header_size = 40;
    uint32_t file_size = file_header_size + info_header_size + this->m_width * this->m_height * 3 + padding_amount * this->m_height;

    uint8_t file_header[file_header_size] = {
        // file type
        'B',
        'M',

        // file size
        file_size,
        file_size >> 8,
        file_size >> 16,
        file_size >> 24,

        // reserved 1 (not used)
        0,
        0,
        // reserved 2 (not used)
        0,
        0,

        // pixel data offset
        file_header_size + info_header_size,
        0,
        0,
        0
    };

    uint8_t info_header[info_header_size] = {
        // header size
        info_header_size,
        0,
        0,
        0,

        // image width
        this->m_width,
        this->m_width >> 8,
        this->m_width >> 16,
        this->m_width >> 24,

        // image height
        this->m_height,
        this->m_height >> 8,
        this->m_height >> 16,
        this->m_height >> 24,

        // planes
        1,
        0,

        // bits per pixel (RGB)
        24,
        0,

        // compression (no compression)
        0,
        0,
        0,
        0,

        // image size (no compression)
        0,
        0,
        0,
        0,

        // X pixels per meter (not specified)
        0,
        0,
        0,
        0,

        // Y pixels per meter (not specified)
        0,
        0,
        0,
        0,

        // total colors (color palette not used)
        0,
        0,
        0,
        0,

        // important colors (generally ignored)
        0,
        0,
        0,
        0,
    };

    f.write((char*)file_header, file_header_size);
    f.write((char*)info_header, info_header_size);

    for (DIM y = 0; y < this->m_height; y++) {
        for (DIM x = 0; x < this->m_width; x++) {
            color c = this->get_color(x, y);

            uint8_t color[] = {
                static_cast<uint8_t>(c.r * 255.0f),
                static_cast<uint8_t>(c.g * 255.0f),
                static_cast<uint8_t>(c.b * 255.0f)
            };

            f.write((char*)color, 3);
        }

        f.write((char*)bmp_pad, padding_amount);
    }

    f.close();

    printf("File '%s' created\n", path);
}

DIM image::get_width() { return this->m_width; }

DIM image::get_height() { return this->m_height;}
