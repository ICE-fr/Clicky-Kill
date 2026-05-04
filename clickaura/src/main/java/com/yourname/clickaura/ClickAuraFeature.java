package com.yourname.clickaura;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
public class ClickAuraFeature {
    public boolean enabled = false;
    public double range = 4.25;
    public Priority priority = Priority.DISTANCE;
    public enum Priority { DISTANCE, ANGLE, HEALTH }
    public void cyclePriority() {
        Priority[] v = Priority.values();
        priority = v[(priority.ordinal() + 1) % v.length];
    }
    public void onLeftClick() {
        if (!enabled) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || mc.interactionManager == null) return;
        PlayerEntity player = mc.player;
        Vec3d eyePos = player.getEyePos();
        List<LivingEntity> targets = mc.world
            .getEntitiesByClass(LivingEntity.class,
                new Box(player.getBlockPos()).expand(range + 1),
                e -> e != player && e.isAlive() && player.distanceTo(e) <= range)
            .stream().sorted(getComparator(player, eyePos)).collect(Collectors.toList());
        if (targets.isEmpty()) return;
        LivingEntity target = targets.get(0);
        lookAt(player, target);
        mc.interactionManager.attackEntity(player, target);
        player.swingHand(Hand.MAIN_HAND);
    }
    private Comparator<LivingEntity> getComparator(PlayerEntity player, Vec3d eyePos) {
        return switch (priority) {
            case DISTANCE -> Comparator.comparingDouble(player::distanceTo);
            case HEALTH   -> Comparator.comparingDouble(LivingEntity::getHealth);
            case ANGLE    -> Comparator.comparingDouble(e -> getAngleTo(eyePos, player, e));
        };
    }
    private double getAngleTo(Vec3d eyePos, PlayerEntity player, Entity target) {
        Vec3d toTarget = target.getEyePos().subtract(eyePos).normalize();
        Vec3d lookVec = player.getRotationVec(1.0f);
        return lookVec.distanceTo(toTarget);
    }
    private void lookAt(PlayerEntity player, Entity target) {
        Vec3d diff = target.getEyePos().subtract(player.getEyePos());
        double h = Math.sqrt(diff.x * diff.x + diff.z * diff.z);
        player.setYaw((float)(Math.toDegrees(Math.atan2(diff.z, diff.x))) - 90f);
        player.setPitch((float)(-Math.toDegrees(Math.atan2(diff.y, h))));
    }
}
