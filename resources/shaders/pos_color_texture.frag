#version 330 core

in vec4 in_Color;
in vec2 in_TexCoord;

uniform sampler2D tex;

out vec4 fragColor;

void main()
{
    fragColor = texture(tex, in_TexCoord) * in_Color;
}