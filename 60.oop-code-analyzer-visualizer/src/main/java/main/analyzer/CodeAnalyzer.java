package main.analyzer;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.*;

import main.model.AnalysisResult;
import main.model.SummaryItem;

public class CodeAnalyzer {
    public static AnalysisResult analyze(String code) {
        AnalysisResult result = new AnalysisResult();
        try {
            CompilationUnit cu = StaticJavaParser.parse(code);

            cu.accept(new VoidVisitorAdapter<AnalysisResult>() {
                @Override
                public void visit(ClassOrInterfaceDeclaration cid, AnalysisResult res) {
                    String className = cid.getNameAsString();

                    // Encapsulation: private/protected fields and getters/setters
                    for (FieldDeclaration field : cid.getFields()) {
                        if (field.isPrivate() || field.isProtected()) {
                            res.getSummaryItems().add(
                                new SummaryItem(className, "Encapsulation", field.getVariables().toString())
                            );
                        }
                    }
                    for (MethodDeclaration method : cid.getMethods()) {
                        if (isGetterOrSetter(method)) {
                            res.getSummaryItems().add(
                                new SummaryItem(className, "Encapsulation", method.getNameAsString())
                            );
                        }
                    }

                    // Inheritance: extends/implements
                    cid.getExtendedTypes().forEach(ext -> res.getSummaryItems().add(
                        new SummaryItem(className, "Inheritance", "extends " + ext.getNameAsString())
                    ));
                    cid.getImplementedTypes().forEach(impl -> res.getSummaryItems().add(
                        new SummaryItem(className, "Inheritance", "implements " + impl.getNameAsString())
                    ));

                    // Abstraction: abstract class/method, interface
                    if (cid.isAbstract()) {
                        res.getSummaryItems().add(
                            new SummaryItem(className, "Abstraction", "abstract class")
                        );
                    }
                    if (cid.isInterface()) {
                        res.getSummaryItems().add(
                            new SummaryItem(className, "Abstraction", "interface")
                        );
                    }
                    super.visit(cid, res);
                }

                @Override
                public void visit(MethodDeclaration md, AnalysisResult res) {
                    // Polymorphism: method overloading/overriding
                    if (md.getAnnotationByName("Override").isPresent()) {
                        res.getSummaryItems().add(
                            new SummaryItem(md.getParentNode().get().toString(), "Polymorphism", "Overridden " + md.getNameAsString())
                        );
                    }
                    // Overloading: same name, different params (simplified)
                    // Add logic as needed
                    super.visit(md, res);
                }
            }, result);

            // Swing parsing: extract Swing components (see below)

        } catch (Exception e) {
            // Handle parse errors
        }
        return result;
    }

    private static boolean isGetterOrSetter(MethodDeclaration method) {
        String name = method.getNameAsString();
        return (name.startsWith("get") || name.startsWith("set")) && method.isPublic();
    }
}
