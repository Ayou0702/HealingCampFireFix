package top.prefersmin.healingcampfirefix.events;

import com.natamus.collective_common_forge.functions.FABFunctions;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.prefersmin.healingcampfirefix.HealingCampFireFix;
import top.prefersmin.healingcampfirefix.config.ModConfig;

import java.util.List;

@Mod.EventBusSubscriber(modid = HealingCampFireFix.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CampfireEvent {

    public static void playerTickEvent(ServerLevel world, ServerPlayer player) {

        List<BlockPos> nearByCampfires = FABFunctions.getAllTaggedTileEntityPositionsNearbyEntity(BlockTags.CAMPFIRES, ModConfig.healingRadius, world, player);
        if (nearByCampfires.isEmpty()) {
            return;
        }

        BlockPos campfire = null;

        for (BlockPos nearByCampfire : nearByCampfires) {

            BlockState campFireState = world.getBlockState(nearByCampfire);
            Block block = campFireState.getBlock();

            if (!ModConfig.enableEffectForNormalCampfires) {
                if (block.equals(Blocks.CAMPFIRE)) {
                    continue;
                }
            }

            if (!ModConfig.enableEffectForSoulCampfires) {
                if (block.equals(Blocks.SOUL_CAMPFIRE)) {
                    continue;
                }
            }

            if (ModConfig.campfireMustBeLit) {
                Boolean isLit = campFireState.getValue(CampfireBlock.LIT);
                if (!isLit) {
                    continue;
                }
            }

            if (ModConfig.campfireMustBeSignalling) {
                Boolean isSignalLing = campFireState.getValue(CampfireBlock.SIGNAL_FIRE);
                if (!isSignalLing) {
                    continue;
                }
            }

            campfire = nearByCampfire.immutable();
            break;

        }

        if (campfire == null) {
            return;
        }

        BlockPos blockPos = player.blockPosition();
        double radius = ModConfig.healingRadius;

        if (blockPos.closerThan(campfire, radius)) {
            addEffect(player);
        }

        if (ModConfig.healPassiveMobs) {
            for (Entity entity : world.getEntities(player, new AABB(campfire.getX() - radius, campfire.getY() - radius, campfire.getZ() - radius, campfire.getX() + radius, campfire.getY() + radius, campfire.getZ() + radius))) {
                if (entity instanceof LivingEntity le && (!(entity instanceof Player)) && !entity.getType().getCategory().equals(MobCategory.MONSTER)) {
                    addEffect(le);
                }
            }
        }

    }

    private static void addEffect(LivingEntity entity) {

        boolean addEffect = true;
        MobEffectInstance effectInstance = entity.getEffect(MobEffects.REGENERATION);
        if (effectInstance != null) {
            int duration = effectInstance.getDuration();
            if (duration > (ModConfig.effectDurationSeconds * 10)) {
                addEffect = false;
            }
        }

        if (addEffect) {
            entity.addEffect(ModConfig.hideEffectParticles ? new MobEffectInstance(MobEffects.REGENERATION, ModConfig.effectDurationSeconds * 20, ModConfig.regenerationLevel - 1, true, false) : new MobEffectInstance(MobEffects.REGENERATION, ModConfig.effectDurationSeconds * 20, ModConfig.regenerationLevel - 1));
        }

    }

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent e) {

        Player player = e.player;
        Level level = player.level();

        if (level.isClientSide) {
            return;
        }

        if (player.tickCount % ModConfig.checkForCampfireDelayInTicks != 0) {
            return;
        }

        CampfireEvent.playerTickEvent((ServerLevel) level, (ServerPlayer) player);

    }

}
