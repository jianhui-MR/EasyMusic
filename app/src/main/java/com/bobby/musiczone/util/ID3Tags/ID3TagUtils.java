package com.bobby.musiczone.util.ID3Tags;

import org.blinkenlights.jid3.ID3Exception;
import org.blinkenlights.jid3.MP3File;
import org.blinkenlights.jid3.MediaFile;
import org.blinkenlights.jid3.v2.ID3V2_3_0Tag;

import java.io.File;

public class ID3TagUtils {
    public static boolean setID3Tags(File sourceFile, ID3Tags id3Tags, boolean clearOriginal) {
        MediaFile oMediaFile = new MP3File(sourceFile);
        ID3V2_3_0Tag oID3V2_3_0Tag = null;
        if (clearOriginal) {
            oID3V2_3_0Tag = new ID3V2_3_0Tag();
        } else {
            try {
                oID3V2_3_0Tag = (ID3V2_3_0Tag) oMediaFile.getID3V2Tag();
            } catch (ID3Exception e) {
                e.printStackTrace();
            }
            if (oID3V2_3_0Tag == null) {
                oID3V2_3_0Tag = new ID3V2_3_0Tag();
            }
        }

        try {
            id3Tags.fillID3Tag(oID3V2_3_0Tag);
            oMediaFile.setID3Tag(oID3V2_3_0Tag);
            oMediaFile.sync();
            return true;
        } catch (ID3Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
