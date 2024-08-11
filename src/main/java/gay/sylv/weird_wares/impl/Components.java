/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl;

import gay.sylv.weird_wares.impl.util.Initializable;

@org.jetbrains.annotations.ApiStatus.Internal
public final class Components implements Initializable {
	public static final Components INSTANCE = new Components();
	
	private Components() {}
}
