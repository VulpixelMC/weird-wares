package gay.sylv.weird_wares.impl.block;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class NetherReactor extends Block {
	public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);
	
	public NetherReactor(Properties properties) {
		super(properties);
		
		registerDefaultState(defaultBlockState().setValue(STATE, State.INACTIVE));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(STATE);
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
