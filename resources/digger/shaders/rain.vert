#version 330 core

in vec3 in_Position;
in vec4 in_Color;
in vec3 in_Normal;
in vec2 in_TexCoord;

void main() {

	vec3 pos = vec3(in_Position.x / 960 - 1, 1 - in_Position.y / 540, -1);
    gl_Position = vec4(pos, 1.0);
}