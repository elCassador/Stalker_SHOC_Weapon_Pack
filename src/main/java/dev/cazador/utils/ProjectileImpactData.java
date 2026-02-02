package dev.cazador.utils;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.ParticleUtil;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class ProjectileImpactData {

    private int damage;
    private Ref<EntityStore> shooter;

    public ProjectileImpactData(int damage, Ref<EntityStore> shooter) {
        this.damage = damage;
        this.shooter = shooter;
    }

    public ProjectileImpactData() {
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public Ref<EntityStore> getShooter() {
        return shooter;
    }

    public void setShooter(Ref<EntityStore> shooter) {
        this.shooter = shooter;
    }

    void onProjectileImpact(Ref<EntityStore> projectileRef, Vector3d impactPosition, Ref<EntityStore> targetRef, String collisionDetail, CommandBuffer<EntityStore> commandBuffer) {
        if (targetRef != null && targetRef.isValid() && projectileRef != null && projectileRef.isValid()) {
            Ref<EntityStore> shooter = this.getShooter();
            Damage.EntitySource source = new Damage.EntitySource(shooter != null ? shooter : projectileRef);
            DamageCause cause = DamageCause.getAssetMap().getAsset("Projectile");
            if (cause != null) {
                Damage damage = new Damage(source, cause, (float) getDamage());
                DamageSystems.executeDamage(targetRef, commandBuffer, damage);
            }
        }

        if (projectileRef != null && projectileRef.isValid()) {
            int index = SoundEvent.getAssetMap().getIndex("SFX_GunPvP_Assault_Rifle_Bullet_Death");
            World world = (projectileRef.getStore().getExternalData()).getWorld();
            EntityStore store = world.getEntityStore();
            world.execute(() -> {
                TransformComponent transform = store.getStore().getComponent(projectileRef, EntityModule.get().getTransformComponentType());

                if (transform == null) {
                    return;
                }

                for (PlayerRef playerRef : world.getPlayerRefs()) {
                    SoundUtil.playSoundEvent3dToPlayer(playerRef.getReference(), index, SoundCategory.SFX, transform.getPosition().add(new Vector3d(0.0, -0.25, 0.0)), store.getStore());
                }

                ParticleUtil.spawnParticleEffect("Gun_Impact", transform.getPosition(), commandBuffer);
                commandBuffer.removeEntity(projectileRef, RemoveReason.REMOVE);
            });
        }
    }
}
