package com.ecokukirin;

import net.minecraft.util.Identifier;

/** Dostepne modele hulajnog. Dodaj nowy wpis + teksture w textures/entity/. */
public enum KukirinVariant {
    G2_PRO("Kukirin G2 Pro", 1.00f, "g2_pro"),
    G3("Kukirin G3", 1.20f, "g3"),
    G4_MAX("Kukirin G4 Max", 1.50f, "g4_max"),
    M5("Kukirin M5", 1.80f, "m5");

    public final String displayName;
    public final float speedMultiplier;
    public final Identifier texture;

    KukirinVariant(String displayName, float speedMultiplier, String tex) {
        this.displayName = displayName;
        this.speedMultiplier = speedMultiplier;
        this.texture = new Identifier(EcoKukirinMod.MOD_ID, "textures/entity/" + tex + ".png");
    }

    public static KukirinVariant byId(int id) {
        KukirinVariant[] v = values();
        return v[Math.floorMod(id, v.length)];
    }
}
