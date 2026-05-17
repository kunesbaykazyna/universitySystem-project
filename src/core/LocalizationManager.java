package core;

import enumerations.Language;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationManager {
    private static ResourceBundle bundle;
    private static Language currentLanguage = Language.RU;

    static {
        setLanguage(currentLanguage);
    }

    public static void setLanguage(Language lang) {
        currentLanguage = lang;
        String code = switch (lang) {
            case RU -> "ru";
            case EN -> "en";
            case KZ -> "kk";
        };
        bundle = ResourceBundle.getBundle("messages", Locale.forLanguageTag(code));
    }

    public static String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "  " + key + "  ";
        }
    }

    public static String getString(String key, Object... args) {
        String pattern = getString(key);
        return String.format(pattern, args);
    }
}