package gay.sylv.weird_wares.impl.item;

import gay.sylv.weird_wares.impl.item.component.DataComponents;
import gay.sylv.weird_wares.impl.item.component.RemoteTarget;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@org.jetbrains.annotations.ApiStatus.Internal
public class SculkRemoteItem extends Item {
	public SculkRemoteItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
		if (stack.has(DataComponents.REMOTE_TARGET)) {
			RemoteTarget remoteTarget = stack.get(DataComponents.REMOTE_TARGET);
			assert remoteTarget != null;
			tooltipComponents.add(
					Component.translatable(
							"tooltip.weird-wares.item.sculk_remote.dimension",
							remoteTarget.dimension().location()
					).withStyle(ChatFormatting.DARK_GRAY)
			);
			tooltipComponents.add(
					Component.translatable(
							"tooltip.weird-wares.item.sculk_remote.position",
							remoteTarget.pos().getX(),
							remoteTarget.pos().getY(),
							remoteTarget.pos().getZ()
					).withStyle(ChatFormatting.DARK_GRAY)
			);
		}
	}
	
	@Override
	public @NotNull InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		
		if (!level.isClientSide() && player != null && player.isShiftKeyDown()) {
			context.getItemInHand().set(
					DataComponents.REMOTE_TARGET,
					new RemoteTarget(level.dimension(), context.getClickedPos())
			);
			level.playSound(
					null,
					player,
					SoundEvents.UI_BUTTON_CLICK.value(),
					SoundSource.PLAYERS,
					1.0f,
					1.0f
			);
		}
		
		return super.useOn(context);
	}
	
	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
		ItemStack stack = player.getItemInHand(usedHand);
		if (!level.isClientSide() && !player.isShiftKeyDown() && stack.has(DataComponents.REMOTE_TARGET)) {
			RemoteTarget remoteTarget = stack.get(DataComponents.REMOTE_TARGET);
			assert remoteTarget != null;
			assert level.getServer() != null;
			Level remoteLevel = level.getServer().getLevel(remoteTarget.dimension());
			assert remoteLevel != null;
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
			level.playSound(
					null,
					remoteTarget.pos(),
					SoundEvents.SCULK_CLICKING,
					SoundSource.PLAYERS,
					1.0f,
					1.0f
			);
		}
		
		return super.use(level, player, usedHand);
	}
	
	public void useBlock(BlockPos pos, Player player, Level level, BlockHitResult result) {
		BlockState blockState = level.getBlockState(pos);
		InteractionHand interactionHand = InteractionHand.MAIN_HAND;
		ItemInteractionResult itemInteractionResult = blockState.useItemOn(player.getItemInHand(interactionHand), level, player, interactionHand, result);
		if (!itemInteractionResult.consumesAction()) {
			if (itemInteractionResult != ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION
					|| !blockState.useWithoutItem(level, player, result).consumesAction()) {
				UseOnContext useOnContext = new UseOnContext(player, interactionHand, result);
				player.getItemInHand(interactionHand).useOn(useOnContext);
			}
		}
	}
}
