package xyz.jawbts.realpeace.mixin.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SlimeEntity.class)
public abstract class SlimeEntityMixin extends MobEntity {
    @Shadow public abstract void setSize(int size, boolean heal);

    protected SlimeEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }
    /**
     * @author Mojang AB
     * @reason 1
     */
    @Inject(method = "tick()V", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if(getHealth() != getMaxHealth()) {
            dropStack(new ItemStack(Items.SLIME_BALL));
        }
        setSize(1, true);
    }
}