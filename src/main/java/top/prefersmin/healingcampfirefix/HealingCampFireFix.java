package top.prefersmin.healingcampfirefix;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import top.prefersmin.healingcampfirefix.config.ModConfig;
import top.prefersmin.healingcampfirefix.events.CampfireEvent;

@Mod(HealingCampFireFix.MODID)
public class HealingCampFireFix {

    public static final String MODID = "healingcampfirefix";

    public HealingCampFireFix() {
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CampfireEvent());
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
    }

}
