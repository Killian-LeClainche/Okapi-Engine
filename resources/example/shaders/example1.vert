#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;

out vec4 vColor;

void main()
{
    vColor = color;
    gl_Position = vec4(position.x / 860 - 1, position.y / 540 - 1, position.z, 1.0);
}