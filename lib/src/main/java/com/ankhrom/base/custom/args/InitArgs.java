package com.ankhrom.base.custom.args;

import java.lang.reflect.Method;

import com.ankhrom.base.common.statics.ArgsHelper;
import com.ankhrom.base.common.statics.ObjectHelper;

public class InitArgs {

    private Object parent;
    private Object[] args;

    public InitArgs(Object parent, Object... args) {
        this.parent = parent;

        if (args != null && args.length > 0) {
            this.args = ArgsHelper.preSortArgs(args);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getArg(Class<T> clazz) {

        if (args == null || args.length == 0) {
            return null;
        }

        for (Object arg : args) {
            if (clazz.isInstance(arg)) {
                return (T) arg;
            }
        }

        return null;
    }

    public <T> T getArg(Class<T> clazz, T defaultValue) {

        T arg = getArg(clazz);

        if (arg == null) {
            return defaultValue;
        }

        return arg;
    }

    public void invoke(String methodName) {

        if (parent == null) {
            return;
        }

        try {
            Method[] methods = parent.getClass().getMethods();
            for (Method method : methods) {
                if (ObjectHelper.equals(methodName, method.getName())) {
                    Class<?>[] paramTypes = method.getParameterTypes();

                    Object[] methodParams = null;
                    if (paramTypes != null && args != null) {

                        if (paramTypes.length != args.length) {
                            continue;
                        }

                        methodParams = ArgsHelper.strictSortArgs(paramTypes, args);

                        if (methodParams == null) {
                            continue;
                        }
                    } else {
                        if (!ArgsHelper.isParamLengthEqual(paramTypes, args)) {
                            continue;
                        }
                    }

                    method.invoke(parent, methodParams);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void invoke(String methodName, Class<?>... paramTypes) {

        if (parent == null) {
            return;
        }

        try {
            Method method = parent.getClass().getMethod(methodName, paramTypes);

            Object[] methodArgs = ArgsHelper.sortArgs(paramTypes, args);

            if (ArgsHelper.isParamLengthEqual(paramTypes, methodArgs)) {
                method.invoke(parent, methodArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getParent() {
        return parent;
    }

    public void setParent(Object parent) {
        this.parent = parent;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }
}
