/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.item;

import gay.sylv.weird_wares.impl.item.component.DataComponents;
import gay.sylv.weird_wares.impl.item.component.RemoteSet;
import gay.sylv.weird_wares.impl.item.component.RemoteState;
import gay.sylv.weird_wares.impl.item.component.RemoteTarget;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.function.Consumer;

@org.jetbrains.annotations.ApiStatus.Internal
public class SculkRemoteItem extends Item {
	public SculkRemoteItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		if (stack.has(DataComponents.REMOTE_TARGET)) {
			RemoteTarget remoteTarget = stack.get(DataComponents.REMOTE_TARGET);
			assert remoteTarget != null;
			tooltipComponents.accept(
					Component.translatable(
							"tooltip.weird-wares.item.sculk_remote.dimension",
							remoteTarget.dimension().identifier()
					).withStyle(ChatFormatting.DARK_GRAY)
			);
			tooltipComponents.accept(
					Component.translatable(
							"tooltip.weird-wares.item.sculk_remote.position",
							remoteTarget.pos().getX(),
							remoteTarget.pos().getY(),
							remoteTarget.pos().getZ()
					).withStyle(ChatFormatting.DARK_GRAY)
			);
		}
		
		if (stack.has(DataComponents.REMOTE_STATE)) {
			if (Objects.requireNonNull(stack.get(DataComponents.REMOTE_STATE)).states().contains(RemoteState.Type.SHIFT)) {
				tooltipComponents.accept(
						Component.translatable(
								"tooltip.weird-wares.item.sculk_remote.remote_state.shift"
						).withStyle(ChatFormatting.DARK_GRAY)
				);
			}
		}
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		
		if (!level.isClientSide() && player != null && player.isShiftKeyDown() && !context.isInside()) {
			context.getItemInHand().set(
					DataComponents.REMOTE_SET,
					RemoteSet.TRUE
			);
			
			RemoteTarget remoteTarget = new RemoteTarget(level.dimension(), context.getClickedPos());
			if (Objects.equals(context.getItemInHand().get(DataComponents.REMOTE_TARGET), remoteTarget)) {
				return InteractionResult.FAIL;
			}
			
			context.getItemInHand().set(
					DataComponents.REMOTE_TARGET,
					remoteTarget
			);
			level.playSound(
					null,
					player,
					SoundEvents.UI_BUTTON_CLICK.value(),
					SoundSource.PLAYERS,
					1.0f,
					1.0f
			);
			
			return InteractionResult.CONSUME;
		}
		
		return super.useOn(context);
	}
	
	@Override
	public @NotNull InteractionResult use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (
				!level.isClientSide() &&
				!stack.getOrDefault(DataComponents.REMOTE_SET, RemoteSet.FALSE).isSet() &&
				stack.has(DataComponents.REMOTE_TARGET)
		) {
			RemoteTarget remoteTarget = stack.get(DataComponents.REMOTE_TARGET);
			assert remoteTarget != null;
			assert level.getServer() != null;
			Level remoteLevel = level.getServer().getLevel(remoteTarget.dimension());
			assert remoteLevel != null;
			boolean shiftKeyDown = player.isShiftKeyDown();
			if (stack.has(DataComponents.REMOTE_STATE) && player instanceof FakePlayer) {
				if (Objects.requireNonNull(stack.get(DataComponents.REMOTE_STATE)).states().contains(RemoteState.Type.SHIFT)) {
					player.setShiftKeyDown(true);
				}
			}
			
			this.useBlock(
					remoteTarget.pos(),
					player,
					remoteLevel,
					new BlockHitResult(
							player.position(),
							player.getDirection(),
							remoteTarget.pos(),
							true
					)
			);
			if (!shiftKeyDown) {
				player.setShiftKeyDown(false);
			}
			if (!player.getUUID().equals(FakePlayer.DEFAULT_UUID)) {
				level.playSound(
						null,
						player,
						SoundEvents.SCULK_CLICKING,
						SoundSource.PLAYERS,
						1.0f,
						1.0f
				);
			}
		}
		
		stack.remove(DataComponents.REMOTE_SET);
		
		return super.use(level, player, usedHand);
	}
	
	@Override
	public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
		ItemStack itemInHand = entity.getItemInHand(InteractionHand.MAIN_HAND);
		if (itemInHand.is(Items.SCULK_REMOTE)) {
			float pitch;
			if (entity.isShiftKeyDown()) {
				pitch = getPitchForStateChange(itemInHand, RemoteState.Type.SHIFT);
			} else {
				pitch = 0.0f;
			}
			
			if (pitch == 0.0f) {
				return false;
			}
			
			level.playSound(
					null,
					entity,
					SoundEvents.UI_BUTTON_CLICK.value(),
					SoundSource.PLAYERS,
					1.0f,
					pitch
			);
		}
		
		return false;
	}
	
	private static float getPitchForStateChange(ItemStack itemInHand, RemoteState.Type state) {
		float pitch;
		if (!itemInHand.has(DataComponents.REMOTE_STATE) || (itemInHand.has(DataComponents.REMOTE_STATE) && !Objects.requireNonNull(itemInHand.get(DataComponents.REMOTE_STATE)).states().contains(state))) {
			pitch = 2.0f;
			itemInHand.set(
					DataComponents.REMOTE_STATE,
					new RemoteState(new HashSet<>(Collections.singleton(state)))
			);
		} else if (itemInHand.has(DataComponents.REMOTE_STATE) && Objects.requireNonNull(itemInHand.get(DataComponents.REMOTE_STATE)).states().contains(state)) {
			pitch = 1.75f;
			Objects.requireNonNull(itemInHand.get(DataComponents.REMOTE_STATE)).states().remove(state);
		} else {
			pitch = 0.0f;
		}
		return pitch;
	}
	
	@Override
	public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
		return false;
	}
	
	public void useBlock(BlockPos pos, Player player, Level level, BlockHitResult result) {
		BlockState blockState = level.getBlockState(pos);
		InteractionHand interactionHand = InteractionHand.MAIN_HAND;
		if (!blockState.useWithoutItem(level, player, result).consumesAction()) {
			var useOnContext = new UseOnContext(player, interactionHand, result);
			player.getItemInHand(interactionHand).useOn(useOnContext);
		}
	}
}
