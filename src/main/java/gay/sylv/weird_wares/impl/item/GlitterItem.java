package gay.sylv.weird_wares.impl.item;

import gay.sylv.weird_wares.impl.DataAttachments;
import gay.sylv.weird_wares.impl.client.render.Rendering;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlitterItem extends Item {
	public GlitterItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
		return true;
	}
	
	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (slot.hasItem() && !slot.getItem().is(Items.GLITTER) && !slot.getItem().has(DataComponents.ENCHANTMENT_GLINT_OVERRIDE)) {
			slot.getItem().set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
			stack.shrink(1);
			try (Level level = player.level()) {
				playSound(level, player);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			return true;
		}
		
		return false;
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		BlockPos clickedPos = context.getClickedPos();
		var chunk = context.getLevel().getChunkAt(clickedPos);
		List<BlockPos> glints = new ArrayList<>(DataAttachments.getGlint(chunk));
		if (!glints.contains(clickedPos)) {
			glints.add(clickedPos);
			DataAttachments.setGlint(chunk, glints);
			playSound(context.getLevel(), context.getPlayer());
			
			if (context.getLevel().isClientSide()) {
				SectionPos sectionPos = SectionPos.of(clickedPos);
				Rendering.markGlintDirty(sectionPos);
			}
			
			return InteractionResult.SUCCESS;
		}
		return super.useOn(context);
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		InteractionResultHolder<ItemStack> ret = super.use(level, player, usedHand);
		playSound(level, player);
		return ret;
	}
	
	private void playSound(Level level, Player player) {
		level.playSound(player, player.position().x(), player.position().y(), player.position().z(), SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.PLAYERS, 2.0F, 1.0F);
	}
}
