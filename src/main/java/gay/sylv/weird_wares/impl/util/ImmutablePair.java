/**
 * Copyright (c) 2024 Vulpixel
 * <p>
 * For more information, read the LICENSE file in the project root.
 * You should have received a copy of the Lambda License with The Software.
 * If not, visit {@link https://sylv.gay/licenses/lambda}.
 */
package gay.sylv.weird_wares.impl.util;

@org.jetbrains.annotations.ApiStatus.Internal
public class ImmutablePair<T, U> {
	protected T first;
	protected U second;
	
	public ImmutablePair(T first, U second) {
		this.first = first;
		this.second = second;
	}
	
	public T first() {
		return first;
	}
	
	public U second() {
		return second;
	}
}
