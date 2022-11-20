package tk.phili.dienst.dienst.videos;

import java.util.Objects;

public class Video {
    private String lang;
    private int id;
    private String name;
    private String length;
    private int mbSize;
    private String downloadURL;

    public Video(String lang, int id, String name, String length, int mbSize, String downloadURL) {
        this.lang = lang;
        this.id = id;
        this.name = name;
        this.length = length;
        this.mbSize = mbSize;
        this.downloadURL = downloadURL;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public int getMbSize() {
        return mbSize;
    }

    public void setMbSize(int mbSize) {
        this.mbSize = mbSize;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return id == video.id && lang.equals(video.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lang, id);
    }
}
