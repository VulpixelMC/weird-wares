package gay.sylv.example_mod.mixin;

import net.minecraft.client.gui.components.LogoRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LogoRenderer.class)
public class LogoRendererMixin {
	@Shadow @Final @Mutable private boolean showEasterEgg;
	
	@Inject(method = "<init>(Z)V", at = @At("TAIL"))
	public void init(boolean bl, CallbackInfo ci) {
		showEasterEgg = true;
	}
}
