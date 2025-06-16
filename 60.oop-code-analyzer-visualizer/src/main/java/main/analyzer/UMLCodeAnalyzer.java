package main.analyzer;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.Modifier;

import main.model.ClassInfo;
import main.model.FieldInfo;
import main.model.MethodInfo;


import java.util.*;

public class UMLCodeAnalyzer {
    public static List<ClassInfo> analyze(String code) {
        List<ClassInfo> classes = new ArrayList<>();
        CompilationUnit cu = StaticJavaParser.parse(code);

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(clazz -> {
            ClassInfo info = new ClassInfo();
            info.name = clazz.getNameAsString();
            info.isAbstract = clazz.isAbstract();
            info.isInterface = clazz.isInterface();
            if (!clazz.getExtendedTypes().isEmpty())
                info.superClass = clazz.getExtendedTypes(0).getNameAsString();
            clazz.getImplementedTypes().forEach(impl -> info.interfaces.add(impl.getNameAsString()));

            // Fields
            for (FieldDeclaration field : clazz.getFields()) {
                String access = getAccessSymbol(field);
                for (VariableDeclarator var : field.getVariables()) {
                    FieldInfo f = new FieldInfo();
                    f.name = var.getNameAsString();
                    f.type = var.getTypeAsString();
                    f.access = access;
                    info.fields.add(f);
                }
            }
            // Methods
            for (MethodDeclaration method : clazz.getMethods()) {
                MethodInfo m = new MethodInfo();
                m.name = method.getNameAsString();
                m.returnType = method.getTypeAsString();
                m.access = getAccessSymbol(method);
                m.isAbstract = method.isAbstract();
                m.isOverride = method.getAnnotationByName("Override").isPresent();
                m.params = new ArrayList<>();
                method.getParameters().forEach(p -> m.params.add(p.getTypeAsString()));
                info.methods.add(m);
            }
            classes.add(info);
        });
        return classes;
    }

    private static String getAccessSymbol(BodyDeclaration<?> decl) {
        if (decl instanceof FieldDeclaration) {
            FieldDeclaration node = (FieldDeclaration) decl;
            if (node.hasModifier(Modifier.Keyword.PRIVATE)) return "-";
            if (node.hasModifier(Modifier.Keyword.PROTECTED)) return "#";
            if (node.hasModifier(Modifier.Keyword.PUBLIC)) return "+";
            return "~"; // package-private
        } else if (decl instanceof MethodDeclaration) {
            MethodDeclaration node = (MethodDeclaration) decl;
            if (node.hasModifier(Modifier.Keyword.PRIVATE)) return "-";
            if (node.hasModifier(Modifier.Keyword.PROTECTED)) return "#";
            if (node.hasModifier(Modifier.Keyword.PUBLIC)) return "+";
            return "~"; // package-private
        }
        return "~";
    }

}
