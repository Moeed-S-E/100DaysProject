package main.model;

import java.util.ArrayList;
import java.util.List;

public class ClassInfo {
    public String name;
    public boolean isAbstract;
    public boolean isInterface;
    public String superClass;
    public List<String> interfaces = new ArrayList<>();
    public List<FieldInfo> fields = new ArrayList<>();
    public List<MethodInfo> methods = new ArrayList<>();
}
