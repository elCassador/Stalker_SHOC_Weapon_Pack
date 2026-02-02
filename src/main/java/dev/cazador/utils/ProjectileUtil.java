package dev.cazador.utils;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.projectile.ProjectileModule;
import com.hypixel.hytale.server.core.modules.projectile.config.ImpactConsumer;
import com.hypixel.hytale.server.core.modules.projectile.config.ProjectileConfig;
import com.hypixel.hytale.server.core.modules.projectile.config.StandardPhysicsProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import java.lang.reflect.Field;

public class ProjectileUtil {

    /** Высота вылета снаряда относительно позиции сущности (примерно уровень груди/плеча) */
    private static final double MUZZLE_HEIGHT = 1.45;

    /** * Дистанция схождения (метры).
     * Снаряд будет направлен из ствола в точку, куда указывает прицел на этой дистанции.
     * Чем меньше число, тем сильнее будет заметно падение пули на расстоянии.
     */
    private static final double CONVERGENCE_DIST = 5.0;

    public static void shootProjectile(
            int pelletCount,
            int damage,
            double spreadAngle,
            String projectileConfigID,
            Ref<EntityStore> shooter,
            CommandBuffer<EntityStore> commandBuffer
    ) {
        TransformComponent transform = commandBuffer.getComponent(shooter, TransformComponent.getComponentType());
        HeadRotation headRotation = commandBuffer.getComponent(shooter, HeadRotation.getComponentType());
        ProjectileConfig config = ProjectileConfig.getAssetMap().getAsset(projectileConfigID);

        if (transform == null || headRotation == null || config == null) return;

        // 1. Позиция ствола (чуть ниже глаз)
        Vector3d muzzlePosition = transform.getTransform().getPosition().clone();
        muzzlePosition.y += 1.8; // Высота вылета

        // 2. Направление взгляда (Берем "чистый" вектор без наклонов вверх)
        Vector3d lookDir = headRotation.getDirection().clone().normalize();

        // Мы НЕ меняем lookDir. Пуля летит ровно туда, куда смотрит камера,
        // но из-за того, что она вылетает ниже глаз и на нее действует гравитация,
        // она будет всегда падать относительно прицела.

        for (int i = 0; i < pelletCount; i++) {
            // Применяем разброс к чистому вектору взгляда
            Vector3d finalDirection = applySpread(lookDir, spreadAngle);

            Ref<EntityStore> projectileRef = ProjectileModule.get().spawnProjectile(
                    shooter,
                    commandBuffer,
                    config,
                    muzzlePosition,
                    finalDirection
            );

            commandBuffer.run(store -> {
                StandardPhysicsProvider physics = store.getComponent(projectileRef, StandardPhysicsProvider.getComponentType());
                if (physics != null) {
                    setCustomImpactConsumer(physics, damage, shooter);
                }
            });
        }
    }

    /**
     * Создает разброс в виде конуса
     */
    private static Vector3d applySpread(Vector3d direction, double spreadAngle) {
        if (spreadAngle <= 0) return direction.clone();

        double r = Math.random() * Math.toRadians(spreadAngle);
        double phi = Math.random() * Math.PI * 2.0;

        Vector3d forward = direction.clone().normalize();
        Vector3d worldUp = new Vector3d(0, 1, 0);
        Vector3d right = forward.clone().cross(worldUp);

        if (right.length() < 0.001) {
            right = new Vector3d(1, 0, 0);
        }
        right.normalize();
        Vector3d up = right.clone().cross(forward).normalize();

        return forward.scale(Math.cos(r))
                .add(right.scale(Math.sin(r) * Math.cos(phi)))
                .add(up.scale(Math.sin(r) * Math.sin(phi)))
                .normalize();
    }

    private static void setCustomImpactConsumer(StandardPhysicsProvider physics, int damage, Ref<EntityStore> shooter) {
        try {
            ProjectileImpactData data = new ProjectileImpactData(damage, shooter);
            Field field = StandardPhysicsProvider.class.getDeclaredField("impactConsumer");
            field.setAccessible(true);
            field.set(physics, (ImpactConsumer) data::onProjectileImpact);
        } catch (ReflectiveOperationException ignored) {}
    }
}