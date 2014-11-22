package com.devsmart.staple;


import com.google.common.base.Joiner;

import java.util.Arrays;
import java.util.Collection;

public class Namespace {

    public static Namespace defaultNameSpace = new Namespace("");

    private String[] paths;

    public Namespace(String path) {
        if("".equals(path)){
            paths = new String[0];
        } else {
            paths = path.split(".");
        }
    }

    public Namespace(Collection<String> path) {
        paths = path.toArray(new String[path.size()]);
    }

    @Override
    public boolean equals(Object obj) {
        boolean retval = false;
        if(obj instanceof Namespace) {
            retval = Arrays.equals(paths, ((Namespace) obj).paths);
        }
        return retval;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return Joiner.on(".").join(paths);
    }

    public String[] getPaths() {
        String[] retval = new String[paths.length];
        System.arraycopy(paths, 0, retval, 0, paths.length);
        return retval;
    }
}
