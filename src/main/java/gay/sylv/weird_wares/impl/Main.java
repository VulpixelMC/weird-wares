package gay.sylv.weird_wares.impl;

import gay.sylv.weird_wares.impl.block.Blocks;
import gay.sylv.weird_wares.impl.client.render.Rendering;
import gay.sylv.weird_wares.impl.entity.Entities;
import gay.sylv.weird_wares.impl.item.Items;
import gay.sylv.weird_wares.impl.item.group.CreativeModeTabs;
import gay.sylv.weird_wares.impl.network.Networking;
import gay.sylv.weird_wares.impl.util.Constants;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Main implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger(Constants.MOD_NAME);
	
	@Override
	public void onInitialize() {
		Networking.INSTANCE.initialize();
		DataAttachments.INSTANCE.initialize();
		Entities.INSTANCE.initialize();
		Items.INSTANCE.initialize();
		Blocks.INSTANCE.initialize();
		CreativeModeTabs.INSTANCE.initialize();
		LootTables.INSTANCE.initialize();
		
		UseBlockCallback.EVENT.register((player, level, hand, hitResult) -> {
			ItemStack itemStack = player.getItemInHand(hand);
			if (itemStack.has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) && itemStack.getItem() instanceof BlockItem) {
				var context = new BlockPlaceContext(level, player, hand, itemStack, hitResult);
				BlockPos clickedPos = context.getClickedPos();
				// sanity check
				if (!context.canPlace() || !level.getEntitiesOfClass(LivingEntity.class, new AABB(clickedPos)).isEmpty()) return InteractionResult.PASS;
				
				LevelChunk chunk = level.getChunkAt(clickedPos);
				Set<BlockPos> enchantedBlocks = new HashSet<>(DataAttachments.getGlint(chunk));
				enchantedBlocks.add(clickedPos);
				DataAttachments.setGlint(chunk, enchantedBlocks);
				
				if (context.getLevel().isClientSide()) {
					SectionPos sectionPos = SectionPos.of(clickedPos);
					Rendering.markGlintDirty(sectionPos);
				}
			}
			return InteractionResult.PASS;
		});
		
		PlayerBlockBreakEvents.AFTER.register((level, _, pos, _, _) -> {
			ChunkAccess chunk = level.getChunk(pos);
			Set<BlockPos> glints = new HashSet<>(DataAttachments.getGlint(chunk));
			if (glints.contains(pos)) {
				glints.remove(pos);
				DataAttachments.setGlint(chunk, glints);
			}
		});
	}
}
