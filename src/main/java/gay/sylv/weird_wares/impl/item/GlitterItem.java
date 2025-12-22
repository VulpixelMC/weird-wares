/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.item;

import gay.sylv.weird_wares.impl.DataAttachments;
import gay.sylv.weird_wares.impl.Main;
import gay.sylv.weird_wares.impl.network.server.AddGlintPayload;
import gay.sylv.weird_wares.impl.network.server.RemoveGlintPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@org.jetbrains.annotations.ApiStatus.Internal
public class GlitterItem extends Item {
	public GlitterItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public void appendHoverText(
			ItemStack stack,
			TooltipContext context,
			TooltipDisplay tooltipDisplay,
			Consumer<Component> tooltipAdder,
			TooltipFlag flag
	) {
		super.appendHoverText(
				stack,
				context,
				tooltipDisplay,
				tooltipAdder,
				flag
		);
		tooltipAdder.accept(Component.translatable("lore.weird-wares.item.glitter"));
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (slot.hasItem() && !slot.getItem().is(Items.GLITTER) && !slot.getItem().has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)) {
			slot.getItem().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			stack.shrink(1);
			playSound(player.level(), player);
			return true;
		}
		
		return false;
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		if (Main.GLINT_DISABLED) return InteractionResult.PASS;

		BlockPos clickedPos = context.getClickedPos();
		var chunk = context.getLevel().getChunkAt(clickedPos);
		Set<BlockPos> glints = new HashSet<>(DataAttachments.getGlint(chunk));
		if (!glints.contains(clickedPos)) {
			glints.add(clickedPos);
			DataAttachments.setGlint(chunk, glints);
			playSound(context.getLevel(), context.getPlayer());
			
			updateGlint(context, clickedPos);
			
			return InteractionResult.SUCCESS;
		}
		return super.useOn(context);
	}
	public static void updateGlint(UseOnContext context, BlockPos clickedPos) {
		updateGlint(context, clickedPos, Set.of(clickedPos));
	}
	
	public static void updateGlint(UseOnContext context, BlockPos clickedPos, Set<BlockPos> glints) {
		if (context.getLevel().isClientSide()) {
			SectionPos sectionPos = SectionPos.of(clickedPos);
		} else {
			Level level = context.getLevel();
			Player player = context.getPlayer();
			addGlint(clickedPos, (ServerLevel) level, player, glints);
		}
	}
	
	public static void addGlint(BlockPos clickedPos, ServerLevel level, Player player) {
		addGlint(clickedPos, level, player, Set.of(clickedPos));
	}
	
	public static void addGlint(BlockPos clickedPos, ServerLevel level, Player player, Set<BlockPos> glints) {
		PlayerLookup.tracking(level, clickedPos)
				.forEach(foundPlayer -> {
							if (foundPlayer != player) {
								ServerPlayNetworking.send(
										foundPlayer,
										new AddGlintPayload(
												new ChunkPos(clickedPos),
												glints
										)
								);
							}
						}
				);
	}
	
	public static void removeGlint(BlockPos clickedPos, ServerLevel level, Player player) {
		removeGlint(clickedPos, level, player, Set.of(clickedPos));
	}
	
	public static void removeGlint(BlockPos clickedPos, ServerLevel level, Player player, Set<BlockPos> glints) {
		PlayerLookup.tracking(level, clickedPos)
				.forEach(foundPlayer -> {
							if (foundPlayer != player) {
								ServerPlayNetworking.send(
										foundPlayer,
										new RemoveGlintPayload(
												new ChunkPos(clickedPos),
												glints
										)
								);
							}
						}
				);
	}
	
	private void playSound(Level level, Player player) {
		level.playSound(player, player.position().x(), player.position().y(), player.position().z(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0F, 1.0F);
	}
}
