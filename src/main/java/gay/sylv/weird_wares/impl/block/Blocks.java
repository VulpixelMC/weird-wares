/**
 * World In a Jar
 * Copyright (C) 2024  VulpixelMC
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package gay.sylv.weird_wares.impl.block;

import gay.sylv.weird_wares.impl.block.entity.type.BlockEntityHolder;
import gay.sylv.weird_wares.impl.util.Constants;
import gay.sylv.weird_wares.impl.util.Conversions;
import gay.sylv.weird_wares.impl.util.Initializable;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

public final class Blocks implements Initializable {
	public static final Blocks INSTANCE = new Blocks();
	
	public static BlockHolder<Block, BlockItem> UNKNOWN;
	public static BlockHolder<Block, BlockItem> INFO_UPDATE;
	public static BlockHolder<Block, BlockItem> INFO_UPDATE2;
	public static BlockHolder<Block, BlockItem> GLOWING_OBSIDIAN;
	public static BlockHolder<NetherReactorBlock, BlockItem> NETHER_REACTOR;
	
	private Blocks() {}
	
	@Override
	public void initialize() {
		UNKNOWN = register(
				"unknown",
				new Block(
						BlockBehaviour.Properties.of()
								.isValidSpawn((_, _, _, _) -> true)
								.instabreak()
								.mapColor(MapColor.DIRT)
								.friction(0.98f)
				)
		);
		INFO_UPDATE = register(
				"info_update",
				new Block(
						BlockBehaviour.Properties.of()
								.sound(SoundType.GRAVEL)
								.isValidSpawn((_, _, _, _) -> true)
								.strength(0.0f, Constants.INFINITE_BLAST_RESISTANCE)
								.mapColor(MapColor.COLOR_LIGHT_GREEN)
				)
		);
		INFO_UPDATE2 = register(
				"info_update2",
				new Block(
						BlockBehaviour.Properties.ofFullCopy(INFO_UPDATE.block())
				)
		);
		GLOWING_OBSIDIAN = register(
				"glowing_obsidian",
				new Block(
						BlockBehaviour.Properties.ofFullCopy(net.minecraft.world.level.block.Blocks.OBSIDIAN)
								.emissiveRendering((_, _, _) -> true)
								.lightLevel(_ -> 12)
				)
		);
		NETHER_REACTOR = register(
				"nether_reactor",
				new NetherReactorBlock(
						BlockBehaviour.Properties.of()
								.strength(3.0f, 6.0f)
								.requiresCorrectToolForDrops()
				)
		);
		
		if (Constants.isClient()) {
			BlockRendering.INSTANCE.initialize();
		}
	}
	
	private static <B extends Block> B registerBlock(@NotNull String id, B block) {
		return Registry.register(BuiltInRegistries.BLOCK, modId(id), block);
	}
	
	private static <B extends Block> BlockHolder<B, BlockItem> register(@NotNull String id, B block) {
		return register(id, block, new BlockItem(block, new Item.Properties()));
	}
	
	private static <B extends Block, I extends Item> BlockHolder<B, I> register(@NotNull String id, B block, I item) {
		block = registerBlock(id, block);
		item = Registry.register(BuiltInRegistries.ITEM, modId(id), item);
		return new BlockHolder<>(block, item);
	}
	
	private static <B extends Block, I extends Item, BE extends BlockEntity> BlockEntityHolder<B, I, BE> registerBlockEntityItem(@NotNull String id, B block, BlockEntityType.BlockEntitySupplier<BE> supplier, I item) {
		BlockHolder<B, I> holder = register(id, block, item);
		BlockEntityType<BE> type = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, modId(id), BlockEntityType.Builder.of(supplier, block).build());
		return Conversions.convert(holder, type);
	}
	
	private static <B extends Block, BE extends BlockEntity> BlockEntityHolder<B, BlockItem, BE> registerBlockEntityItem(@NotNull String id, B block, BlockEntityType.BlockEntitySupplier<BE> supplier) {
		return registerBlockEntityItem(id, block, supplier, new BlockItem(block, new Item.Properties()));
	}
	
	public static final class BlockRendering implements Initializable {
		public static final BlockRendering INSTANCE = new BlockRendering();
		
		private BlockRendering() {}
		
		private static <B extends Block, I extends Item, BE extends BlockEntity> void register(@NotNull BlockEntityHolder<B, I, BE> holder, BlockEntityRendererProvider<BE> rendererProvider) {
			BlockEntityRenderers.register(holder.type(), rendererProvider);
		}
		
		private static void addRenderType(Block block, RenderType renderType) {
			BlockRenderLayerMap.INSTANCE.putBlock(block, renderType);
		}
	}
}
