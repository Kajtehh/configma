package pl.kajteh.test;

import org.bukkit.plugin.java.JavaPlugin;
import pl.kajteh.configma.Config;
import pl.kajteh.configma.yaml.YamlConfig;

import java.io.File;

public final class TestPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final long startTime = System.currentTimeMillis();

        final Config<TestConfig> testConfig = YamlConfig.builder(TestConfig.class)
                .file(new File(this.getDataFolder(), "test-config.yml"))
                .load();

        this.getLogger().info("Configuration loaded in " + (System.currentTimeMillis() - startTime) + "ms");

        testConfig
                .edit(config -> config.test = "nowy str")
                .get(config -> this.getLogger().info(config.test))
                .save();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
