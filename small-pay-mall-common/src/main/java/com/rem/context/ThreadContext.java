package com.rem.context;


public class ThreadContext {

    private static ThreadLocal<String> threadLocal = ThreadLocal.withInitial(String::new);

    public static void setCurrentThread(String openid) {
        threadLocal.set(openid);
    }

    public static String getThreadLocal() {
        return threadLocal.get();
    }

    public static void removeCurrentThread() {
        threadLocal.remove();
    }
}
