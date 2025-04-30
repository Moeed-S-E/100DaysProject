package main.model;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CppMemoryVisualizer {
    private static long currentAddress = 0x1000; // Starting address
    private static final Random random = new Random();

    public static String parseCppCode(String code) {
        StringBuilder visualization = new StringBuilder("Memory Visualization:\n\n=== Stack ===\n");
        visualization.append("Address    Type       Name            Value           Size\n");
        visualization.append("----------------------------------------------------------\n");

        // Map of primitive types to sizes (in bytes)
        String[] lines = code.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Handle primitives: e.g., "int a = 5;", "char c = 'A';", "bool b = true;"
            Pattern primitivePattern = Pattern.compile("(int|char|float|double|bool|short|long|unsigned\\s+int)\\s+(\\w+)\\s*=\\s*([^;]+)\\s*;");
            Matcher primitiveMatcher = primitivePattern.matcher(line);
            if (primitiveMatcher.matches()) {
                String type = primitiveMatcher.group(1);
                String name = primitiveMatcher.group(2);
                String value = primitiveMatcher.group(3).trim();
                int size = getPrimitiveSize(type);
                visualization.append(String.format("0x%04X     %-10s %-15s %-15s %-5d\n", 
                    currentAddress, type, name, value, size));
                currentAddress += size + (random.nextInt(3) * 4); // Random padding (0, 4, 8 bytes)
                continue;
            }

            // Handle char[]: e.g., "char str[] = \"hello\";"
            Pattern charArrayPattern = Pattern.compile("char\\s+(\\w+)\\s*\\[\\s*\\]\\s*=\\s*\"([^\"]*)\"\\s*;");
            Matcher charArrayMatcher = charArrayPattern.matcher(line);
            if (charArrayMatcher.matches()) {
                String name = charArrayMatcher.group(1);
                String value = charArrayMatcher.group(2);
                int size = value.length() + 1; // Include null terminator
                visualization.append(String.format("0x%04X     char[]     %-15s %-15s %-5d\n", 
                    currentAddress, name, "\"" + value + "\"", size));
                currentAddress += size + (random.nextInt(3) * 4);
                continue;
            }

            // Handle fixed-size arrays: e.g., "int arr[3] = {1, 2, 3};"
            Pattern arrayPattern = Pattern.compile("(int|float|double|char)\\s+(\\w+)\\s*\\[\\s*(\\d+)\\s*\\]\\s*=\\s*\\{([^}]+)\\}\\s*;");
            Matcher arrayMatcher = arrayPattern.matcher(line);
            if (arrayMatcher.matches()) {
                String type = arrayMatcher.group(1);
                String name = arrayMatcher.group(2);
                int count = Integer.parseInt(arrayMatcher.group(3));
                String values = arrayMatcher.group(4).trim();
                int elementSize = getPrimitiveSize(type);
                int size = elementSize * count;
                visualization.append(String.format("0x%04X     %s[%d]    %-15s {%s}          %-5d\n", 
                    currentAddress, type, count, name, values, size));
                currentAddress += size + (random.nextInt(3) * 4);
                continue;
            }

            // Handle struct: e.g., "struct Point { int x; int y; }; Point p = {1, 2};"
            Pattern structPattern = Pattern.compile("struct\\s+(\\w+)\\s*\\{([^}]+)\\}\\s*;\\s*\\1\\s+(\\w+)\\s*=\\s*\\{([^}]+)\\}\\s*;");
            Matcher structMatcher = structPattern.matcher(line);
            if (structMatcher.matches()) {
                String structName = structMatcher.group(1);
                String fields = structMatcher.group(2);
                String varName = structMatcher.group(3);
                String values = structMatcher.group(4).trim();
                int size = calculateStructSize(fields);
                visualization.append(String.format("0x%04X     struct %-7s %-15s {%s}          %-5d\n", 
                    currentAddress, structName, varName, values, size));
                currentAddress += size + (random.nextInt(3) * 4);
                continue;
            }
        }

        // Reset address for next visualization
        currentAddress = 0x1000;
        return visualization.toString();
    }

    private static int getPrimitiveSize(String type) {
        switch (type) {
            case "char":
            case "bool":
                return 1;
            case "short":
                return 2;
            case "int":
            case "float":
            case "unsigned int":
                return 4;
            case "long":
            case "double":
                return 8;
            default:
                return 4; // Default for unknown types
        }
    }

    private static int calculateStructSize(String fields) {
        int size = 0;
        String[] fieldDecls = fields.split(";");
        for (String field : fieldDecls) {
            field = field.trim();
            if (field.isEmpty()) continue;
            Pattern fieldPattern = Pattern.compile("(int|char|float|double|bool|short|long|unsigned\\s+int)\\s+\\w+");
            Matcher fieldMatcher = fieldPattern.matcher(field);
            if (fieldMatcher.matches()) {
                String type = fieldMatcher.group(1);
                size += getPrimitiveSize(type);
            }
        }
        return size;
    }
}