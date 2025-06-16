package main.diagram;

import java.util.List;
import java.util.StringJoiner;

import main.model.ClassInfo;
import main.model.FieldInfo;
import main.model.MethodInfo;

public class PlantUMLGenerator {
    public static String generate(List<ClassInfo> classes) {
        StringBuilder sb = new StringBuilder();
        sb.append("@startuml\n");
        for (ClassInfo clazz : classes) {
            if (clazz.isInterface) {
                sb.append("interface ").append(clazz.name).append(" {\n");
            } else if (clazz.isAbstract) {
                sb.append("abstract class ").append(clazz.name).append(" {\n");
            } else {
                sb.append("class ").append(clazz.name).append(" {\n");
            }
            for (FieldInfo f : clazz.fields) {
                sb.append("    ").append(f.access).append(" ").append(f.name).append(" : ").append(f.type).append("\n");
            }
            for (MethodInfo m : clazz.methods) {
                StringJoiner params = new StringJoiner(", ");
                for (String p : m.params) params.add(p);
                sb.append("    ").append(m.access).append(" ").append(m.name).append("(").append(params).append(")").append(" : ").append(m.returnType).append("\n");
            }
            sb.append("}\n");
        }
        // Relationships
        for (ClassInfo clazz : classes) {
            if (clazz.superClass != null) {
                sb.append(clazz.name).append(" --|> ").append(clazz.superClass).append("\n");
            }
            for (String iface : clazz.interfaces) {
                sb.append(clazz.name).append(" ..|> ").append(iface).append("\n");
            }
        }
        sb.append("@enduml");
        return sb.toString();
    }
}