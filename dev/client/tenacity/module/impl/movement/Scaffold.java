package dev.client.tenacity.module.impl.movement;

import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.utils.player.RotationUtils;
import dev.client.tenacity.utils.player.ScaffoldUtils;
import dev.event.EventListener;
import dev.event.impl.player.MotionEvent;
import dev.settings.impl.BooleanSetting;
import dev.settings.impl.ModeSetting;
import dev.settings.impl.NumberSetting;
import dev.utils.time.TimerUtil;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;

public class Scaffold
extends Module {
    private ScaffoldUtils.BlockCache blockCache;
    private ScaffoldUtils.BlockCache lastBlockCache;
    private final ModeSetting placetype = new ModeSetting("Place Type", "Post", "Pre", "Post");
    public static NumberSetting extend = new NumberSetting("Extend", 0.0, 6.0, 0.0, 0.01);
    public static BooleanSetting sprint = new BooleanSetting("Sprint", false);
    private final BooleanSetting tower = new BooleanSetting("Tower", false);
    private final NumberSetting towerTimer = new NumberSetting("Tower Timer Boost", 1.2, 5.0, 0.1, 0.1);
    private final BooleanSetting swing = new BooleanSetting("Swing", false);
    private float[] rotations;
    private final TimerUtil timer = new TimerUtil();
    private final EventListener<MotionEvent> onMotion = e -> {
        if (e.isPre()) {
            if (this.lastBlockCache != null) {
                this.rotations = RotationUtils.getFacingRotations2(this.lastBlockCache.getPosition().getX(), this.lastBlockCache.getPosition().getY(), this.lastBlockCache.getPosition().getZ());
                Scaffold.mc.thePlayer.renderYawOffset = this.rotations[0];
                Scaffold.mc.thePlayer.rotationYawHead = this.rotations[0];
                e.setYaw(this.rotations[0]);
                e.setPitch(81.0f);
                Scaffold.mc.thePlayer.rotationPitchHead = 81.0f;
            } else {
                e.setPitch(81.0f);
                e.setYaw(Scaffold.mc.thePlayer.rotationYaw + 180.0f);
                Scaffold.mc.thePlayer.rotationPitchHead = 81.0f;
                Scaffold.mc.thePlayer.renderYawOffset = Scaffold.mc.thePlayer.rotationYaw + 180.0f;
                Scaffold.mc.thePlayer.rotationYawHead = Scaffold.mc.thePlayer.rotationYaw + 180.0f;
            }
            if (Scaffold.mc.thePlayer.isPotionActive(Potion.moveSpeed.id)) {
                Scaffold.mc.thePlayer.motionX *= 0.66;
                Scaffold.mc.thePlayer.motionZ *= 0.66;
            }
            this.blockCache = ScaffoldUtils.grab();
            if (this.blockCache == null) return;
            this.lastBlockCache = ScaffoldUtils.grab();
            int slot = ScaffoldUtils.grabBlockSlot();
            if (slot == -1) {
                return;
            }
            Scaffold.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(slot));
            if (!this.placetype.getMode().equalsIgnoreCase("Pre")) return;
            if (this.blockCache == null) {
                return;
            }
            Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.inventory.getStackInSlot(slot), this.lastBlockCache.position, this.lastBlockCache.facing, ScaffoldUtils.getHypixelVec3(this.lastBlockCache));
            if (this.swing.isEnabled()) {
                Scaffold.mc.thePlayer.swingItem();
            }
            Scaffold.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            this.blockCache = null;
        } else {
            int slot;
            if (this.tower.isEnabled()) {
                if (Scaffold.mc.gameSettings.keyBindJump.isKeyDown()) {
                    Scaffold.mc.timer.timerSpeed = this.towerTimer.getValue().floatValue();
                    if (Scaffold.mc.thePlayer.motionY < 0.0) {
                        Scaffold.mc.thePlayer.jump();
                    }
                } else {
                    Scaffold.mc.timer.timerSpeed = 1.0f;
                }
            }
            if ((slot = ScaffoldUtils.grabBlockSlot()) == -1) {
                return;
            }
            if (!this.placetype.getMode().equalsIgnoreCase("Post")) return;
            if (this.blockCache == null) {
                return;
            }
            Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.inventory.getStackInSlot(slot), this.lastBlockCache.position, this.lastBlockCache.facing, ScaffoldUtils.getHypixelVec3(this.lastBlockCache));
            if (this.swing.isEnabled()) {
                Scaffold.mc.thePlayer.swingItem();
            }
            Scaffold.mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
            this.blockCache = null;
        }
    };

    public Scaffold() {
        super("Scaffold", Category.MOVEMENT, "Automatically places blocks under you");
        this.addSettings(this.placetype, extend, sprint, this.tower, this.towerTimer, this.swing);
        this.towerTimer.addParent(this.tower, mode -> mode.isEnabled());
    }

    @Override
    public void onDisable() {
        Scaffold.mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(Scaffold.mc.thePlayer.inventory.currentItem));
        super.onDisable();
    }

    @Override
    public void onEnable() {
        this.lastBlockCache = null;
        super.onEnable();
    }
}