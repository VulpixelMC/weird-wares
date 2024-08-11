/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.util;

@org.jetbrains.annotations.ApiStatus.Internal
public final class BlockFlags {
	public static final int BLOCK_UPDATE = 1;
	public static final int SYNC = 2;
	public static final int BAKE = 4;
	public static final int RENDER_MAIN_THREAD = 8;
	public static final int PREVENT_NEIGHBOR_REACTIONS = 16;
	public static final int PREVENT_NEIGHBOR_LOOT_DROPS = 32;
	public static final int BLOCK_MOVE = 64;
	
	public static final int DEFAULT = BLOCK_UPDATE | SYNC;
	public static final int STATE_CHANGE = SYNC;
	
	private BlockFlags() {}
}
