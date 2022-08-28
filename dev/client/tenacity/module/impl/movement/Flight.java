package dev.client.tenacity.module.impl.movement;

import dev.client.tenacity.module.Category;
import dev.client.tenacity.module.Module;
import dev.client.tenacity.module.impl.combat.TargetStrafe;
import dev.client.tenacity.utils.player.MovementUtils;
import dev.event.EventListener;
import dev.event.impl.network.PacketReceiveEvent;
import dev.event.impl.network.PacketSendEvent;
import dev.event.impl.player.MotionEvent;
import dev.event.impl.player.MoveEvent;
import dev.settings.impl.ModeSetting;
import dev.settings.impl.NumberSetting;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public final class Flight
extends Module {
    private final ModeSetting mode = new ModeSetting("Mode", "Watchdog", "Watchdog", "Vanilla", "AirWalk");
    private final NumberSetting speed = new NumberSetting("Speed", 2.0, 5.0, 0.0, 0.1);
    private float stage;
    private int ticks;
    private boolean doFly;
    private double x;
    private double y;
    private double z;
    private final ArrayList<Packet> packets = new ArrayList();
    private boolean hasClipped;
    private double speedStage;
    private final EventListener<MotionEvent> onMotion = e -> {
        switch (this.mode.getMode()) {
            case "Watchdog": {
                Flight.mc.thePlayer.cameraPitch = 0.05f;
                Flight.mc.thePlayer.cameraYaw = 0.05f;
                Flight.mc.thePlayer.posY = this.y;
                if (Flight.mc.thePlayer.onGround && this.stage == 0.0f) {
                    Flight.mc.thePlayer.motionY = 0.09;
                }
                this.stage += 1.0f;
                if (Flight.mc.thePlayer.onGround && this.stage > 2.0f && !this.hasClipped) {
                    Flight.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Flight.mc.thePlayer.posX, Flight.mc.thePlayer.posY - 0.15, Flight.mc.thePlayer.posZ, false));
                    Flight.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(Flight.mc.thePlayer.posX, Flight.mc.thePlayer.posY + 0.15, Flight.mc.thePlayer.posZ, true));
                    this.hasClipped = true;
                }
                if (this.doFly) {
                    Flight.mc.thePlayer.motionY = 0.0;
                    Flight.mc.thePlayer.onGround = true;
                    Flight.mc.timer.timerSpeed = 2.0f;
                    break;
                }
                MovementUtils.setSpeed(0.0);
                Flight.mc.timer.timerSpeed = 5.0f;
                break;
            }
            case "Vanilla": {
                Flight.mc.thePlayer.motionY = Flight.mc.gameSettings.keyBindJump.isKeyDown() ? this.speed.getValue() : (Flight.mc.gameSettings.keyBindSneak.isKeyDown() ? -this.speed.getValue().doubleValue() : 0.0);
                break;
            }
            case "AirWalk": {
                Flight.mc.thePlayer.motionY = 0.0;
                Flight.mc.thePlayer.onGround = true;
                break;
            }
        }
    };
    private final EventListener<MoveEvent> onMove = e -> {
        if (this.mode.is("Vanilla")) {
            e.setSpeed(MovementUtils.isMoving() ? this.speed.getValue() : 0.0);
        }
        if (this.mode.is("Watchdog")) return;
        TargetStrafe.strafe(e);
    };
    private final EventListener<PacketSendEvent> onPacketSend = e -> {};
    private final EventListener<PacketReceiveEvent> onPacketReceive = e -> {
        if (!this.mode.is("Watchdog")) return;
        if (!(e.getPacket() instanceof S08PacketPlayerPosLook)) return;
        S08PacketPlayerPosLook s08 = (S08PacketPlayerPosLook)e.getPacket();
        this.y = s08.getY();
        this.doFly = true;
    };

    public Flight() {
        super("Flight", Category.MOVEMENT, "Hovers you in the air");
        this.speed.addParent(this.mode, m -> m.is("Vanilla"));
        this.addSettings(this.mode, this.speed);
    }

    @Override
    public void onEnable() {
        this.doFly = false;
        this.ticks = 0;
        this.stage = 0.0f;
        this.x = Flight.mc.thePlayer.posX;
        this.y = Flight.mc.thePlayer.posY;
        this.z = Flight.mc.thePlayer.posZ;
        this.hasClipped = false;
        this.packets.clear();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (this.mode.is("Vanilla")) {
            Flight.mc.thePlayer.motionZ = 0.0;
            Flight.mc.thePlayer.motionY = 0.0;
            Flight.mc.thePlayer.motionX = 0.0;
        }
        Flight.mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }
}