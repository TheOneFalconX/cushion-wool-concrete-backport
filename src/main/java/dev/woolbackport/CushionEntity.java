package dev.woolbackport;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * A thin, placeable seat. No collision with other entities (you can walk over it);
 * right-click sits you down, sneak gets back up (vanilla's generic dismount key),
 * attacking it breaks it and drops itself.
 */
public class CushionEntity extends Entity {
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
        builder.define(COLOR, "white");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput valueOutput) {
        valueOutput.putString("color", getColor());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput valueInput) {
        setColor(valueInput.getString("color").orElse("white"));
    }

    @Override
    public boolean isPickable() {
        // Lets players click it (to sit) and attack it (to break it).
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canCollideWith(Entity other) {
        // No collision: other entities/players pass through instead of being blocked.
        return false;
    }
    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!this.getPassengers().isEmpty() || player.isPassenger()) {
            return InteractionResult.PASS;
        }
        player.startRiding(this);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        ItemStack drop = new ItemStack(WoolBackport.cushionItem(getColor()));
        this.spawnAtLocation(level, drop);
        this.discard();
        return true;
    }
}
