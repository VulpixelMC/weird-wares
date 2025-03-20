/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Codecs {
	public static final Codec<Set<BlockPos>> POS_LIST = Codec.list(BlockPos.CODEC).xmap(HashSet::new, ArrayList::new);
	
	private Codecs() {}
	
	public static <T> Codec<Set<T>> set(Codec<T> elementCodec) {
		Codec<List<T>> listCodec = Codec.list(elementCodec);
		return Codec.of(
				new Encoder<>() {
					@Override
					public <U> DataResult<U> encode(Set<T> input, DynamicOps<U> ops, U prefix) {
						return listCodec.encode(List.copyOf(input), ops, prefix);
					}
				},
				new Decoder<>() {
					@Override
					public <U> DataResult<Pair<Set<T>, U>> decode(DynamicOps<U> ops, U input) {
						return listCodec.decode(ops, input).map((a) -> Pair.of(new HashSet<>(a.getFirst()), a.getSecond()));
					}
				}
		);
	}
}
