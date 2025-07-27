package tk.phili.dienst.dienst.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JWLang {

    /// ISO 639-3
    private String symbol;

    /// JW Language Code
    private String langcode;
}
