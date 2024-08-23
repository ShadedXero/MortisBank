package com.mortisdevelopment.mortisbank.dependencies;

import com.mortisdevelopment.mortiscore.dependencies.DependencyManager;
import com.mortisdevelopment.mortiscore.libby.Library;
import org.bukkit.plugin.java.JavaPlugin;

public class BankDependencyManager extends DependencyManager {

    public BankDependencyManager(JavaPlugin plugin) {
        super(plugin);
    }

    public BankDependencyManager(JavaPlugin plugin, String directoryName) {
        super(plugin, directoryName);
    }

    @Override
    public void addLibraries() {
        addMavenCentral();
        addJitPack();
        addMavenLocal();
        loadLibrary(Library.builder()
                .groupId("de{}rapha149")
                .artifactId("signgui")
                .version("2.4.1")
                .relocate("de{}rapha149{}signgui", "com{}mortisdevelopment{}mortisbank{}signgui")
                .build());
        loadLibrary(Library.builder()
                .groupId("net{}wesjd")
                .artifactId("anvilgui")
                .version("1.10.2-SNAPSHOT")
                .relocate("net{}wesjd{}anvilgui", "com{}mortisdevelopment{}mortisbank{}anvilgui")
                .build());
    }
}
