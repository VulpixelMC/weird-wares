package gay.sylv.weird_wares.impl.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

@org.jetbrains.annotations.ApiStatus.Internal
public class NetherReactorBlock extends BaseEntityBlock {
	public static final MapCodec<NetherReactorBlock> CODEC = simpleCodec(NetherReactorBlock::new);
	public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
	
	public NetherReactorBlock(Properties properties) {
		super(properties);
		
		registerDefaultState(defaultBlockState().setValue(STATE, State.INACTIVE));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STATE);
	}
	
	@Override
	protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return null;
	}
	
	@Override
	public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
		super.playerDestroy(level, player, pos, state, blockEntity, tool);
	}
	
	@org.jetbrains.annotations.ApiStatus.Internal
	public static class NetherReactorBlockEntity extends BlockEntity {
		public NetherReactorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
			super(type, pos, blockState);
		}
	}
	
	public enum State implements StringRepresentable {
		INACTIVE,
		ACTIVE,
		USED;
		
		@Override
		public @NotNull String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
