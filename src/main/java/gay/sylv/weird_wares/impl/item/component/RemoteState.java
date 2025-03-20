/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gay.sylv.weird_wares.impl.util.Codecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.IntFunction;

@org.jetbrains.annotations.ApiStatus.Internal
public record RemoteState(@NotNull Set<Type> states) {
	public static final Codec<RemoteState> CODEC = RecordCodecBuilder.create(instance -> 
			instance.group(
					Codecs.set(StringRepresentable.fromValues(Type::values))
							.fieldOf("states")
							.forGetter(RemoteState::states)
			).apply(instance, RemoteState::new)
	);
	
	public static StreamCodec<ByteBuf, Type> TYPE_STREAM_CODEC = ByteBufCodecs.idMapper(Type.BY_ID, Type::id);
	
	public static final StreamCodec<FriendlyByteBuf, RemoteState> STREAM_CODEC = StreamCodec.of(
			(buf, state) -> buf.writeCollection(state.states(), TYPE_STREAM_CODEC),
			(buf) -> new RemoteState(buf.<Type, Set<Type>>readCollection(HashSet::new, TYPE_STREAM_CODEC))
	);
	
	public enum Type implements StringRepresentable {
		SHIFT(0, "shift");
		
		public static final IntFunction<Type> BY_ID = ByIdMap.continuous(type -> type.id, Type.values(), ByIdMap.OutOfBoundsStrategy.ZERO);
		private final String name;
		private final int id;
		
		Type(int id, String name) {
			this.name = name;
			this.id = id;
		}
		
		@Override
		public @NotNull String getSerializedName() {
			return this.name;
		}
		
		public int id() {
			return id;
		}
	}
}
