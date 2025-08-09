package tk.phili.dienst.dienst.videos;

import android.content.Context;

import com.google.gson.annotations.Expose;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import lombok.Data;
import tk.phili.dienst.dienst.utils.JWImages;

@Data
public class JWVideo {

    @Expose
    private String guid;
    @Expose
    private String languageAgnosticNaturalKey;
    @Expose
    private String naturalKey;
    @Expose
    private String title;
    @Expose
    private String description;
    @Expose
    private String firstPublished;
    @Expose
    private String durationFormattedHHMM;

    private List<JWVideoFile> files;
    @Expose
    private JWImages images;

    public boolean isDownloaded(Context context) {
        return getFile(context).exists();
    }

    public File getFile(Context context) {
        return new File(context.getFilesDir(), "videos/" + naturalKey + ".mp4");
    }

    public String getJWLanguage() {
        List<String> partsKeyNoLanguage = Arrays.asList(languageAgnosticNaturalKey.split("_"));
        List<String> partsKeyWithLanguage = Arrays.asList(naturalKey.split("_"));

        for(String keyPart : partsKeyWithLanguage) {
            if(!partsKeyNoLanguage.contains(keyPart)) {
                return keyPart;
            }
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JWVideo jwVideo = (JWVideo) o;
        return Objects.equals(guid, jwVideo.guid) && Objects.equals(naturalKey, jwVideo.naturalKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(guid, naturalKey);
    }
}
