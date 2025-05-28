#version 410 core

layout (location = 0) in vec3 Position;
layout (location = 1) in vec2 UV0;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat4 TextureMat;
uniform vec3 ChunkOffset;
//uniform int FogShape;

out vec2 texCoord0;

void main() {
	vec3 pos = Position + ChunkOffset;
	gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

//	vertexDistance = fog_distance(pos, FogShape); // todo: implement fog
	texCoord0 = (TextureMat * vec4(UV0, 0.0, 1.0)).xy;
}
