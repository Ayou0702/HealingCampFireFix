package top.prefersmin.healingcampfirefix.common;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import top.prefersmin.healingcampfirefix.config.ModConfig;

/**
 * 命令注册类
 *
 * @author PrefersMin
 * @version 1.0
 */
public class CommandRegister {

    // 注册命令
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("hcff")
                        // 设置命令权限
                        .requires(source -> source.hasPermission(4))
                        // 配置文件重载
                        .then(Commands.literal("configReload")
                                .executes(CommandRegister::configReload)
                        )
        );
    }

    // 配置文件重载方法
    private static int configReload(CommandContext<CommandSourceStack> context) {
        ModConfig.reloadConfig();
        return 1;
    }

}
