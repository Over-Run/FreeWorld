#version 110

varying vec3 out_color;
varying vec2 out_texCoord;
uniform sampler2D texSampler;
uniform int hasTexture;

void main() {
    gl_FragColor = vec4(out_color, 1.0);
    if (hasTexture != 0)
        gl_FragColor *= texture2D(texSampler, out_texCoord);
}
