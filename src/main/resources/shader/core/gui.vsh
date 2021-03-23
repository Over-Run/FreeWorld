#version 110

attribute vec3 vert;

void main() {
    gl_Position = vec4(vert, 1.0);
}
