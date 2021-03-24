#version 110

varying vec4 out_color;
varying vec2 out_texCoord;
uniform sampler2D texSampler;

void main() {
    gl_FragColor = texture2D(texSampler, out_texCoord) * out_color;
}
