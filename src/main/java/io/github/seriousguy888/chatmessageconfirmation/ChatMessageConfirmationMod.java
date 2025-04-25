package io.github.seriousguy888.chatmessageconfirmation;

import io.github.seriousguy888.chatmessageconfirmation.gui.ConfirmationGui;
import io.github.seriousguy888.chatmessageconfirmation.gui.ConfirmationScreen;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatMessageConfirmationMod implements ModInitializer {
    private static ChatMessageConfirmationMod instance;
    public static final String MOD_ID = "chat_message_confirmation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private MinecraftClient client;

    private static final String BYPASS_PREFIX = "#";
    private boolean nextMessageShouldBypass = false;

    public static ChatMessageConfirmationMod getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;
        client = MinecraftClient.getInstance();

        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.startsWith(BYPASS_PREFIX) || nextMessageShouldBypass) {
                nextMessageShouldBypass = false;
                return true;
            }

            client.send(() -> client.setScreen(new ConfirmationScreen(new ConfirmationGui(message, false))));

            return false;
        });

        ClientSendMessageEvents.ALLOW_COMMAND.register(command -> {
            if (nextMessageShouldBypass) {
                nextMessageShouldBypass = false;
                return true;
            }

            client.send(() -> client.setScreen(new ConfirmationScreen(new ConfirmationGui(command, true))));
            return false;
        });

        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            if (message.startsWith(BYPASS_PREFIX)) {
                return message.substring(BYPASS_PREFIX.length());
            } else {
                return message;
            }
        });
    }

    public void sendPendingMessage(String messageOrCommand, boolean isCommand) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            LOGGER.warn("Attempted to send pending message/command when there is no ClientPlayerEntity.");
            return;
        }

        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) {
            LOGGER.warn("Attempted to send pending message/command when there is no ClientPlayNetworkHandler.");
            return;
        }

        client.send(() -> {
            nextMessageShouldBypass = true;

            if (isCommand) {
                networkHandler.sendChatCommand(messageOrCommand);
            } else {
                networkHandler.sendChatMessage(messageOrCommand);
            }
        });
    }
}