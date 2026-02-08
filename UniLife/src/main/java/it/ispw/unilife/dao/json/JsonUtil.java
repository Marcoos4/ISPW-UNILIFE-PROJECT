package it.ispw.unilife.dao.json;

import com.google.gson.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JsonUtil {

    private static final Logger logger = Logger.getLogger(JsonUtil.class.getName());
    private static final String JSON_DIR = System.getProperty("user.dir") + File.separator + "unilife_data";
    private static Gson gson;

    private JsonUtil() {}

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                    .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeDeserializer())
                    .create();
        }
        return gson;
    }

    public static String getJsonDir() {
        return JSON_DIR;
    }

    public static File getFile(String fileName) {
        Path dir = Paths.get(JSON_DIR);
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Cannot create JSON data directory", e);
            }
        }

        File file = new File(JSON_DIR, fileName);
        if (!file.exists()) {
            try {
                // Crea file con array vuoto
                try (Writer writer = new FileWriter(file)) {
                    writer.write("[]");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Cannot create JSON file: {0}", fileName);
            }
        }
        return file;
    }

    public static String readFile(File file) {
        try {
            return new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot read JSON file", e);
            return "[]";
        }
    }

    public static void writeFile(File file, String content) {
        try (Writer writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot write JSON file", e);
        }
    }

    // --- Adapter per LocalDateTime ---
    private static class LocalDateTimeSerializer implements JsonSerializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(FORMATTER));
        }
    }

    private static class LocalDateTimeDeserializer implements JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), FORMATTER);
        }
    }
}
