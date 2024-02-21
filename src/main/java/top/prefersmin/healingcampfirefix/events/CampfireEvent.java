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

    @SubscribeEvent
    public void playerTickEvent(TickEvent.PlayerTickEvent e) {
        Player player = e.player;
        Level level = player.level();
        if (level.isClientSide) {
            return;
        }

        CampfireEvent.playerTickEvent((ServerLevel)level, (ServerPlayer)player);
    }

    public static void playerTickEvent(ServerLevel world, ServerPlayer player) {
        if (player.tickCount % ModConfig.checkForCampfireDelayInTicks != 0) {
            return;
        }

        List<BlockPos> nearbycampfires = FABFunctions.getAllTaggedTileEntityPositionsNearbyEntity(BlockTags.CAMPFIRES, ModConfig.healingRadius, world, player);
        if (nearbycampfires.isEmpty()) {
            return;
        }

        BlockPos campfire = null;
        for (BlockPos nearbycampfire : nearbycampfires) {
            BlockState campfirestate = world.getBlockState(nearbycampfire);
            Block block = campfirestate.getBlock();

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
                Boolean islit = campfirestate.getValue(CampfireBlock.LIT);
                if (!islit) {
                    continue;
                }
            }
            if (ModConfig.campfireMustBeSignalling) {
                Boolean issignalling = campfirestate.getValue(CampfireBlock.SIGNAL_FIRE);
                if (!issignalling) {
                    continue;
                }
            }

            campfire = nearbycampfire.immutable();
            break;
        }

        if (campfire == null) {
            return;
        }

        BlockPos ppos = player.blockPosition();
        double r = ModConfig.healingRadius;
        if (ppos.closerThan(campfire, r)) {
            boolean addeffect = true;
            MobEffectInstance currentregen = player.getEffect(MobEffects.REGENERATION);
            if (currentregen != null) {
                int currentduration = currentregen.getDuration();
                if (currentduration > (ModConfig.effectDurationSeconds*10)) {
                    addeffect = false;
                }
            }

            if (addeffect) {
                player.addEffect(ModConfig.hideEffectParticles?new MobEffectInstance(MobEffects.REGENERATION, ModConfig.effectDurationSeconds*20, ModConfig.regenerationLevel-1, true, false):new MobEffectInstance(MobEffects.REGENERATION, ModConfig.effectDurationSeconds*20, ModConfig.regenerationLevel-1));
            }
        }
        if (ModConfig.healPassiveMobs) {
            for (Entity entity : world.getEntities(player, new AABB(campfire.getX()-r, campfire.getY()-r, campfire.getZ()-r, campfire.getX()+r, campfire.getY()+r, campfire.getZ()+r))) {
                if (entity instanceof LivingEntity le && (!(entity instanceof Player)) && !entity.getType().getCategory().equals(MobCategory.MONSTER)) {

                    boolean addeffect = true;
                    MobEffectInstance currentregen = le.getEffect(MobEffects.REGENERATION);
                    if (currentregen != null) {
                        int currentduration = currentregen.getDuration();
                        if (currentduration > (ModConfig.effectDurationSeconds*10)) {
                            addeffect = false;
                        }
                    }

                    if (addeffect) {
                        le.addEffect(ModConfig.hideEffectParticles?new MobEffectInstance(MobEffects.REGENERATION, ModConfig.effectDurationSeconds*20, ModConfig.regenerationLevel-1, true, false):new MobEffectInstance(MobEffects.REGENERATION, ModConfig.effectDurationSeconds*20, ModConfig.regenerationLevel-1));
                    }
                }
            }
        }
    }
    
}
