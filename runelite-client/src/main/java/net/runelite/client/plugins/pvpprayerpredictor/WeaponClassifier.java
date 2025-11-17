package net.runelite.client.plugins.pvpprayerpredictor;

import net.runelite.api.ItemID;
import net.runelite.client.plugins.pvpprayerpredictor.AttackStyle;

import java.util.Set;
import java.util.stream.Stream;
import java.util.stream.Collectors;

public final class WeaponClassifier
{
    private WeaponClassifier() {}

    // =============================
    // ===== MELEE WEAPON SETS =====
    // =============================

    // ---- Godswords ----
    private static final Set<Integer> MELEE_GODSWORDS = Set.of(
            ItemID.ARMADYL_GODSWORD,
            ItemID.BANDOS_GODSWORD,
            ItemID.SARADOMIN_GODSWORD,
            ItemID.ZAMORAK_GODSWORD,
            ItemID.ANCIENT_GODSWORD
    );

    // ---- Dragon melee weapons ----
    private static final Set<Integer> MELEE_DRAGON = Set.of(
            ItemID.DRAGON_CLAWS,
            ItemID.DRAGON_DAGGER,
            ItemID.DRAGON_DAGGERP,
            ItemID.DRAGON_DAGGERP_5680,
            ItemID.DRAGON_DAGGERP_5698,
            ItemID.DRAGON_SCIMITAR,
            ItemID.DRAGON_LONGSWORD,
            ItemID.DRAGON_MACE,
            ItemID.DRAGON_SPEAR,
            ItemID.DRAGON_SPEARP,
            ItemID.DRAGON_SPEARP_5716,
            ItemID.DRAGON_SPEARP_5730,
            ItemID.DRAGON_2H_SWORD,
            ItemID.DRAGON_WARHAMMER,
            ItemID.DRAGON_HALBERD,
            ItemID.DRAGON_SWORD,
            //ItemID.DRAGON_LONGBOW, // not melee, just to avoid confusion (NOT included here)
            ItemID.DRAGON_HUNTER_LANCE // PvM melee weapon but PvP-capable
    );

    // ---- Spec melee & high tier PK weapons ----
    private static final Set<Integer> MELEE_SPEC = Set.of(
            ItemID.GRANITE_MAUL,
            ItemID.GRANITE_MAUL_24225,
            ItemID.GRANITE_MAUL_20557,
            ItemID.VOIDWAKER,
            ItemID.ABYSSAL_WHIP,
            ItemID.ABYSSAL_TENTACLE,
            ItemID.ABYSSAL_BLUDGEON,
            ItemID.ABYSSAL_DAGGER,
            ItemID.ABYSSAL_DAGGER_P,
            ItemID.ABYSSAL_DAGGER_P_13269,
            ItemID.GHRAZI_RAPIER,
            ItemID.ELDER_MAUL,
            ItemID.OSMUMTENS_FANG,
            ItemID.VESTAS_LONGSWORD,
            ItemID.STATIUSS_WARHAMMER,
            ItemID.ZURIELS_STAFF, // MAGIC staff, not melee (excluded from this set)
            ItemID.INQUISITORS_MACE,
            ItemID.SARADOMIN_SWORD,
            ItemID.SARADOMINS_BLESSED_SWORD,
            ItemID.HOLY_SANGUINESTI_STAFF, // magic, excluded
            ItemID.BARRELCHEST_ANCHOR
    );

    // ---- Obsidian (Tzhaar) melee ----
    private static final Set<Integer> MELEE_OBSIDIAN = Set.of(
            //ItemID.TZHAAR_KET_OM,
            //ItemID.OBSIDIAN_SWORD,
            //ItemID.OBSIDIAN_DAGGER
    );

    // ---- Barrows melee ----
    private static final Set<Integer> MELEE_BARROWS = Set.of(
            ItemID.DHAROKS_GREATAXE,
            ItemID.VERACS_FLAIL,
            ItemID.TORAGS_HAMMERS,
            ItemID.GUTHANS_WARSPEAR
    );

    // ---- Misc melee PK weapons ----
    private static final Set<Integer> MELEE_MISC = Set.of(
            ItemID.LEAFBLADED_BATTLEAXE,
            //ItemID.LEAF_BLADED_SWORD,
            ItemID.ZAMORAKIAN_HASTA,
            ItemID.BONE_DAGGER,
            ItemID.BRONZE_SCIMITAR, ItemID.IRON_SCIMITAR, ItemID.STEEL_SCIMITAR,
            ItemID.BLACK_SCIMITAR, ItemID.MITHRIL_SCIMITAR, ItemID.ADAMANT_SCIMITAR,
            ItemID.RUNE_SCIMITAR,
            ItemID.IRON_2H_SWORD, ItemID.STEEL_2H_SWORD, ItemID.MITHRIL_2H_SWORD,
            ItemID.ADAMANT_2H_SWORD, ItemID.RUNE_2H_SWORD
    );

    // ==============================
    // ===== RANGED WEAPON SETS =====
    // ==============================

    // ---- Crossbows ----
    private static final Set<Integer> RANGED_XBOWS = Set.of(
            ItemID.RUNE_CROSSBOW,
            ItemID.DRAGON_CROSSBOW,
            ItemID.ARMADYL_CROSSBOW,
            ItemID.KARILS_CROSSBOW,
            ItemID.ZARYTE_CROSSBOW,
            //ItemID.HUNTER_CROSSBOW,
            ItemID.DRAGON_HUNTER_CROSSBOW
    );

    // ---- Bows ----
    private static final Set<Integer> RANGED_BOWS = Set.of(
            ItemID.MAGIC_SHORTBOW,
            ItemID.MAGIC_SHORTBOW_I,
            ItemID.DARK_BOW,
            ItemID.CRYSTAL_BOW,
            ItemID.BOW_OF_FAERDHINEN,
            ItemID.BOW_OF_FAERDHINEN_C,
            //ItemID.COMPOSITE_BOW,
            ItemID.OAK_SHORTBOW,
            ItemID.WILLOW_SHORTBOW,
            ItemID.MAPLE_SHORTBOW,
            ItemID.YEW_SHORTBOW
    );

    // ---- Ballistas ----
    private static final Set<Integer> RANGED_BALLISTAS = Set.of(
            ItemID.LIGHT_BALLISTA,
            ItemID.HEAVY_BALLISTA
    );

    // ---- Thrown weapons (knives, darts, javelins, axes) ----
    private static final Set<Integer> RANGED_THROWN = Set.of(
            ItemID.DRAGON_KNIFE,
            ItemID.RUNE_KNIFE,
            ItemID.DRAGON_DART,
            ItemID.RUNE_DART,
            ItemID.RUNE_JAVELIN,
            ItemID.DRAGON_JAVELIN,
            ItemID.RUNE_THROWNAXE,
            ItemID.DRAGON_THROWNAXE
    );

    // ---- Chinchompas ----
    private static final Set<Integer> RANGED_CHINS = Set.of(
            ItemID.CHINCHOMPA,
            ItemID.RED_CHINCHOMPA,
            ItemID.BLACK_CHINCHOMPA
    );

    // ---- PvP thrown weapons (Morrigan’s) ----
    private static final Set<Integer> RANGED_PVP = Set.of(
            ItemID.MORRIGANS_JAVELIN,
            ItemID.MORRIGANS_THROWING_AXE
    );

    // ---- Misc ranged PK weapons ----
    private static final Set<Integer> RANGED_MISC = Set.of(
            ItemID.TOXIC_BLOWPIPE,
            //ItemID.SALAMANDER, // salamanders change style, handled separately if needed
            ItemID.BLACK_SALAMANDER
    );

    // =============================
    // ===== MAGIC WEAPON SETS =====
    // =============================

    // ---- Magic staves (standard + ancient) ----
    private static final Set<Integer> MAGIC_STAVES = Set.of(
            ItemID.ANCIENT_STAFF,
            ItemID.STAFF_OF_THE_DEAD,
            ItemID.TOXIC_STAFF_OF_THE_DEAD,
            ItemID.STAFF_OF_LIGHT,
            ItemID.NIGHTMARE_STAFF,
            ItemID.HARMONISED_NIGHTMARE_STAFF,
            ItemID.VOLATILE_NIGHTMARE_STAFF,
            ItemID.ELDRITCH_NIGHTMARE_STAFF,
            ItemID.SANGUINESTI_STAFF,
            ItemID.HOLY_SANGUINESTI_STAFF,
            ItemID.TUMEKENS_SHADOW
    );

    // ---- Tridents ----
    private static final Set<Integer> MAGIC_TRIDENTS = Set.of(
            ItemID.TRIDENT_OF_THE_SEAS,
            ItemID.TRIDENT_OF_THE_SEAS_E,
            ItemID.TRIDENT_OF_THE_SWAMP,
            ItemID.TRIDENT_OF_THE_SWAMP_E
    );

    // ---- Sceptres & wands ----
    private static final Set<Integer> MAGIC_WANDS = Set.of(
            ItemID.MASTER_WAND,
            ItemID.KODAI_WAND
    );

    private static final Set<Integer> MAGIC_SCEPTRES = Set.of(
            ItemID.ANCIENT_SCEPTRE,
            ItemID.BLOOD_ANCIENT_SCEPTRE,
            ItemID.ICE_ANCIENT_SCEPTRE,
            ItemID.SMOKE_ANCIENT_SCEPTRE,
            ItemID.SHADOW_ANCIENT_SCEPTRE,
            ItemID.ACCURSED_SCEPTRE,
            ItemID.THAMMARONS_SCEPTRE_U,
            ItemID.THAMMARONS_SCEPTRE
    );

    // ---- F2P magic ----
    private static final Set<Integer> MAGIC_F2P = Set.of(
            ItemID.STAFF_OF_FIRE,
            ItemID.STAFF_OF_WATER,
            ItemID.STAFF_OF_AIR,
            ItemID.STAFF_OF_EARTH
    );

    // =========================
    // ===== MERGED LOOKUP =====
    // =========================

    private static final Set<Integer> MELEE = merge(
            MELEE_GODSWORDS, MELEE_DRAGON, MELEE_SPEC, MELEE_OBSIDIAN,
            MELEE_BARROWS, MELEE_MISC
    );

    private static final Set<Integer> RANGED = merge(
            RANGED_XBOWS, RANGED_BOWS, RANGED_BALLISTAS, RANGED_THROWN,
            RANGED_CHINS, RANGED_PVP, RANGED_MISC
    );

    private static final Set<Integer> MAGIC = merge(
            MAGIC_STAVES, MAGIC_TRIDENTS, MAGIC_WANDS, MAGIC_SCEPTRES, MAGIC_F2P
    );

    private static Set<Integer> merge(Set<Integer>... sets)
    {
        return Stream.of(sets).flatMap(Set::stream).collect(Collectors.toSet());
    }

    // =========================
    // ===== CLASSIFY API ======
    // =========================

    public static AttackStyle classify(int weaponItemId)
    {
        if (MELEE.contains(weaponItemId)) return AttackStyle.MELEE;
        if (RANGED.contains(weaponItemId)) return AttackStyle.RANGED;
        if (MAGIC.contains(weaponItemId)) return AttackStyle.MAGIC;
        return AttackStyle.UNKNOWN;
    }
}
