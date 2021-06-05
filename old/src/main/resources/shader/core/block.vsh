#version 110

attribute vec3 vert;
attribute vec4 in_color;
attribute vec2 in_texCoord;
varying vec4 out_color;
varying vec2 out_texCoord;
uniform mat4 projectionMatrix, modelViewMatrix;

void main() {
    gl_Position = projectionMatrix * modelViewMatrix * vec4(vert, 1.0);
    out_color = in_color;
    out_texCoord = in_texCoord;
}
