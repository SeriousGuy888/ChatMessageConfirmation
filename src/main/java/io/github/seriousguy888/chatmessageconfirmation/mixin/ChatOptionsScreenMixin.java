package io.github.seriousguy888.chatmessageconfirmation.mixin;

import io.github.seriousguy888.chatmessageconfirmation.ChatMessageConfirmationMod;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.ChatOptionsScreen;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatOptionsScreen.class)
public abstract class ChatOptionsScreenMixin extends GameOptionsScreen {
    public ChatOptionsScreenMixin(Screen parent, GameOptions gameOptions, Text title) {
        super(parent, gameOptions, title);
    }

    @Unique
    private static final String ALLOW_BYPASS_OPTION_NAME = "options.chat_message_confirmation.allow_bypass";

    @Unique
    private static final SimpleOption<Boolean> ALLOW_BYPASS = SimpleOption.ofBoolean(
            ALLOW_BYPASS_OPTION_NAME,
            ChatMessageConfirmationMod.CONFIG.ALLOW_BYPASS,
            newValue -> {
                ChatMessageConfirmationMod.CONFIG.ALLOW_BYPASS = newValue;
                ChatMessageConfirmationMod.CONFIG.save();
            });

    @Inject(method = "addOptions", at = @At("HEAD"))
    private void addOptions(CallbackInfo ci) {
        if(this.body != null) {
            this.body.addSingleOptionEntry(ALLOW_BYPASS);
        }
    }
}
