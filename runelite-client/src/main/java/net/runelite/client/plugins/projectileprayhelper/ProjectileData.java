package net.runelite.client.plugins.projectileprayhelper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

class ParsedEntry
{
    final String key;
    final String value;
    ParsedEntry(String k, String v){ key=k; value=v; }
}

final class RuleParser
{
    private RuleParser(){}

    static List<ParsedEntry> parseLines(String block)
    {
        List<ParsedEntry> out = new ArrayList<>();
        if (block == null || block.isEmpty()) return out;

        String[] lines = block.split("\\r?\\n");
        for (String raw : lines)
        {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) continue;
            String[] kv = line.split("\\s*[:=]\\s*", 2);
            if (kv.length != 2) continue;
            String k = kv[0].trim();
            String v = kv[1].trim();
            if (!k.isEmpty() && !v.isEmpty()) out.add(new ParsedEntry(k, v));
        }
        return out;
    }
}

final class ProjectileRule
{
    final PrayerType type;
    final String label;

    ProjectileRule(PrayerType type, String label)
    {
        this.type = type;
        this.label = label;
    }
}

enum PrayerType
{
    MAGIC(new Color(0x34,0x98,0xDB)),     // blue-ish
    RANGED(new Color(0x27,0xAE,0x60)),    // green
    MELEE(new Color(0xE74C3C)),           // red ✅ NEW
    UNKNOWN(Color.WHITE);

    final Color color;
    PrayerType(Color c) { this.color = c; }

    static PrayerType fromLabel(String s)
    {
        String t = s.toLowerCase();

        if (t.contains("mage") || t.contains("magic"))
            return MAGIC;

        if (t.contains("range") || t.contains("ranged") || t.contains("missile"))
            return RANGED;

        if (t.contains("melee") || t.contains("mel") || t.contains("stab") || t.contains("slash"))
            return MELEE; // ✅ NEW

        return UNKNOWN;
    }
}

