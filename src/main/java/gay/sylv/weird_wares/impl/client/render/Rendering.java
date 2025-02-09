/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import gay.sylv.weird_wares.impl.DataAttachments;
import gay.sylv.weird_wares.impl.compat.client.SodiumCompatibility;
import gay.sylv.weird_wares.impl.duck.Accessor_BufferBuilder;
import gay.sylv.weird_wares.impl.util.Initializable;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Environment(EnvType.CLIENT)
@org.jetbrains.annotations.ApiStatus.Internal
public final class Rendering implements Initializable {
	public static final Rendering INSTANCE = new Rendering();
	public static final Direction[] DIRECTIONS = Direction.values();
	public static final Object2ObjectMap<SectionPos, BufferBuilder> glintBufferBuilder = new Object2ObjectArrayMap<>();
	public static final Object2ObjectMap<SectionPos, VertexBuffer> glintVertexBuffer = new Object2ObjectArrayMap<>();
	
	private static final RandomSource RANDOM = RandomSource.create();
	private static final float Z_FIGHT_SCALE = 0.015f;
	private static final float Z_FIGHT_SCALE_Y = 0.02f;
	
	public static VertexFormat GLINT_FORMAT;
	public static VertexFormat.Mode GLINT_MODE;
	public static RenderType TERRAIN_GLINT;
	
	private Rendering() {}
	
	public static void markGlintDirty(SectionPos sectionPos) {
		glintBufferBuilder.remove(sectionPos);
	}
	
	@Override
	public void initialize() {
		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> {
			TERRAIN_GLINT = RenderType.create(
					"terrain_glint",
					DefaultVertexFormat.POSITION_TEX,
					VertexFormat.Mode.QUADS,
					1536,
					RenderType.CompositeState.builder()
							.setShaderState(new RenderStateShard.ShaderStateShard(() -> {
								try {
									return new ShaderInstance(minecraft.getResourceManager(), "rendertype_terrain_glint", DefaultVertexFormat.POSITION_TEX);
								} catch (IOException e) {
									throw new RuntimeException(e);
								}
							}))
							.setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, true, false))
							.setWriteMaskState(RenderType.COLOR_WRITE)
							.setCullState(RenderType.CULL)
							.setDepthTestState(RenderType.LEQUAL_DEPTH_TEST)
							.setTransparencyState(RenderType.GLINT_TRANSPARENCY)
							.setTexturingState(RenderType.GLINT_TEXTURING)
							.setOutputState(RenderType.ITEM_ENTITY_TARGET)
							.createCompositeState(false)
			);
			GLINT_FORMAT = TERRAIN_GLINT.format();
			GLINT_MODE = TERRAIN_GLINT.mode();
		});
		ClientChunkEvents.CHUNK_UNLOAD.register((clientLevel, chunk) -> {
			var glint = DataAttachments.getGlint(chunk);
			glint.forEach(pos -> {
				SectionPos sectionPos = SectionPos.of(pos);
				glintBufferBuilder.remove(sectionPos);
				VertexBuffer vertexBuffer = glintVertexBuffer.remove(sectionPos);
				if (vertexBuffer != null) {
					vertexBuffer.close();
				}
			});
		});
		WorldRenderEvents.BEFORE_ENTITIES.register(context -> {
			BlockModelShaper blockModelShaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
			SodiumCompatibility.getRenderedSections(context.worldRenderer()).forEach(sectionPos -> {
				BufferBuilder bufferBuilder;
				Set<BlockPos> glint;
				try (ClientLevel level = context.world()) {
					var glintOptional = DataAttachments.getGlintOptional(level.getChunkAt(sectionPos.origin()));
					if (glintOptional.isPresent()) {
						glint = glintOptional.get();
					} else return;
					if (glint.isEmpty() || glint.stream().noneMatch(x -> SectionPos.of(x).equals(sectionPos))) return;
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				boolean hasBuilder = glintBufferBuilder.containsKey(sectionPos);
				if (!hasBuilder) {
					bufferBuilder = Tesselator.getInstance().begin(GLINT_MODE, GLINT_FORMAT);
					glintBufferBuilder.put(sectionPos, bufferBuilder);
				} else {
					bufferBuilder = glintBufferBuilder.get(sectionPos);
				}
				
				// rebuild if dirty
				if (!hasBuilder) {
					PoseStack poseStack = new PoseStack();
					try (ClientLevel level = context.world()) {
						glint
								.forEach(pos -> {
									BlockState state = level.getBlockState(pos);
									BakedModel model = blockModelShaper.getBlockModel(state);
									
									poseStack.pushPose();
									poseStack.translate(SectionPos.sectionRelative(pos.getX()), SectionPos.sectionRelative(pos.getY()), SectionPos.sectionRelative(pos.getZ()));
									translateAndScale(poseStack);
									
									if (!state.getShape(level, pos).isEmpty() && state.getShape(level, pos).bounds().maxY <= 0.5f) {
										poseStack.translate(0.0f, Z_FIGHT_SCALE_Y, 0.0f);
									}
									
									// directional faces
									BlockPos.MutableBlockPos mutablePos = pos.mutable();
									for (Direction direction : DIRECTIONS) {
										if (Block.shouldRenderFace(state, level, pos, direction, mutablePos.setWithOffset(pos, direction))) {
											List<BakedQuad> quads = model.getQuads(state, direction, RANDOM);
											for (BakedQuad quad : quads) {
												bufferBuilder.putBulkData(poseStack.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, LevelRenderer.getLightColor(context.world(), pos), OverlayTexture.NO_OVERLAY);
											}
										}
									}
									// non-directional faces
									List<BakedQuad> quads = model.getQuads(state, null, RANDOM);
									for (BakedQuad quad : quads) {
										bufferBuilder.putBulkData(poseStack.last(), quad, 1.0f, 1.0f, 1.0f, 1.0f, LevelRenderer.getLightColor(context.world(), pos), OverlayTexture.NO_OVERLAY);
									}
									poseStack.popPose();
								});
						
						if (((Accessor_BufferBuilder) bufferBuilder).weird_wares$isBuilding()) {
							MeshData meshData = bufferBuilder.build();
							if (meshData != null) {
								VertexBuffer vertexBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
								vertexBuffer.bind();
								vertexBuffer.upload(meshData);
								VertexBuffer.unbind();
								glintVertexBuffer.put(sectionPos, vertexBuffer);
							} else {
								VertexBuffer vertexBuffer = glintVertexBuffer.remove(sectionPos);
								if (vertexBuffer != null) {
									vertexBuffer.close();
								}
							}
						}
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				
				VertexBuffer buffer = glintVertexBuffer.get(sectionPos);
				if (buffer != null) {
					TERRAIN_GLINT.setupRenderState();
					ShaderInstance shaderInstance = RenderSystem.getShader();
					assert shaderInstance != null;
					
					// set uniforms
					Uniform chunkOffset = shaderInstance.CHUNK_OFFSET;
					if (chunkOffset != null) {
						BlockPos sectionOriginBlockPos = sectionPos.origin();
						Vec3 sectionOrigin = new Vec3(sectionOriginBlockPos.getX(), sectionOriginBlockPos.getY(), sectionOriginBlockPos.getZ());
						Vec3 cameraPos = context.camera().getPosition();
						Vec3 pos = sectionOrigin.subtract(cameraPos);
						chunkOffset.set((float) pos.x(), (float) pos.y(), (float) pos.z());
					}
					
					// set required uniforms & bind shader
					shaderInstance.setDefaultUniforms(GLINT_MODE, context.positionMatrix(), context.projectionMatrix(), Minecraft.getInstance().getWindow());
					shaderInstance.apply();
					if (chunkOffset != null) {
						chunkOffset.upload();
					}
					
					buffer.bind();
					buffer.draw();
					
					if (chunkOffset != null) {
						chunkOffset.set(0.0f, 0.0f, 0.0f);
					}
					
					// clean up the hot mess we made on your screen
					shaderInstance.clear();
					VertexBuffer.unbind();
					TERRAIN_GLINT.clearRenderState();
				}
			});
		});
	}
	
	private static void translateAndScale(PoseStack poseStack) {
		poseStack.translate(-Z_FIGHT_SCALE / 2.0f, -Z_FIGHT_SCALE_Y / 2.0f, -Z_FIGHT_SCALE / 2.0f);
		poseStack.scale(Z_FIGHT_SCALE + 1.0f, Z_FIGHT_SCALE_Y + 1.0f, Z_FIGHT_SCALE + 1.0f);
	}
}
