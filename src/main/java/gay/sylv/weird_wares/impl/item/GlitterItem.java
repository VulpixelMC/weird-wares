package gay.sylv.weird_wares.impl.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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
			return true;
		}
		
		return false;
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		InteractionResultHolder<ItemStack> ret = super.use(level, player, usedHand);
		level.playSound(player, new BlockPos((int) player.position().x(), (int) player.position().y(), (int) player.position().z()), SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.PLAYERS, 1.0F, 2.0F);
		return ret;
	}
}
