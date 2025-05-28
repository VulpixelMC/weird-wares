package gay.sylv.weird_wares.impl.client.render;

import gay.sylv.weird_wares.impl.util.Constants;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL40C;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

@org.jetbrains.annotations.ApiStatus.Internal
public class ShaderProgram {
	private final String name;
	private final ShaderType[] shaderTypes;
	private int program = -1;
	private boolean compiled = false;
	
	public ShaderProgram(String name, ShaderType... shaderTypes) {
		this.name = name;
		this.shaderTypes = shaderTypes;
	}
	
	public boolean compile(ResourceProvider resourceProvider) {
		this.program = GL40C.glCreateProgram();
		
		for (ShaderType shaderType : shaderTypes) {
			try {
				String shaderSource = openShader(resourceProvider, Constants.modId(name), shaderType);
				int shader = GL40C.glCreateShader(shaderType.getGlType());
				GL40C.glShaderSource(shader, shaderSource);
				GL40C.glCompileShader(shader);
				
				int[] success = new int[1];
				GL40C.glGetShaderiv(shader, GL40C.GL_COMPILE_STATUS, success);
				if (success[0] == 0) {
					String infoLog = GL40C.glGetShaderInfoLog(shader);
					Rendering.LOGGER.error("Failed to compile shader {} for program {}", shaderType, name);
					Rendering.LOGGER.error(infoLog);
					GL40C.glDeleteShader(shader);
					GL40C.glDeleteProgram(program);
					return false;
				}
				
				GL40C.glAttachShader(program, shader);
				GL40C.glDeleteShader(shader);
			} catch (IOException e) {
				Rendering.LOGGER.error("Failed to open shader {} for program {}", shaderType, name, e);
			}
		}
		
		GL40C.glLinkProgram(program);
		
		int[] success = new int[1];
		GL40C.glGetProgramiv(program, GL40C.GL_LINK_STATUS, success);
		if (success[0] == 0) {
			String infoLog = GL40C.glGetProgramInfoLog(program);
			Rendering.LOGGER.error("Failed to link shaders for program {}", name);
			Rendering.LOGGER.error(infoLog);
			return false;
		}
		
		compiled = true;
		return true;
	}
	
	public void setMat4(String name, Matrix4fc matrix4f) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
		GL40C.glUniformMatrix4fv(GL40C.glGetUniformLocation(program, name), false, matrix4f.get(buffer));
	}
	
	public void setVec3(String name, Vector3fc vector) {
		GL40C.glUniform3f(GL40C.glGetUniformLocation(program, name), vector.x(), vector.y(), vector.z());
	}
	
	public void setVec4(String name, Vector4fc vector) {
		GL40C.glUniform4f(GL40C.glGetUniformLocation(program, name), vector.x(), vector.y(), vector.z(), vector.w());
	}
	
	public void setInt(String name, int value) {
		GL40C.glUniform1i(GL40C.glGetUniformLocation(program, name), value);
	}
	
	public void setFloat(String name, float value) {
		GL40C.glUniform1f(GL40C.glGetUniformLocation(program, name), value);
	}
	
	public void use() {
		GL40C.glUseProgram(program);
	}
	
	public int getProgram() {
		return program;
	}
	
	public boolean isCompiled() {
		return compiled;
	}
	
	private static String openShader(ResourceProvider resourceProvider, ResourceLocation loc, ShaderType shaderType) throws IOException {
		Resource resource = resourceProvider.getResourceOrThrow(
				loc
						.withPrefix("shaders/")
						.withSuffix(shaderType.getExtension())
		);
		try (InputStream inputStream = resource.open()) {
			return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
		}
	}
}
