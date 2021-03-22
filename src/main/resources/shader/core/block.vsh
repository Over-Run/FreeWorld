#version 110

attribute vec3 vert;
attribute vec3 in_color;
varying vec3 out_color;
uniform mat4 projectionMatrix, worldMatrix;

void main() {
    gl_Position = projectionMatrix * worldMatrix * vec4(vert, 1.0);
    out_color = in_color;
}
