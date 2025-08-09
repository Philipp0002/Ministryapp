package tk.phili.dienst.dienst.utils;

import java.util.Locale;

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

    public String getLocalizedLanguageName() {
        return new Locale(symbol).getDisplayName(Locale.getDefault());
    }
}
