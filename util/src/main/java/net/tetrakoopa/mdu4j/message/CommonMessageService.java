package net.tetrakoopa.mdu4j.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public abstract class CommonMessageService implements MessageService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommonMessageService.class);

    private final Properties defaultProperties = new Properties();

    private final Pattern singleQuoteEscapeRegex;

    private final String defaultLang;
    private final Locale defaultLocale = Locale.ENGLISH;
    private final Locale otherLocales[] = { Locale.FRENCH };

    private final Map<String, Properties> otherProperties = new HashMap<String, Properties>();

    private CommonMessageService fallback;

    protected abstract String getLang();

    public CommonMessageService(CommonMessageService fallback, String name) {
        this.fallback = fallback;
        this.singleQuoteEscapeRegex = Pattern.compile("(?<!')'(?!')");
        try {
            loadAllProperties(name);
        } catch (IOException e) {
            LOGGER.warn("Could not load message properties '"+name+"' : "+e.getMessage());
        }
        this.defaultLang = langId(defaultLocale);
    }
    public CommonMessageService(String name) {
        this(null, name);
    }

    @Override
    public String getMessage(String text) {
        if (isPropertyKey(text)) {
            final String key = extractPropertyKey(text);
            return getMessageFromKey(key);
        }
        return text;
    }
    protected String getMessageFromKey(String key) {
        if (!containsProperty(key)) {
            if (fallback != null) {
                return fallback.getMessageFromKey(key);
            }
            return unkknowMessageForKey(key);
        }
        return getProperty(key);
    }

    @Override
    public String getMessage(String text, Object... args) {
        if (isPropertyKey(text)) {
            final String key = extractPropertyKey(text);
            return getMessageFromKey(key, args);
        }
        return text;
    }
    protected String getMessageFromKey(String key, Object... args) {
        if (!containsProperty(key)) {
            if (fallback != null) {
                return fallback.getMessageFromKey(key, args);
            }
            return unkknowMessageForKey(key);
        }

        final String property = getProperty(key);
        if (singleQuoteEscapeRegex != null) {
            return MessageFormat.format(singleQuoteEscapeRegex.matcher(property).replaceAll("''") , args);
        } else {
            return MessageFormat.format(property, args);
        }
    }

    private boolean containsProperty(String key) {
        final String lang = getLang();
        if ((!lang.equals(defaultLang)) && otherProperties.containsKey(lang)) {
            final Properties properties = otherProperties.get(lang);
            if (properties.containsKey(key)) {
                return true;
            }
        }
        return defaultProperties.containsKey(key);
    }

    private String getProperty(String key) {
        final String lang = getLang();
        if ((!lang.equals(defaultLang)) && otherProperties.containsKey(lang)) {
            final Properties properties = otherProperties.get(lang);
            if (properties.containsKey(key)) {
                return (String)properties.get(key);
            }
        }
        return (String) defaultProperties.get(key);
    }

    private static String langId(Locale locale) {
        return locale.getLanguage().toLowerCase();
    }

    private String unkknowMessageForKey(String key) {
        return "["+key+"]";
    }

    private static boolean isPropertyKey(String text) {
        return (text.startsWith("${") && text.endsWith("}"));
    }
    private static String extractPropertyKey(String text) {
        return text.substring(2,text.length()-1);
    }
    private boolean isEL(String text) {
        return (text.startsWith("#{") && text.endsWith("}"));
    }

    private void loadAllProperties(String propertiesName) throws IOException {
        loadProperties(defaultProperties, propertiesName, null);
        for (Locale locale: otherLocales) {
            final Properties properties = new Properties();
            final String lang = langId(locale);
            loadProperties(properties, propertiesName, lang);
            otherProperties.put(lang, properties);
        }
    }
    private boolean loadProperties(Properties properties, String propertiesName,String lang) throws IOException {
        String resourceDirectory = "net/tetrakoopa/mdu4j/admin/message";
        if (lang != null && !lang.equals("")) {
            resourceDirectory = resourceDirectory+"-"+lang;
        }
        final String resourceName = resourceDirectory+"/"+propertiesName+".properties";
        final InputStream input = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (input == null) {
            throw new IOException("Could not find defaultProperties messages '"+resourceName+"'");
        }
        properties.load(input);
        LOGGER.info("Loaded properties messages '"+resourceName+"'");
        return true;
    }

}
