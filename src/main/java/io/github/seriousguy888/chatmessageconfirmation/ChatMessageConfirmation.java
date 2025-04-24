package io.github.seriousguy888.chatmessageconfirmation;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatMessageConfirmation implements ModInitializer {
    public static final String MOD_ID = "chat-message-confirmation";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final String BYPASS_PREFIX = "#";

    @Override
    public void onInitialize() {
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            return message.startsWith(BYPASS_PREFIX);
        });

        ClientSendMessageEvents.MODIFY_CHAT.register(message -> {
            if (message.startsWith(BYPASS_PREFIX)) {
                return message.substring(BYPASS_PREFIX.length());
            } else {
                return message;
            }
        });
    }
}