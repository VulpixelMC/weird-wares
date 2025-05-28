package gay.sylv.weird_wares.impl.client.render;

import org.lwjgl.opengl.GL46;

public enum ShaderType {
	FRAGMENT("frag", GL46.GL_FRAGMENT_SHADER),
	VERTEX("vert", GL46.GL_VERTEX_SHADER),;
	
	private final String extension;
	private final int glType;
	
	ShaderType(String extension, int glType) {
		this.extension = "." + extension;
		this.glType = glType;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public int getGlType() {
		return glType;
	}
}
