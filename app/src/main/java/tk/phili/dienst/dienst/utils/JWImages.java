package tk.phili.dienst.dienst.utils;

import lombok.Data;

@Data
public class JWImages {

    private PNR pnr;
    @Data
    public class PNR {
        private String lg;
    }
}
