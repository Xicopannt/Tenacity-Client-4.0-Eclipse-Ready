package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import dev.client.tenacity.Tenacity;
import dev.client.tenacity.config.DragManager;
import dev.client.tenacity.module.impl.render.ArraylistMod;
import dev.client.tenacity.module.impl.render.HudMod;
import dev.client.tenacity.utils.misc.HoveringUtil;
import dev.client.tenacity.utils.objects.Dragging;
import dev.client.tenacity.utils.render.ColorUtil;
import dev.client.tenacity.utils.render.RoundedUtil;
import dev.utils.animations.Animation;
import dev.utils.animations.Direction;
import dev.utils.animations.impl.DecelerateAnimation;
import dev.utils.font.FontUtil;
import java.awt.Color;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class GuiChat
extends GuiScreen {
    private static final Logger logger = LogManager.getLogger();
    private String historyBuffer = "";
    private int sentHistoryCursor = -1;
    private boolean playerNamesFound;
    private boolean waitingOnAutocomplete;
    private int autocompleteIndex;
    private List<String> foundPlayerNames = Lists.newArrayList();
    protected GuiTextField inputField;
    private String defaultInputFieldText = "";
    Animation resetButtonHover;
    public static Animation openingAnimation = new DecelerateAnimation(175, 1.0, Direction.BACKWARDS);
    ArraylistMod arraylistMod = (ArraylistMod)Tenacity.INSTANCE.getModuleCollection().get(ArraylistMod.class);

    public GuiChat() {
    }

    public GuiChat(String defaultText) {
        this.defaultInputFieldText = defaultText;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents((boolean)false);
        this.mc.ingameGUI.getChatGUI().resetScroll();
    }

    @Override
    public void updateScreen() {
        this.inputField.updateCursorCounter();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.waitingOnAutocomplete = false;
        if (keyCode == 15) {
            this.autocompletePlayerNames();
        } else {
            this.playerNamesFound = false;
        }
        if (keyCode == 1) {
            openingAnimation.setDirection(Direction.BACKWARDS);
        } else if (keyCode != 28 && keyCode != 156) {
            if (keyCode == 200) {
                this.getSentHistory(-1);
            } else if (keyCode == 208) {
                this.getSentHistory(1);
            } else if (keyCode == 201) {
                this.mc.ingameGUI.getChatGUI().scroll(this.mc.ingameGUI.getChatGUI().getLineCount() - 1);
            } else if (keyCode == 209) {
                this.mc.ingameGUI.getChatGUI().scroll(-this.mc.ingameGUI.getChatGUI().getLineCount() + 1);
            } else {
                this.inputField.textboxKeyTyped(typedChar, keyCode);
            }
        } else {
            String s = this.inputField.getText().trim();
            if (s.length() > 0) {
                this.sendChatMessage(s);
            }
            openingAnimation.setDirection(Direction.BACKWARDS);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int i = Mouse.getEventDWheel();
        if (i == 0) return;
        if (i > 1) {
            i = 1;
        }
        if (i < -1) {
            i = -1;
        }
        if (!GuiChat.isShiftKeyDown()) {
            i *= 7;
        }
        this.mc.ingameGUI.getChatGUI().scroll(i);
    }

    @Override
    public void initGui() {
        openingAnimation = new DecelerateAnimation(175, 1.0);
        Iterator<Dragging> iterator = DragManager.draggables.values().iterator();
        while (true) {
            if (!iterator.hasNext()) {
                this.resetButtonHover = new DecelerateAnimation(250, 1.0);
                Keyboard.enableRepeatEvents((boolean)true);
                this.sentHistoryCursor = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
                this.inputField = new GuiTextField(0, this.fontRendererObj, 4, this.height - 12, this.width - 4, 12);
                this.inputField.setMaxStringLength(100);
                this.inputField.setEnableBackgroundDrawing(false);
                this.inputField.setFocused(true);
                this.inputField.setText(this.defaultInputFieldText);
                this.inputField.setCanLoseFocus(false);
                return;
            }
            Dragging dragging = iterator.next();
            if (dragging.hoverAnimation.getDirection().equals((Object)Direction.BACKWARDS)) continue;
            dragging.hoverAnimation.setDirection(Direction.BACKWARDS);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (openingAnimation.finished(Direction.BACKWARDS)) {
            this.mc.displayGuiScreen(null);
            return;
        }
        Gui.drawRect2(2.0, (double)this.height - 14.0 * openingAnimation.getOutput(), this.width - 4, 12.0, Integer.MIN_VALUE);
        this.inputField.yPosition = (float)((double)this.height - 12.0 * openingAnimation.getOutput());
        this.inputField.drawTextBox();
        IChatComponent ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY());
        if (ichatcomponent != null && ichatcomponent.getChatStyle().getChatHoverEvent() != null) {
            this.handleComponentHover(ichatcomponent, mouseX, mouseY);
        }
        DragManager.draggables.values().forEach(dragging -> {
            if (!dragging.getModule().isToggled()) return;
            if (dragging.getModule().equals(this.arraylistMod)) {
                dragging.onDrawArraylist(this.arraylistMod, mouseX, mouseY);
            } else {
                dragging.onDraw(mouseX, mouseY);
            }
        });
        HudMod hudMod = (HudMod)Tenacity.INSTANCE.getModuleCollection().get(HudMod.class);
        Color[] colors = hudMod.getClientColors();
        boolean hovering = HoveringUtil.isHovering((float)this.width / 2.0f - 50.0f, 20.0f, 100.0f, 20.0f, mouseX, mouseY);
        this.resetButtonHover.setDirection(hovering ? Direction.FORWARDS : Direction.BACKWARDS);
        float alpha = (float)(0.5 + 0.5 * this.resetButtonHover.getOutput());
        Color color = ColorUtil.interpolateColorsBackAndForth(15, 1, colors[0], colors[1], HudMod.hueInterpolation.isEnabled());
        RoundedUtil.drawRoundOutline((float)this.width / 2.0f - 50.0f, 20.0f, 100.0f, 20.0f, 10.0f, 2.0f, new Color(40, 40, 40, (int)(255.0f * alpha)), color);
        FontUtil.tenacityBoldFont20.drawCenteredString("Reset Draggables", (float)this.width / 2.0f, 20.0f + FontUtil.tenacityBoldFont20.getMiddleOfBox(20.0f), -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        IChatComponent ichatcomponent;
        if (mouseButton == 0 && this.handleComponentClick(ichatcomponent = this.mc.ingameGUI.getChatGUI().getChatComponent(Mouse.getX(), Mouse.getY()))) {
            return;
        }
        this.inputField.mouseClicked(mouseX, mouseY, mouseButton);
        boolean hoveringResetButton = HoveringUtil.isHovering((float)this.width / 2.0f - 100.0f, 20.0f, 200.0f, 20.0f, mouseX, mouseY);
        if (hoveringResetButton && mouseButton == 0) {
            Iterator<Dragging> iterator = DragManager.draggables.values().iterator();
            while (iterator.hasNext()) {
                Dragging dragging2 = iterator.next();
                dragging2.setX(dragging2.initialXVal);
                dragging2.setY(dragging2.initialYVal);
            }
            return;
        }
        DragManager.draggables.values().forEach(dragging -> {
            if (!dragging.getModule().isToggled()) return;
            if (dragging.getModule().equals(this.arraylistMod)) {
                dragging.onClickArraylist(this.arraylistMod, mouseX, mouseY, mouseButton);
            } else {
                dragging.onClick(mouseX, mouseY, mouseButton);
            }
        });
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        DragManager.draggables.values().forEach(dragging -> {
            if (!dragging.getModule().isToggled()) return;
            dragging.onRelease(state);
        });
    }

    @Override
    protected void setText(String newChatText, boolean shouldOverwrite) {
        if (shouldOverwrite) {
            this.inputField.setText(newChatText);
        } else {
            this.inputField.writeText(newChatText);
        }
    }

    public void autocompletePlayerNames() {
        if (this.playerNamesFound) {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            if (this.autocompleteIndex >= this.foundPlayerNames.size()) {
                this.autocompleteIndex = 0;
            }
        } else {
            int i = this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false);
            this.foundPlayerNames.clear();
            this.autocompleteIndex = 0;
            String s = this.inputField.getText().substring(i).toLowerCase();
            String s1 = this.inputField.getText().substring(0, this.inputField.getCursorPosition());
            this.sendAutocompleteRequest(s1, s);
            if (this.foundPlayerNames.isEmpty()) {
                return;
            }
            this.playerNamesFound = true;
            this.inputField.deleteFromCursor(i - this.inputField.getCursorPosition());
        }
        if (this.foundPlayerNames.size() > 1) {
            StringBuilder stringbuilder = new StringBuilder();
            for (String s2 : this.foundPlayerNames) {
                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }
                stringbuilder.append(s2);
            }
            this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new ChatComponentText(stringbuilder.toString()), 1);
        }
        this.inputField.writeText(this.foundPlayerNames.get(this.autocompleteIndex++));
    }

    private void sendAutocompleteRequest(String p_146405_1_, String p_146405_2_) {
        if (p_146405_1_.length() < 1) return;
        BlockPos blockpos = null;
        if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            blockpos = this.mc.objectMouseOver.getBlockPos();
        }
        this.mc.thePlayer.sendQueue.addToSendQueue(new C14PacketTabComplete(p_146405_1_, blockpos));
        this.waitingOnAutocomplete = true;
    }

    public void getSentHistory(int msgPos) {
        int i = this.sentHistoryCursor + msgPos;
        int j = this.mc.ingameGUI.getChatGUI().getSentMessages().size();
        if ((i = MathHelper.clamp_int(i, 0, j)) == this.sentHistoryCursor) return;
        if (i == j) {
            this.sentHistoryCursor = j;
            this.inputField.setText(this.historyBuffer);
        } else {
            if (this.sentHistoryCursor == j) {
                this.historyBuffer = this.inputField.getText();
            }
            this.inputField.setText(this.mc.ingameGUI.getChatGUI().getSentMessages().get(i));
            this.sentHistoryCursor = i;
        }
    }

    public void onAutocompleteResponse(String[] p_146406_1_) {
        if (!this.waitingOnAutocomplete) return;
        this.playerNamesFound = false;
        this.foundPlayerNames.clear();
        for (String s : p_146406_1_) {
            if (s.length() <= 0) continue;
            this.foundPlayerNames.add(s);
        }
        String s1 = this.inputField.getText().substring(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false));
        String s2 = StringUtils.getCommonPrefix(p_146406_1_);
        if (s2.length() > 0 && !s1.equalsIgnoreCase(s2)) {
            this.inputField.deleteFromCursor(this.inputField.func_146197_a(-1, this.inputField.getCursorPosition(), false) - this.inputField.getCursorPosition());
            this.inputField.writeText(s2);
        } else {
            if (this.foundPlayerNames.size() <= 0) return;
            this.playerNamesFound = true;
            this.autocompletePlayerNames();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}