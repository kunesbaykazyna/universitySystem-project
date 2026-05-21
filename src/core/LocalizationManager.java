package core;

import enumerations.Language;
import java.text.MessageFormat;
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
        try {
            String pattern = bundle.getString(key);
            return MessageFormat.format(pattern, args);
        } catch (Exception e) {
            return "  " + key + "  ";
        }
    }
}