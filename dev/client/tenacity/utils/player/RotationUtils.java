package dev.client.tenacity.utils.player;

import dev.client.tenacity.utils.player.ScaffoldUtils;
import dev.utils.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RotationUtils
implements Utils {
    public static void setRotations(float yaw, float pitch) {
        RotationUtils.mc.thePlayer.rotationYawHead = RotationUtils.mc.thePlayer.renderYawOffset = yaw;
        RotationUtils.mc.thePlayer.rotationPitchHead = pitch;
    }

    public static float clampRotation() {
        float rotationYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        float n = 1.0f;
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward < 0.0f) {
            rotationYaw += 180.0f;
            n = -0.5f;
        } else if (Minecraft.getMinecraft().thePlayer.movementInput.moveForward > 0.0f) {
            n = 0.5f;
        }
        if (Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe > 0.0f) {
            rotationYaw -= 90.0f * n;
        }
        if (!(Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe < 0.0f)) return rotationYaw * ((float)Math.PI / 180);
        rotationYaw += 90.0f * n;
        return rotationYaw * ((float)Math.PI / 180);
    }

    public static float getSensitivityMultiplier() {
        float SENSITIVITY = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6f + 0.2f;
        return SENSITIVITY * SENSITIVITY * SENSITIVITY * 8.0f * 0.15f;
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapAngleTo180_float(to - from);
        if (f > speed) {
            f = speed;
        }
        if (!(f < -speed)) return from + f;
        f = -speed;
        return from + f;
    }

    public static void setRotations(float[] rotations) {
        RotationUtils.setRotations(rotations[0], rotations[1]);
    }

    public static float[] getFacingRotations(ScaffoldUtils.BlockCache blockCache) {
        double d1 = (double)blockCache.position.getX() + 0.5 - RotationUtils.mc.thePlayer.posX + (double)blockCache.facing.getFrontOffsetX() / 2.0;
        double d2 = (double)blockCache.position.getZ() + 0.5 - RotationUtils.mc.thePlayer.posZ + (double)blockCache.facing.getFrontOffsetZ() / 2.0;
        double d3 = RotationUtils.mc.thePlayer.posY + (double)RotationUtils.mc.thePlayer.getEyeHeight() - (double)blockCache.position.getY();
        double d4 = MathHelper.sqrt_double(d1 * d1 + d2 * d2);
        float f1 = (float)(Math.atan2(d2, d1) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float)(Math.atan2(d3, d4) * 180.0 / Math.PI);
        if (!(f1 < 0.0f)) return new float[]{f1, f2};
        f1 += 360.0f;
        return new float[]{f1, f2};
    }

    public static float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        }
        Minecraft mc = Minecraft.getMinecraft();
        double xSize = entity.posX - mc.thePlayer.posX;
        double ySize = entity.posY + (double)(entity.getEyeHeight() / 2.0f) - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
        double zSize = entity.posZ - mc.thePlayer.posZ;
        double theta = MathHelper.sqrt_double(xSize * xSize + zSize * zSize);
        float yaw = (float)(Math.atan2(zSize, xSize) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(Math.atan2(ySize, theta) * 180.0 / Math.PI));
        return new float[]{(mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw)) % 360.0f, (mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)) % 360.0f};
    }

    public static float[] getFacingRotations2(int paramInt1, double d, int paramInt3) {
        EntitySnowball localEntityPig = new EntitySnowball(Minecraft.getMinecraft().theWorld);
        localEntityPig.posX = (double)paramInt1 + 0.5;
        localEntityPig.posY = d + 0.5;
        localEntityPig.posZ = (double)paramInt3 + 0.5;
        return RotationUtils.getRotationsNeeded(localEntityPig);
    }

    public static float getYaw(Vec3 to) {
        float x = (float)(to.xCoord - RotationUtils.mc.thePlayer.posX);
        float z = (float)(to.zCoord - RotationUtils.mc.thePlayer.posZ);
        float var1 = (float)(StrictMath.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float rotationYaw = RotationUtils.mc.thePlayer.rotationYaw;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }

    public static Vec3 getVecRotations(float yaw, float pitch) {
        double d = Math.cos(Math.toRadians(-yaw) - Math.PI);
        double d1 = Math.sin(Math.toRadians(-yaw) - Math.PI);
        double d2 = -Math.cos(Math.toRadians(-pitch));
        double d3 = Math.sin(Math.toRadians(-pitch));
        return new Vec3(d1 * d2, d3, d * d2);
    }

    public static float[] getRotations(double posX, double posY, double posZ) {
        double x = posX - RotationUtils.mc.thePlayer.posX;
        double z = posZ - RotationUtils.mc.thePlayer.posZ;
        double y = posY + (double)RotationUtils.mc.thePlayer.getEyeHeight() - RotationUtils.mc.thePlayer.posY;
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(MathHelper.atan2(z, x) * 180.0 / Math.PI) - 90.0f;
        float pitch = (float)(-(MathHelper.atan2(y, d3) * 180.0 / Math.PI));
        return new float[]{yaw, pitch};
    }
}