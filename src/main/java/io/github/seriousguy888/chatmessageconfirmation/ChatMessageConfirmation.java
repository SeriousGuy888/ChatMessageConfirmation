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

public class ChatMessageConfirmation implements ModInitializer {
    private static ChatMessageConfirmation instance;

    public static final String MOD_ID = "chat-message-confirmation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String BYPASS_PREFIX = "#";

    private MinecraftClient client;

    public static ChatMessageConfirmation getInstance() {
        return instance;
    }

    @Override
    public void onInitialize() {
        instance = this;
        client = MinecraftClient.getInstance();

        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.startsWith(BYPASS_PREFIX)) {
                return true;
            }

            client.send(() -> client.setScreen(new ConfirmationScreen(new ConfirmationGui(message))));

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

    public void sendPendingMessage(String pendingMessage) {
        ClientPlayerEntity player = client.player;
        if (player == null) {
            LOGGER.warn("Attempted to send pending chat message when there is no ClientPlayerEntity.");
            return;
        }

        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) {
            LOGGER.warn("Attempted to send pending chat message when there is no ClientPlayNetworkHandler.");
            return;
        }

        client.send(() -> {
            networkHandler.sendChatMessage(BYPASS_PREFIX + pendingMessage);
        });
    }
}