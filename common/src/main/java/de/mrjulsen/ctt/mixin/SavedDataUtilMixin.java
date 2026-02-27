package de.mrjulsen.ctt.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.utility.SavedDataUtil;
import de.mrjulsen.ctt.util.CreateThreadedTrains;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.saveddata.SavedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@Mixin(SavedDataUtil.class)
public class SavedDataUtilMixin {

    @Unique
    private static long ctt$saveTime;

    @Inject(method = "saveWithDatOld", at = @At("HEAD"))
    private static <T extends SavedData> void ctt$measureTimeStart(T savedData, File file, CallbackInfo ci) {
        ctt$saveTime = System.currentTimeMillis();
    }

    @Inject(method = "saveWithDatOld", at = @At("TAIL"))
    private static <T extends SavedData> void ctt$measureTimeEnd(T savedData, File file, CallbackInfo ci) {
        CreateThreadedTrains.LOGGER.info("Track Graph saved. Took " + (System.currentTimeMillis() - ctt$saveTime) + "ms");
    }

    @Inject(
            method = "saveWithDatOld",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/io/File;createTempFile(Ljava/lang/String;Ljava/lang/String;Ljava/io/File;)Ljava/io/File;"
            ),
            cancellable = true
    )
    private static <T extends SavedData> void ctt$wrapInAsyncWorker(T savedData, File file, CallbackInfo ci, @Local(name = "compoundtag") CompoundTag compoundtag, @Local(name = "savedDataName") String savedDataName) {
        CreateThreadedTrains.submitAsyncIO(() -> {
            synchronized (file.getAbsolutePath().intern()) {
                try {
                    long time = System.currentTimeMillis();
                    File temp = File.createTempFile(savedDataName, ".dat", file.getParentFile());
                    NbtIo.writeCompressed(compoundtag, temp);
                    File oldFile = Paths.get(file.getParent(), savedDataName + ".dat_old").toFile();
                    Util.safeReplaceFile(file, temp, oldFile);
                    CreateThreadedTrains.LOGGER.info("Track Graph saved to file. Took " + (System.currentTimeMillis() - time) + "ms");
                } catch (IOException ioexception) {
                    Create.LOGGER.error("Could not save data {}", savedData, ioexception);
                }
            }
        });

        savedData.setDirty(false);
        ci.cancel();
    }
}
