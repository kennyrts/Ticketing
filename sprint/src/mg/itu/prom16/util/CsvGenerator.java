package mg.itu.prom16.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV generator utility for Sprint framework
 * Converts ModelView data to CSV format
 */
public class CsvGenerator {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Generate CSV from ModelView data
     * @param modelView The ModelView containing data to export
     * @param includeHeaders Whether to include column headers
     * @param delimiter The delimiter to use (default: comma)
     * @return byte array containing the CSV content
     */
    public static byte[] generateCsv(ModelView modelView, boolean includeHeaders, String delimiter) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(baos, "UTF-8");
        
        HashMap<String, Object> data = modelView.getData();
        
        // Process each data entry
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            // Skip common web attributes
            if (key.equals("error") || key.equals("success") || key.equals("message")) {
                continue;
            }
            
            if (value instanceof List) {
                List<?> list = (List<?>) value;
                if (!list.isEmpty()) {
                    writeCsvForList(writer, key, list, includeHeaders, delimiter);
                }
            } else if (value != null) {
                writeCsvForSingleObject(writer, key, value, includeHeaders, delimiter);
            }
        }
        
        writer.flush();
        writer.close();
        
        return baos.toByteArray();
    }
    
    /**
     * Write CSV for a list of objects
     */
    private static void writeCsvForList(Writer writer, String listName, List<?> list, 
                                       boolean includeHeaders, String delimiter) throws IOException {
        
        if (list.isEmpty()) return;
        
        Object firstItem = list.get(0);
        
        // Write section header
        writer.write("# " + formatKey(listName) + "\n");
        
        if (isPrimitiveOrString(firstItem)) {
            // Simple list of primitives
            if (includeHeaders) {
                writer.write(formatKey(listName) + "\n");
            }
            for (Object item : list) {
                writer.write(escapeCsvValue(item.toString()) + "\n");
            }
        } else {
            // List of objects
            Field[] fields = firstItem.getClass().getDeclaredFields();
            
            // Write headers
            if (includeHeaders) {
                boolean first = true;
                for (Field field : fields) {
                    if (!first) writer.write(delimiter);
                    writer.write(formatKey(field.getName()));
                    first = false;
                }
                writer.write("\n");
            }
            
            // Write data rows
            for (Object item : list) {
                boolean first = true;
                for (Field field : fields) {
                    if (!first) writer.write(delimiter);
                    
                    try {
                        field.setAccessible(true);
                        Object fieldValue = field.get(item);
                        writer.write(escapeCsvValue(formatValue(fieldValue)));
                    } catch (IllegalAccessException e) {
                        writer.write("");
                    }
                    first = false;
                }
                writer.write("\n");
            }
        }
        
        writer.write("\n"); // Empty line between sections
    }
    
    /**
     * Write CSV for a single object
     */
    private static void writeCsvForSingleObject(Writer writer, String objectName, Object obj, 
                                               boolean includeHeaders, String delimiter) throws IOException {
        
        writer.write("# " + formatKey(objectName) + "\n");
        
        if (isPrimitiveOrString(obj)) {
            if (includeHeaders) {
                writer.write(formatKey(objectName) + "\n");
            }
            writer.write(escapeCsvValue(obj.toString()) + "\n");
        } else {
            Field[] fields = obj.getClass().getDeclaredFields();
            
            // Write as key-value pairs
            if (includeHeaders) {
                writer.write("Property" + delimiter + "Value\n");
            }
            
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    Object fieldValue = field.get(obj);
                    
                    writer.write(escapeCsvValue(formatKey(field.getName())));
                    writer.write(delimiter);
                    writer.write(escapeCsvValue(formatValue(fieldValue)));
                    writer.write("\n");
                } catch (IllegalAccessException e) {
                    // Skip inaccessible fields
                }
            }
        }
        
        writer.write("\n"); // Empty line between sections
    }
    
    /**
     * Format a value for CSV output
     */
    private static String formatValue(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof Date) {
            return DATE_FORMAT.format((Date) value);
        } else if (value instanceof Boolean) {
            return ((Boolean) value) ? "Yes" : "No";
        } else {
            return value.toString();
        }
    }
    
    /**
     * Escape CSV value (handle commas, quotes, newlines)
     */
    private static String escapeCsvValue(String value) {
        if (value == null) return "";
        
        // If value contains delimiter, quotes, or newlines, wrap in quotes
        if (value.contains(",") || value.contains("\"") || value.contains("\n") || value.contains("\r")) {
            // Escape existing quotes by doubling them
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        }
        
        return value;
    }
    
    /**
     * Format a key name for display
     */
    private static String formatKey(String key) {
        // Convert camelCase to Title Case
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        
        for (char c : key.toCharArray()) {
            if (c == '_') {
                result.append(' ');
                capitalizeNext = true;
            } else if (Character.isUpperCase(c) && result.length() > 0) {
                result.append(' ').append(c);
                capitalizeNext = false;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
    
    /**
     * Check if an object is a primitive type or String
     */
    private static boolean isPrimitiveOrString(Object obj) {
        if (obj == null) return true;
        
        Class<?> clazz = obj.getClass();
        return clazz.isPrimitive() || 
               clazz == String.class || 
               clazz == Integer.class || 
               clazz == Long.class || 
               clazz == Double.class || 
               clazz == Float.class || 
               clazz == Boolean.class ||
               clazz == Date.class ||
               Number.class.isAssignableFrom(clazz);
    }
}