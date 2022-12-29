package xyz.jawbts.realpeace.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.entity.raid.RaiderEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(WitchEntity.class)
public abstract class WitchEntityMixin extends RaiderEntity{
    protected WitchEntityMixin(EntityType<? extends RaiderEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean isDrinking();
    /**
     * @author Mojang AB
     * @reason 1
     */
    @Overwrite
    public void attack(LivingEntity target, float pullProgress) {
        if (!this.isDrinking()) {
            Vec3d vec3d = target.getVelocity();
            double d = target.getX() + vec3d.x - this.getX();
            double e = target.getEyeY() - 1.100000023841858 - this.getY();
            double f = target.getZ() + vec3d.z - this.getZ();
            double g = Math.sqrt(d * d + f * f);
            Potion potion = Potions.NIGHT_VISION;
            if (target instanceof RaiderEntity) {
                if (target.getHealth() <= 4.0F) {
                    potion = Potions.HEALING;
                } else {
                    potion = Potions.REGENERATION;
                }

                this.setTarget((LivingEntity)null);
            } else if (g >= 8.0 && !target.hasStatusEffect(StatusEffects.SPEED)) {
                potion = Potions.SWIFTNESS;
            } else if (!target.hasStatusEffect(StatusEffects.HEALTH_BOOST)) {
                float f2 = target.getMaxHealth() - target.getHealth();
                if(f2 > 10.0) {
                    potion = Potions.STRONG_HEALING;
                } else if(f2 > 5.0) {
                    potion = Potions.HEALING;
                }
            }

            PotionEntity potionEntity = new PotionEntity(this.world, this);
            potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
            potionEntity.setPitch(potionEntity.getPitch() - -20.0F);
            potionEntity.setVelocity(d, e + g * 0.2, f, 0.75F, 8.0F);
            if (!this.isSilent()) {
                this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            }

            this.world.spawnEntity(potionEntity);
        }
    }
}
