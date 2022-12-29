package xyz.jawbts.realpeace.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CreeperEntity.class)
public abstract class CreeperEntityMixin extends HostileEntity {
    protected CreeperEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    private int lastFuseTime;
    @Shadow
    private int currentFuseTime;
    @Shadow
    private int fuseTime;
    @Shadow
    public abstract boolean isIgnited();
    @Shadow
    public abstract void setFuseSpeed(int fuseSpeed);
    @Shadow
    public abstract  int getFuseSpeed();

    private boolean isAbleToExplore = true;

    /**
     * @author Mojang AB
     * @reason 1
     */
    @Overwrite
    public void tick() {
        if (this.isAlive()) {
            this.lastFuseTime = this.currentFuseTime;
            if (this.isIgnited()) {
                this.setFuseSpeed(1);
            }

            int i = this.getFuseSpeed();
            if (i > 0 && this.currentFuseTime == 0) {
                this.playSound(SoundEvents.ENTITY_CREEPER_PRIMED, 1.0F, 0.5F);
                this.emitGameEvent(GameEvent.PRIME_FUSE);
            }

            if(isAbleToExplore) {
                this.currentFuseTime += i;
                if (this.currentFuseTime < 0) {
                    this.currentFuseTime = 0;
                }

                if (this.currentFuseTime >= this.fuseTime) {
                    this.currentFuseTime = 0;
                    dropStack(new ItemStack(Items.GUNPOWDER, (int)(Math.random() * 100) % 5 + 1));
                    isAbleToExplore = false;
                }
            }
        }

        super.tick();
    }
}
