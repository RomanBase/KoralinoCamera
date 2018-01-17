package com.ankhrom.base.common.statics;

import android.support.annotation.Nullable;

import com.ankhrom.base.custom.args.InitArgs;

public final class ArgsHelper {

    @Nullable
    public static Object[] strictSortArgs(Class<?>[] params, Object[] inputArgs) {

        if (params == null || inputArgs == null || params.length != inputArgs.length) {
            return null;
        }

        Object lockedArg = new Object();

        Object[] sortedArgs = new Object[params.length];
        Object[] args = new Object[inputArgs.length];
        System.arraycopy(inputArgs, 0, args, 0, inputArgs.length);

        int index = 0;
        for (Class param : params) {
            boolean matchFound = false;

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];

                if (arg == lockedArg) {
                    continue;
                }

                if (arg == null || ((Class<?>) param).isAssignableFrom(arg.getClass()) || param.isInstance(arg)) {
                    matchFound = true;
                    sortedArgs[index++] = arg;
                    args[i] = lockedArg;
                    break;
                }
            }

            if (!matchFound) {
                return null;
            }
        }

        return sortedArgs;
    }

    @Nullable
    public static Object[] sortArgs(Class<?>[] params, Object[] inputArgs) {

        if (inputArgs == null || inputArgs.length == 0) {
            if (params == null) {
                return null;
            } else {
                return new Object[params.length];
            }
        }

        Object lockedArg = new Object();

        Object[] sortedArgs = new Object[params.length];
        Object[] args = new Object[inputArgs.length];
        System.arraycopy(inputArgs, 0, args, 0, inputArgs.length);

        int index = 0;
        for (Class param : params) {
            boolean matchFound = false;

            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];

                if (arg == lockedArg) {
                    continue;
                }

                if (arg == null || ((Class<?>) param).isAssignableFrom(arg.getClass()) || param.isInstance(arg)) {
                    matchFound = true;
                    sortedArgs[index++] = arg;
                    args[i] = lockedArg;
                    break;
                }
            }

            if (!matchFound) {
                sortedArgs[index++] = null;
            }
        }

        return sortedArgs;
    }

    //bubble bubble
    public static Object[] preSortArgs(Object[] args) {

        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < args.length - i - 1; j++) {
                Object firstArg = args[j];
                Object secondArg = args[j + 1];
                if (firstArg == null) { // bubble nulls to end
                    if (secondArg != null) {
                        args[j] = secondArg;
                        args[j + 1] = null;
                    }
                } else {
                    if (secondArg != null) { // bubble classes with same super-super class (it's dummy - just check number of super classes)
                        Class<?> firstArgClass = firstArg.getClass();
                        Class<?> secondArgClass = secondArg.getClass();
                        Class<?> firstArgSuperClass = getSuperClass(firstArgClass);
                        Class<?> secondArgSuperClass = getSuperClass(secondArgClass);
                        if (firstArgSuperClass == secondArgSuperClass) {
                            int c1 = getSupperClassCount(firstArgClass);
                            int c2 = getSupperClassCount(secondArgClass);
                            if (getSupperClassCount(firstArgClass) > getSupperClassCount(secondArgClass)) {
                                args[j] = secondArg;
                                args[j + 1] = firstArg;
                            }
                        }
                    }
                }
            }
        }

        return args;
    }

    public static Class<?> getSuperClass(Class<?> clazz) {

        Class c = clazz.getSuperclass();

        return (c == null || c == Object.class) ? clazz : getSuperClass(c);
    }

    public static int getSupperClassCount(Class<?> clazz) {

        if (clazz == null || clazz == Object.class) {
            return 0;
        }

        return 1 + getSupperClassCount(clazz.getSuperclass());
    }

    public static boolean isParamLengthEqual(Object[] paramTypes, Object[] args) {

        if (paramTypes == null || paramTypes.length == 0) {
            if (args != null && args.length > 0) {
                return false;
            }
        } else {
            if (args != null) {
                if (args.length != paramTypes.length) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean contains(Object[] args, Class<?> clazz) {

        for (Object o : args) {
            if (clazz.isInstance(o)) {
                return true;
            }
        }

        return false;
    }

    public static Object[] addArg(Object[] args, Object arg) {

        if (args == null) {
            return new Object[]{arg};
        }

        Object[] output = new Object[args.length + 1];

        System.arraycopy(args, 0, output, 0, args.length);
        output[args.length] = arg;

        return output;
    }

    public static <T> T getArg(Class<T> clazz, Object[] args, T defaultValue) {

        if (args == null || args.length == 0) {
            return defaultValue;
        }

        return new InitArgs(null, args).getArg(clazz, defaultValue);
    }
}
