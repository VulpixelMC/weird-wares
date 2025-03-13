package gay.sylv.weird_wares.impl.item.dispense;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@org.jetbrains.annotations.ApiStatus.Internal
public class RemoteDispenseBehavior extends DefaultDispenseItemBehavior {
	@Override
	protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack stack) {
		Player player = FakePlayer.get(blockSource.level());
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		player.setPos(blockSource.center());
		return stack.use(blockSource.level(), player, InteractionHand.MAIN_HAND).getObject();
	}
	
	@Override
	protected void playSound(BlockSource blockSource) {
	}
	
	@Override
	protected void playAnimation(BlockSource blockSource, Direction direction) {
	}
}
