package tk.phili.dienst.dienst.videos;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import tk.phili.dienst.dienst.utils.JWImages;

@Data
public class JWVideoCategory {

    private String key;
    private String type;
    private String name;
    private String description;

    private List<JWVideoCategory> subcategories = new ArrayList<>();
    private List<JWVideo> media = new ArrayList<>();
    private JWImages images;

}
