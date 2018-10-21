#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec4 color;
layout(location = 3) in vec2 texCoord;

out vec4 in_Color;
out vec2 in_TexCoord;

void main()
{
    in_Color = color;
    in_TexCoord = texCoord;
    gl_Position = vec4(position.x / 960 - 1, position.y / 540 - 1, position.z, 1.0);
}