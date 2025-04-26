package io.github.seriousguy888.chatmessageconfirmation.config;

import io.github.seriousguy888.chatmessageconfirmation.ChatMessageConfirmationMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
    private static final Text TITLE_TEXT = Text.translatable("options.chat_message_confirmation.title");
    private static final Text ALLOW_BYPASS_TEXT = Text.translatable("options.chat_message_confirmation.allow_bypass");
    private static final Text BYPASS_PREFIX_TEXT = Text.translatable("options.chat_message_confirmation.bypass_prefix");
    private static final Text DONE_TEXT = Text.translatable("gui.done");

    private static final int COLOUR_WHITE = 0xffffff;
    private static final int COLOUR_GRAY = 0xa0a0a0;

    private TextFieldWidget bypassPrefixField;
    private ButtonWidget doneButton;
    private final Screen parent;

    private final int WIDGET_WIDTH = 200;
    private int widgetXToCenter = this.width / 2 - WIDGET_WIDTH / 2;

    public ConfigScreen(Screen parent) {
        super(TITLE_TEXT);
        this.parent = parent;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.doneButton.active || this.getFocused() != this.bypassPrefixField || keyCode != 257 && keyCode != 335) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.close();
            return true;
        }
    }

    protected void init() {
        int widgetHeight = 20;
        widgetXToCenter = this.width / 2 - WIDGET_WIDTH / 2;

        // The button to toggle allowing confirmation bypassing
        this.addDrawableChild(
                ButtonWidget.builder(getAllowBypassText(),
                                button -> {
                                    ChatMessageConfirmationMod.CONFIG.ALLOW_BYPASS = !ChatMessageConfirmationMod.CONFIG.ALLOW_BYPASS;
                                    button.setMessage(getAllowBypassText());
                                })
                        .dimensions(widgetXToCenter, height / 3 - 12, WIDGET_WIDTH, widgetHeight)
                        .build());

        // This field allows the user to change what the bypass prefix is
        this.bypassPrefixField = new TextFieldWidget(this.textRenderer,
                this.width / 2,
                height / 3 + 12,
                WIDGET_WIDTH / 2,
                widgetHeight,
                BYPASS_PREFIX_TEXT);
        this.bypassPrefixField.setMaxLength(128);
        this.bypassPrefixField.setText(ChatMessageConfirmationMod.CONFIG.BYPASS_PREFIX);
        this.bypassPrefixField.setChangedListener((text) ->
                ChatMessageConfirmationMod.CONFIG.BYPASS_PREFIX = bypassPrefixField.getText());
        this.addSelectableChild(this.bypassPrefixField);

        this.doneButton = this.addDrawableChild(
                ButtonWidget.builder(DONE_TEXT,
                                button -> this.close())
                        .dimensions(widgetXToCenter, 2 * this.height / 3, WIDGET_WIDTH, widgetHeight)
                        .build());
    }

    private Text getAllowBypassText() {
        return Text.translatable(
                "options." + (ChatMessageConfirmationMod.CONFIG.ALLOW_BYPASS ? "on" : "off") + ".composed",
                ALLOW_BYPASS_TEXT);
    }

    public void resize(MinecraftClient client, int width, int height) {
        this.init(client, width, height);
    }

    public void close() {
        if (this.client != null) {
            this.client.setScreen(this.parent);
        } else {
            ChatMessageConfirmationMod.LOGGER.error("Client is not set???? Can't exit config screen.");
        }
    }

    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);

        // draw the menu title at the top of the screen
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, COLOUR_WHITE);

        // draw a label next to the bypass prefix text field
        context.drawTextWithShadow(this.textRenderer,
                BYPASS_PREFIX_TEXT,
                widgetXToCenter + 1,
                height / 3 + 12 + 6, // add 6px because text field is 20px and MC font is 7px tall
                COLOUR_GRAY);

        this.bypassPrefixField.render(context, mouseX, mouseY, deltaTicks);
    }
}
