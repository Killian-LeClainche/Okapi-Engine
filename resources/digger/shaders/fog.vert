#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 3) in vec2 texCoord;

out vec3 in_Position;
out vec4 in_Color;
out vec2 in_TexCoord;

void main() {

    in_Position = vec3(position.x / 960 - 1, position.y / 540 - 1, position.z);
    gl_Position = vec4(in_Position, 1.0);
}