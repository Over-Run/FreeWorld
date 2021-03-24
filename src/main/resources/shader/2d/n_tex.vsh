#version 110

attribute vec2 vert;
attribute vec4 in_color;
varying vec4 out_color;
uniform mat4 projModelViewMat;

void main() {
    gl_Position = projModelViewMat * vec4(vert, 0.0, 1.0);
    out_color = in_color;
}
