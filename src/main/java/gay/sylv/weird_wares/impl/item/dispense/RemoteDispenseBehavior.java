package gay.sylv.weird_wares.impl.item.dispense;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@org.jetbrains.annotations.ApiStatus.Internal
public class RemoteDispenseBehavior extends DefaultDispenseItemBehavior {
	@Override
	protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack stack) {
		Player player = makeMockPlayer(blockSource.level(), GameType.ADVENTURE);
		player.setItemInHand(InteractionHand.MAIN_HAND, stack);
		return stack.use(blockSource.level(), player, InteractionHand.MAIN_HAND).getObject();
	}
	
	@Override
	protected void playSound(BlockSource blockSource) {
	}
	
	@Override
	protected void playAnimation(BlockSource blockSource, Direction direction) {
	}
	
	private static Player makeMockPlayer(Level level, GameType gameType) {
		return new Player(level, BlockPos.ZERO, 0.0F, new GameProfile(UUID.randomUUID(), "dispense-mock-player")) {
			@Override
			public boolean isSpectator() {
				return gameType == GameType.SPECTATOR;
			}
			
			@Override
			public boolean isCreative() {
				return gameType.isCreative();
			}
			
			@Override
			public boolean isLocalPlayer() {
				return true;
			}
		};
	}
}
