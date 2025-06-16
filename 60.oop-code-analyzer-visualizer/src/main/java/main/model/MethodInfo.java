package main.model;

import java.util.List;

public class MethodInfo {
    public String name;
    public String returnType;
    public String access; // "+", "-", "#"
    public boolean isAbstract;
    public boolean isOverride;
    public List<String> params;
}
