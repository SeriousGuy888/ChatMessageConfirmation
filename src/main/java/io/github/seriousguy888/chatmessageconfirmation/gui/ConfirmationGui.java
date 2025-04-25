package io.github.seriousguy888.chatmessageconfirmation.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.seriousguy888.chatmessageconfirmation.ChatMessageConfirmationMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ConfirmationGui extends LightweightGuiDescription {
    private static final String CONFIRMATION_FOR_MESSAGES = "gui.chat_message_confirmation.are_you_sure.chat_message";
    private static final String CONFIRMATION_FOR_COMMANDS = "gui.chat_message_confirmation.are_you_sure.command";
    private static final String BUTTON_CONFIRM = "gui.chat_message_confirmation.button.confirm";
    private static final String BUTTON_CANCEL = "gui.chat_message_confirmation.button.cancel";

    public ConfirmationGui(String messageContents, boolean isCommand) {
        int cellSize = 18;
        int gap = 2;
        int gridWidth = 12;
        int gridHeight = 7;

        int quoteColour = 0x000000;
        int quoteColourDarkMode = 0xffffff;


        WGridPanel root = new WGridPanel(cellSize);
        setRootPanel(root);

        root.setSize(cellSize * gridWidth, cellSize * gridHeight);
        root.setGaps(gap, gap);
        root.setInsets(Insets.ROOT_PANEL);

        WLabel label = new WLabel(Text.translatable(isCommand ? CONFIRMATION_FOR_COMMANDS : CONFIRMATION_FOR_MESSAGES));
        root.add(label, 0, 0, gridWidth, 1);

        WText textField = new WText(Text.literal((isCommand ? "/" : "") + messageContents));
        textField.setColor(quoteColour, quoteColourDarkMode);
        root.add(textField, 1, 1, gridWidth - 2, gridHeight - 2);

        MinecraftClient client = MinecraftClient.getInstance();
        WButton yesButton = new WButton(Text.translatable(BUTTON_CONFIRM)).setOnClick(() -> {
            ChatMessageConfirmationMod.getInstance().sendPendingMessage(messageContents, isCommand);
            client.send(() -> client.setScreen(null));
        });
        WButton noButton = new WButton(Text.translatable(BUTTON_CANCEL)).setOnClick(() -> {
            client.send(() -> client.setScreen(null));
        });
        root.add(yesButton, 0, gridHeight - 1, gridWidth / 2, 1);
        root.add(noButton, gridWidth / 2, gridHeight - 1, gridWidth / 2, 1);


        root.validate(this);
    }
}
