package net.wkhan.naturesaura_plus.data.reload;

import net.minecraftforge.fml.ModList;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.filter.AbstractFilter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.wkhan.naturesaura_plus.data.reload.LogCleaner.LOADED_MODS_CACHE;
import static net.wkhan.naturesaura_plus.data.reload.LogCleaner.MISSING_MODS_CACHE;

public class DynamicModIdFilter extends AbstractFilter {
    private static final Pattern NAMESPACE_PATTERN = Pattern.compile("([a-z0-9_.-]+):[a-z0-9_/.-]+");

    @Override
    public Result filter(LogEvent event) {
        if (event.getMessage() == null)
            return Result.NEUTRAL;
        String msg = event.getMessage().getFormattedMessage();
        if (!isMsgPotentiallyBloat(msg))
            return Result.NEUTRAL;

        Matcher matcher = NAMESPACE_PATTERN.matcher(msg);
        boolean willIShutUpLogs = false;
        while (matcher.find()) {
            String detectedModId = matcher.group(1);
            if (LOADED_MODS_CACHE.contains(detectedModId))
                continue;
            if (MISSING_MODS_CACHE.contains(detectedModId)) {
                willIShutUpLogs = true;
                continue;
            }
            if (!ModList.get().isLoaded(detectedModId)) {
                MISSING_MODS_CACHE.add(detectedModId);
                willIShutUpLogs = true;
                continue;
            }
            LOADED_MODS_CACHE.add(detectedModId);
        }
        if (willIShutUpLogs)
            return Result.DENY;

        return Result.NEUTRAL;
    }

    private static boolean isMsgPotentiallyBloat(String msg) {
        return msg.contains("[NaturesAuraPlus]") && msg.contains("Unknown Registry ID");
    }
}
