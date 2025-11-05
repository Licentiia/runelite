package net.runelite.client.plugins.prayerpredictor;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

class ParsedEntry
{
    final String key;
    final String value;

    ParsedEntry(String key, String value)
    {
        this.key = key;
        this.value = value;
    }
}

final class Parser
{
    private Parser(){}

    static List<ParsedEntry> parseLines(String block)
    {
        List<ParsedEntry> out = new ArrayList<>();
        if (block == null || block.isEmpty()) return out;

        String[] lines = block.split("\\r?\\n");
        for (String raw : lines)
        {
            String line = raw.trim();
            if (line.isEmpty() || line.startsWith("#") || line.startsWith("//"))
            {
                continue;
            }
            // allow id=Label or id:Label
            String[] kv = line.split("\\s*[:=]\\s*", 2);
            if (kv.length != 2) continue;

            String k = kv[0].trim();
            String v = kv[1].trim();
            if (!k.isEmpty() && !v.isEmpty())
            {
                out.add(new ParsedEntry(k, v));
            }
        }
        return out;
    }

    static Color parseColor(String s)
    {
        // Accept #RRGGBB or 0xRRGGBB or plain hex RRGGBB
        String hex = s.trim();
        if (hex.startsWith("#")) hex = hex.substring(1);
        if (hex.startsWith("0x") || hex.startsWith("0X")) hex = hex.substring(2);
        if (hex.length() != 6) return null;
        try
        {
            int rgb = Integer.parseInt(hex, 16);
            return new Color(rgb);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }
}
