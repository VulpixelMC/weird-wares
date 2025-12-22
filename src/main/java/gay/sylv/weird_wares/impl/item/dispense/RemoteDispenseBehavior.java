/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.item.dispense;

import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@org.jetbrains.annotations.ApiStatus.Internal
public class RemoteDispenseBehavior extends DefaultDispenseItemBehavior {
	@Override
	protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack stack) {
		Player player = FakePlayer.get(blockSource.level());
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		player.setPos(blockSource.center());
		player.setYRot(blockSource.state().getValue(DispenserBlock.FACING).toYRot());
		stack.use(blockSource.level(), player, InteractionHand.MAIN_HAND);

		return stack;
	}
	
	@Override
	protected void playSound(BlockSource blockSource) {
		Player player = FakePlayer.get(blockSource.level());
		Vec3 position = player.position();
		blockSource.level().playSound(
				null,
				position.x,
				position.y,
				position.z,
				SoundEvents.SCULK_CLICKING,
				SoundSource.BLOCKS,
				1.0f,
				1.125f
		);
	}
	
	@Override
	protected void playAnimation(BlockSource blockSource, Direction direction) {
	}
}
