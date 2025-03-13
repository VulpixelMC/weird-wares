package gay.sylv.weird_wares.impl.item.component;

import gay.sylv.weird_wares.impl.util.Initializable;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Function;

import static gay.sylv.weird_wares.impl.util.Constants.modId;

@org.jetbrains.annotations.ApiStatus.Internal
public class DataComponents implements Initializable {
	public static final DataComponents INSTANCE = new DataComponents();
	
	public static DataComponentType<RemoteTarget> REMOTE_TARGET;
	
	@Override
	public void initialize() {
		REMOTE_TARGET = register(
				"remote_target",
				builder -> builder
						.persistent(RemoteTarget.CODEC)
						.networkSynchronized(RemoteTarget.STREAM_CODEC)
		);
	}
	
	private static <T> DataComponentType<T> register(String id, Function<DataComponentType.Builder<T>, DataComponentType.Builder<T>> builder) {
		return Registry.register(
				BuiltInRegistries.DATA_COMPONENT_TYPE,
				modId(id),
				builder.apply(DataComponentType.builder()).build()
		);
	}
}
