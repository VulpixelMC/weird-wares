package gay.sylv.weird_wares.impl.util;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;

import java.util.List;

public final class Codecs {
	public static final Codec<List<BlockPos>> POS_LIST = Codec.list(BlockPos.CODEC);
	
	private Codecs() {}
}
