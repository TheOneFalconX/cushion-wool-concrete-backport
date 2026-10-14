package dev.woolbackport;

import net.minecraft.core.NonNullList;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class CushionEntity extends LivingEntity { // Changed from Entity to LivingEntity
    private static final EntityDataAccessor<String> COLOR =
        SynchedEntityData.defineId(CushionEntity.class, EntityDataSerializers.STRING);

    public CushionEntity(EntityType<? extends CushionEntity> type, Level level) {
        super(type, level);
    }

    public void setColor(String color) {
        this.entityData.set(COLOR, color);
    }

    public String getColor() {
        return this.entityData.get(COLOR);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(COLOR, "white");
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        return false;
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!this.getPassengers().isEmpty() || player.isPassenger()) {
            return InteractionResult.PASS;
        }
        player.startRiding(this, true, true);
        return InteractionResult.CONSUME;
    }

    // --- LivingEntity required abstract methods ---
    @Override
    public Iterable<ItemStack> getArmorSlots() {
        return NonNullList.create();
    }

    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }
    // ---------------------------------------------

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        // Plays your exact custom cushion break sound instead of the armor stand sound
        return WoolBackport.CUSHION_BREAK;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        ItemStack drop = new ItemStack(WoolBackport.cushionItem(getColor()));
        this.spawnAtLocation(level, drop);
        this.discard();
        return true;
    }
}
