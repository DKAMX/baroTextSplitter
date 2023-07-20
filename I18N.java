import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    public static ResourceBundle texts;

    static {
        // load current system's display langauge
        texts = ResourceBundle.getBundle("MessagesBundle", Locale.getDefault());
    }

    /**
     * A wrapper for ResourceBundle's getString() method
     * 
     * @param key the key for the desired string
     * @return the string for the given key
     */
    public static String getString(String key) {
        return texts.getString(key);
    }
}
