package tk.phili.dienst.dienst.videos;

import lombok.Data;

@Data
public class JWVideoFile {
    private String progressiveDownloadURL;
    private long filesize;
    private String label;
}
