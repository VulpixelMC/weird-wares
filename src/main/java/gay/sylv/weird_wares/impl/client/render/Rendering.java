/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import gay.sylv.weird_wares.impl.Main;
import gay.sylv.weird_wares.impl.util.Constants;
import gay.sylv.weird_wares.impl.util.Initializable;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.caffeinemc.mods.sodium.client.render.chunk.ChunkRenderMatrices;
import net.caffeinemc.mods.sodium.client.render.chunk.RenderSection;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.ChunkRenderList;
import net.caffeinemc.mods.sodium.client.render.chunk.lists.SortedRenderLists;
import net.caffeinemc.mods.sodium.client.render.chunk.region.RenderRegion;
import net.caffeinemc.mods.sodium.client.util.iterator.ByteIterator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL40C;
import org.lwjgl.opengl.GLCapabilities;
import org.slf4j.Logger;

import java.util.Iterator;

@Environment(EnvType.CLIENT)
@org.jetbrains.annotations.ApiStatus.Internal
public final class Rendering implements Initializable {
	public static final Logger LOGGER = Main.getLogger("Rendering");
	public static final Rendering INSTANCE = new Rendering();
	public static final Object2ObjectMap<SectionPos, BufferBuilder> glintBufferBuilder = new Object2ObjectArrayMap<>();
	
	public static ShaderProgram TERRAIN_GLINT_SHADER;
	public static boolean gl33Ext;
	public static boolean gl40Ext;
	public static boolean gl43Ext;
	public static boolean gl40;
	public static boolean gl43;
	
	private static int vao;
	private static int vbo;
	private static final float[] CUBE_VERTICES = {
			1.0f, 0.0f, 0.0f,  1.0f, 0.0f,
			0.0f, 0.0f, 0.0f,  0.0f, 0.0f,
			1.0f,  1.0f, 0.0f,  1.0f, 1.0f,
			1.0f,  1.0f, 0.0f,  1.0f, 1.0f,
			0.0f, 0.0f, 0.0f,  0.0f, 0.0f,
			0.0f,  1.0f, 0.0f,  0.0f, 1.0f,
			
			0.0f, 0.0f,  1.0f,  0.0f, 0.0f,
			1.0f, 0.0f,  1.0f,  1.0f, 0.0f,
			1.0f,  1.0f,  1.0f,  1.0f, 1.0f,
			1.0f,  1.0f,  1.0f,  1.0f, 1.0f,
			0.0f,  1.0f,  1.0f,  0.0f, 1.0f,
			0.0f, 0.0f,  1.0f,  0.0f, 0.0f,
			
			0.0f,  1.0f,  1.0f,  1.0f, 0.0f,
			0.0f,  1.0f, 0.0f,  1.0f, 1.0f,
			0.0f, 0.0f, 0.0f,  0.0f, 1.0f,
			0.0f, 0.0f, 0.0f,  0.0f, 1.0f,
			0.0f, 0.0f,  1.0f,  0.0f, 0.0f,
			0.0f,  1.0f,  1.0f,  1.0f, 0.0f,
			
			1.0f,  1.0f, 0.0f,  1.0f, 1.0f,
			1.0f,  1.0f,  1.0f,  1.0f, 0.0f,
			1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
			1.0f, 0.0f, 0.0f,  0.0f, 1.0f,
			1.0f,  1.0f,  1.0f,  1.0f, 0.0f,
			1.0f, 0.0f,  1.0f,  0.0f, 0.0f,
			
			0.0f, 0.0f, 0.0f,  0.0f, 1.0f,
			1.0f, 0.0f, 0.0f,  1.0f, 1.0f,
			1.0f, 0.0f,  1.0f,  1.0f, 0.0f,
			1.0f, 0.0f,  1.0f,  1.0f, 0.0f,
			0.0f, 0.0f,  1.0f,  0.0f, 0.0f,
			0.0f, 0.0f, 0.0f,  0.0f, 1.0f,
			
			1.0f,  1.0f, 0.0f,  1.0f, 1.0f,
			0.0f,  1.0f, 0.0f,  0.0f, 1.0f,
			1.0f,  1.0f,  1.0f,  1.0f, 0.0f,
			1.0f,  1.0f,  1.0f,  1.0f, 0.0f,
			0.0f,  1.0f, 0.0f,  0.0f, 1.0f,
			0.0f,  1.0f,  1.0f,  0.0f, 0.0f,
	};
	
	private Rendering() {}
	
	public static void markGlintDirty(SectionPos sectionPos) {
		glintBufferBuilder.remove(sectionPos);
	}
	
	@Override
	public void initialize() {
		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> {
			// Sanity check in case any children decide to run this on Pojav Launcher or a potato
			GLCapabilities capabilities = GL.getCapabilities();
			gl33Ext = capabilities.GL_ARB_texture_swizzle;
			gl40Ext = gl33Ext && capabilities.GL_ARB_draw_indirect;
			gl43Ext = capabilities.GL_ARB_shader_storage_buffer_object && capabilities.GL_ARB_base_instance;
			gl40 = capabilities.OpenGL40 || gl40Ext;
			gl43 = capabilities.OpenGL43 || gl43Ext;
			
			if (!gl40) {
				LOGGER.warn("=================================================================================");
				LOGGER.warn("You are using an unsupported version of OpenGL.");
				LOGGER.warn("The minimum version of OpenGL supported by {} is 4.0!", Constants.MOD_NAME);
				LOGGER.warn("Expect severe issues to arise.");
				LOGGER.warn("If you use Pojav Launcher, you will get ZERO SUPPORT! POJAV LAUNCHER IS UNSUPPORTED!");
				LOGGER.warn("=================================================================================");
				return;
			} else if (!gl43) {
				LOGGER.warn("=================================================================================");
				LOGGER.warn("You are using an old version of OpenGL less than 4.3.");
				LOGGER.warn("Expect performance issues and graphical glitches.");
				LOGGER.warn("If you use a Mac, this message can probably be ignored.");
				LOGGER.warn("=================================================================================");
			}
			
			ResourceProvider resourceProvider = minecraft.getResourceManager();
			TERRAIN_GLINT_SHADER = new ShaderProgram("terrain_glint", ShaderType.FRAGMENT, ShaderType.VERTEX);
			if (!TERRAIN_GLINT_SHADER.compile(resourceProvider)) {
				return;
			}
			
			// set samplers
			TERRAIN_GLINT_SHADER.use();
			TERRAIN_GLINT_SHADER.setInt("GlintTex", 0);
			
			vao = GL40C.glGenVertexArrays();
			vbo = GL40C.glGenBuffers();
			
			GL40C.glBindVertexArray(vao);
			GL40C.glBindBuffer(GL40C.GL_ARRAY_BUFFER, vbo);
			GL40C.glBufferData(GL40C.GL_ARRAY_BUFFER, CUBE_VERTICES, GL40C.GL_STATIC_DRAW);
			
			// position
			GL40C.glVertexAttribPointer(0, 3, GL40C.GL_FLOAT, false, 5 * 4, 0);
			GL40C.glEnableVertexAttribArray(0);
			// UV
			GL40C.glVertexAttribPointer(1, 2, GL40C.GL_FLOAT, false, 5 * 4, 3 * 4);
			GL40C.glEnableVertexAttribArray(1);
			
			GL40C.glBindVertexArray(0);
			
			// Increase depth buffer precision
			GL40C.glDepthRange(0.0f, 0.01f);
		});
	}
	
	public static void renderGlint(SortedRenderLists renderLists, ChunkRenderMatrices matrices, double x, double y, double z) {
		if (!gl40 || !TERRAIN_GLINT_SHADER.isCompiled()) return;
		
		// todo: instanced rendering
		// Batch all glints
		// Render glints instanced
		
		Iterator<ChunkRenderList> iterator = renderLists.iterator();
		
		GL40C.glEnable(GL40C.GL_DEPTH_TEST);
		GL40C.glDepthMask(false);
		GL40C.glColorMask(true, true, true, true);
		GL40C.glEnable(GL40C.GL_CULL_FACE);
		GL40C.glDepthFunc(GL40C.GL_LEQUAL);
		GL40C.glEnable(GL40C.GL_BLEND);
		GL40C.glBlendFuncSeparate(GL40C.GL_SRC_ALPHA, GL40C.GL_ONE_MINUS_SRC_COLOR, GL40C.GL_ONE, GL40C.GL_ZERO);
		
		RenderSystem.activeTexture(GL40C.GL_TEXTURE0);
		RenderSystem.bindTexture(getTextureId(ItemRenderer.ENCHANTED_GLINT_ITEM));
		
		while (iterator.hasNext()) {
			ChunkRenderList chunkRenderList = iterator.next();
			RenderRegion region = chunkRenderList.getRegion();
			ByteIterator sectionIndexIterator = chunkRenderList.sectionsWithGeometryIterator(false);
			
			if (sectionIndexIterator == null) return;
			while (sectionIndexIterator.hasNext()) {
				int sectionIndex = sectionIndexIterator.nextByteAsInt();
				RenderSection section = region.getSection(sectionIndex);
				int sectionX = section.getChunkX() * 16;
				int sectionY = section.getChunkY() * 16;
				int sectionZ = section.getChunkZ() * 16;
				
				TERRAIN_GLINT_SHADER.use();
				TERRAIN_GLINT_SHADER.setMat4("ModelViewMat", matrices.modelView());
				TERRAIN_GLINT_SHADER.setMat4("ProjMat", matrices.projection());
				TERRAIN_GLINT_SHADER.setMat4("TextureMat", setupGlintTexturing(0.5f));
				TERRAIN_GLINT_SHADER.setVec3("ChunkOffset", new Vector3f((float) ((double) sectionX - x), (float)  ((double) sectionY - y), (float) ((double) sectionZ - z)));
				TERRAIN_GLINT_SHADER.setVec4("ColorModulator", new Vector4f(1.0f, 1.0f, 1.0f, 1.0f));
				TERRAIN_GLINT_SHADER.setFloat("GlintAlpha", 0.5f);
				GL40C.glBindVertexArray(vao);
				GL40C.glDrawArrays(GL40C.GL_TRIANGLES, 0, 36);
			}
		}
		
		GL40C.glDisable(GL40C.GL_DEPTH_TEST);
		GL40C.glDepthMask(true);
		GL40C.glColorMask(true, true, true, true);
		GL40C.glEnable(GL40C.GL_CULL_FACE);
		GL40C.glDepthFunc(GL40C.GL_LEQUAL);
		GL40C.glDisable(GL40C.GL_BLEND);
		GL40C.glBlendFuncSeparate(GL40C.GL_SRC_ALPHA, GL40C.GL_ONE_MINUS_SRC_ALPHA, GL40C.GL_ONE, GL40C.GL_ZERO);
	}
	
	// Shamelessly stolen from RenderType#setupGlintTexturing
	private static Matrix4f setupGlintTexturing(float scale) {
		long l = (long)((double) Util.getMillis() * Minecraft.getInstance().options.glintSpeed().get() * 8.0);
		float f = (float)(l % 110000L) / 110000.0F;
		float g = (float)(l % 30000L) / 30000.0F;
		Matrix4f matrix4f = new Matrix4f().translation(-f, g, 0.0F);
		matrix4f.rotateZ((float) (Math.PI / 18)).scale(scale);
		return matrix4f;
	}
	
	private static int getTextureId(ResourceLocation resourceLocation) {
		TextureManager textureManager = Minecraft.getInstance().getTextureManager();
		AbstractTexture abstractTexture = textureManager.getTexture(resourceLocation);
		return abstractTexture.getId();
	}
}
