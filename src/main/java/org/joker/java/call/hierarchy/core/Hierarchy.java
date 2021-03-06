package org.joker.java.call.hierarchy.core;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Hierarchy<T> {

    private T target;
    private List<Hierarchy<T>> calls;

    public Hierarchy(T target) {
        this(target, new ArrayList<>());
    }

    public Hierarchy(T target, List<Hierarchy<T>> calls) {
        this.target = target;
        this.calls = calls;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public List<Hierarchy<T>> getCalls() {
        return calls;
    }

    public void setCalls(List<Hierarchy<T>> calls) {
        this.calls = calls;
    }

    public void addCall(Hierarchy<T> call) {
        Function<T, String> function = getFunction();
        String s1 = function.apply(call.getTarget());
        for (Hierarchy<T> hierarchy : calls) {
            String s2 = function.apply(hierarchy.target);
            if (s1.equals(s2)) {
                return;
            }
        }
        calls.add(call);
    }

    public List<String> toStringList() {
        Function<T, String> function = getFunction();
        String prefix = function.apply(target);
        return toStringList(prefix, calls, function);
    }

    public List<String> toStringList(String prefix, List<Hierarchy<T>> calls, Function<T, String> function) {
        List<String> list = new ArrayList<>();
        if (calls.isEmpty()) {
            list.add(prefix);
            return list;
        }
        for (Hierarchy<T> call : calls) {
            String s = String.format("%s -> %s", prefix, function.apply(call.target));
            if (call.getCalls().isEmpty()) {
                list.add(s);
            } else {
                List<String> childList = toStringList(s, call.getCalls(), function);
                list.addAll(childList);
            }
        }
        return list;
    }

    private Function<T, String> getFunction() {
        return declaration -> {
            if (declaration instanceof ResolvedMethodDeclaration resolvedMethodDeclaration) {
                return resolvedMethodDeclaration.getQualifiedName();
            } else {
                System.err.println("function error: " + declaration.toString());
                return "";
            }
        };
    }

}
