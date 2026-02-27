package de.mrjulsen.ctt;

import dev.architectury.injectables.annotations.ExpectPlatform;

public class CTTCrossPlatform {
    @ExpectPlatform
    public static void registerConfig() {
        throw new AssertionError();
    }
}
