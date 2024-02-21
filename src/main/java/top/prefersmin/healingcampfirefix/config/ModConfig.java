package top.prefersmin.healingcampfirefix.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import top.prefersmin.healingcampfirefix.HealingCampFireFix;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = HealingCampFireFix.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue CHECK_FOR_CAMPFIRE_DELAY_IN_TICKS = BUILDER
            .comment("营火Tick检查间隔")
            .defineInRange("checkForCampfireDelayInTicks", 100, 1, 1200);

    private static final ForgeConfigSpec.IntValue HEALING_RADIUS = BUILDER
            .comment("营火检查半径")
            .defineInRange("healingRadius", 8, 1, 64);

    private static final ForgeConfigSpec.IntValue EFFECT_DURATION_SECONDS = BUILDER
            .comment("恢复效果持续时间")
            .defineInRange("effectDurationSeconds", 20, 1, 600);

    private static final ForgeConfigSpec.IntValue REGENERATION_LEVEL = BUILDER
            .comment("恢复效果等级")
            .defineInRange("regenerationLevel", 1, 1, 5);

    private static final ForgeConfigSpec.BooleanValue HEAL_PASSIVE_MOBS = BUILDER
            .comment("是否治疗被动生物")
            .define("healPassiveMobs", true);

    private static final ForgeConfigSpec.BooleanValue HIDE_EFFECT_PARTICLES = BUILDER
            .comment("是否隐藏治疗效果粒子")
            .define("hideEffectParticles", true);

    private static final ForgeConfigSpec.BooleanValue CAMPFIRE_MUST_BE_LIT = BUILDER
            .comment("只有营火被点燃时才生效")
            .define("campfireMustBeLit", true);

    private static final ForgeConfigSpec.BooleanValue CAMPFIRE_MUST_BE_SIGNALLING = BUILDER
            .comment("只有营火发出信号时才生效")
            .define("campfireMustBeSignalling", false);

    private static final ForgeConfigSpec.BooleanValue ENABLE_EFFECT_FOR_NORMAL_CAMPFIRES = BUILDER
            .comment("启用普通营火效果")
            .define("enableEffectForNormalCampfires", true);

    private static final ForgeConfigSpec.BooleanValue ENABLE_EFFECT_FOR_SOUL_CAMPFIRES = BUILDER
            .comment("启用灵魂营火效果")
            .define("enableEffectForSoulCampfires", true);

    public static int checkForCampfireDelayInTicks;
    public static int healingRadius;
    public static int effectDurationSeconds;
    public static int regenerationLevel;
    public static boolean healPassiveMobs;
    public static boolean hideEffectParticles;
    public static boolean campfireMustBeLit;
    public static boolean campfireMustBeSignalling;
    public static boolean enableEffectForNormalCampfires;
    public static boolean enableEffectForSoulCampfires;

    public static final ForgeConfigSpec SPEC = BUILDER.build();

    public static final Path path = FMLPaths.CONFIGDIR.get().resolve(HealingCampFireFix.MODID + "-common.toml");

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        checkForCampfireDelayInTicks = CHECK_FOR_CAMPFIRE_DELAY_IN_TICKS.get();
        healingRadius = HEALING_RADIUS.get();
        effectDurationSeconds = EFFECT_DURATION_SECONDS.get();
        regenerationLevel = REGENERATION_LEVEL.get();
        healPassiveMobs = HEAL_PASSIVE_MOBS.get();
        hideEffectParticles = HIDE_EFFECT_PARTICLES.get();
        campfireMustBeLit = CAMPFIRE_MUST_BE_LIT.get();
        campfireMustBeSignalling = CAMPFIRE_MUST_BE_SIGNALLING.get();
        enableEffectForNormalCampfires = ENABLE_EFFECT_FOR_NORMAL_CAMPFIRES.get();
        enableEffectForSoulCampfires = ENABLE_EFFECT_FOR_SOUL_CAMPFIRES.get();
    }

    /**
     * 自动监听配置文件重载事件，并通过重载事件重载配置文件
     *
     * @param event 事件
     */
    @SubscribeEvent
    public void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == net.minecraftforge.fml.config.ModConfig.Type.COMMON) {
            SPEC.setConfig(event.getConfig().getConfigData());
        }
    }

    /**
     * 手动重载配置文件
     */
    public static void reloadConfig() {

        // 创建并加载配置文件
        CommentedFileConfig commentedFileConfig = CommentedFileConfig.builder(path).sync().autosave().writingMode(WritingMode.REPLACE).build();
        commentedFileConfig.load();

        // 设置新的配置文件
        SPEC.setConfig(commentedFileConfig);

    }

}
