package com.zwz.common.gray.keeper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GrayTagKeeper {

    public static final String GRAY_TAG = "graytag";

    /**
     * 支持父子线程之间的数据传递 线程池的情况下需要兼容可以考虑阿里的事务线程
     */
    private static final ThreadLocal<String> CONTEXT = new ThreadLocal<>();

    public static void setGrayTag(String grayTag) {
        log.debug("GrayTagKeeper setGrayTag" + grayTag);
        CONTEXT.set(grayTag);
    }

    public static String getGrayTag() {
        return CONTEXT.get();
    }

    public static void clear() {
        log.debug("GrayTagKeeper clear");
        CONTEXT.remove();
    }
}
