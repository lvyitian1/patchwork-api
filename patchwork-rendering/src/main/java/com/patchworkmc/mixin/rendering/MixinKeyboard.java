package com.patchworkmc.mixin.rendering;

import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Keyboard;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.Screen;

@Mixin(Keyboard.class)
public abstract class MixinKeyboard {
	@Shadow
	private boolean repeatEvents;

	@Inject(method = "method_1454", at = @At("HEAD"), cancellable = true)
	private void preKeyEvent(int i, boolean[] bls, ParentElement element, int key, int scanCode, int j, CallbackInfo info) {
		if (i != 1 && (i != 2 || !this.repeatEvents)) {
			if (i == 0) {
				if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyReleasedEvent.Pre((Screen) element, key, scanCode, j))) {
					bls[0] = true;
					info.cancel();
				}
			}
		} else {
			if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyPressedEvent.Pre((Screen) element, key, scanCode, j))) {
				bls[0] = true;
				info.cancel();
			}
		}
	}

	@Inject(method = "method_1454", at = @At("RETURN"))
	private void postKeyEvent(int i, boolean[] bls, ParentElement element, int key, int scanCode, int j, CallbackInfo info) {
		if (bls[0]) {
			return;
		}

		if (i != 1 && (i != 2 || !this.repeatEvents)) {
			if (i == 0) {
				if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyReleasedEvent.Post((Screen) element, key, scanCode, j))) {
					bls[0] = true;
				}
			}
		} else {
			if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardKeyPressedEvent.Post((Screen) element, key, scanCode, j))) {
				bls[0] = true;
			}
		}
	}

	@Inject(method = "method_1458", at = @At("HEAD"), cancellable = true)
	private static void charEvent(Element element, int i, int j, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Pre((Screen) element, (char) i, j))) {
			info.cancel();
		} else if (!element.charTyped((char) i, j)) {
			MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Post((Screen) element, (char) i, j));
		}
	}

	@Inject(method = "method_1473", at = @At("HEAD"), cancellable = true)
	private static void charEvent(Element element, char i, int j, CallbackInfo info) {
		if (MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Pre((Screen) element, i, j))) {
			info.cancel();
		} else if (!element.charTyped(i, j)) {
			MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.KeyboardCharTypedEvent.Post((Screen) element, i, j));
		}
	}
}
