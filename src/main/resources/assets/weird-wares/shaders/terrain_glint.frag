#version 410 core

uniform sampler2D GlintTex;

uniform vec4 ColorModulator;
//uniform float FogStart;
//uniform float FogEnd;
uniform float GlintAlpha;

in vec2 texCoord0;

out vec4 fragColor;

void main() {
	vec4 color = texture(GlintTex, texCoord0) * ColorModulator;
//	float fade = linear_fog_fade(vertexDistance, FogStart, FogEnd) * GlintAlpha; // todo: implement fog later
	fragColor = vec4(color.rgb * GlintAlpha, color.a);
}
