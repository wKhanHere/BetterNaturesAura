package net.wkhan.naturesaura_plus.data.reload;

import net.wkhan.naturesaura_plus.data.config.ManualConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.wkhan.naturesaura_plus.data.config.ManualConfig.getAllowedModIds;

public class LogCleaner {

    public static final Set<String> LOADED_MODS_CACHE = ConcurrentHashMap.newKeySet();
    public static final Set<String> MISSING_MODS_CACHE = ConcurrentHashMap.newKeySet();

    private static DynamicModIdFilter activeFilter = null;

    public static void init() {
        ManualConfig.load();

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration configuration = context.getConfiguration();
        LoggerConfig rootLogger = configuration.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

        if (!ManualConfig.isEnabled()) {
            System.out.println("[NaturesAuraPlus] Config is set to show ALL error messages, including json files referencing content from missing mods!" +
                    "\n[NaturesAuraPlus] Prepare for potentially massive logs!");
            if (activeFilter == null)
                return;
            rootLogger.removeFilter(activeFilter);
            activeFilter.stop();
            activeFilter = null; // Clear the reference
            context.updateLoggers();
            return;
        }

        LOADED_MODS_CACHE.clear();
        MISSING_MODS_CACHE.clear();

        LOADED_MODS_CACHE.addAll(getAllowedModIds());
        LOADED_MODS_CACHE.add("minecraft");
        LOADED_MODS_CACHE.add("forge");
        LOADED_MODS_CACHE.add("c");
        LOADED_MODS_CACHE.add("naturesaura_plus");

        if (activeFilter != null)
            return;
        activeFilter = new DynamicModIdFilter();
        activeFilter.start();
        rootLogger.addFilter(activeFilter);
        context.updateLoggers();
    }
}
