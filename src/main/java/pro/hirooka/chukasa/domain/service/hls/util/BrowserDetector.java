package pro.hirooka.chukasa.domain.service.hls.util;

import pro.hirooka.chukasa.domain.config.common.type.WebBrowserType;

public class BrowserDetector {

    private static final String SAFARI = "Safari/";
    private static final String CHROME = "Chrome/";
    private static final String CHROMIUM = "Chromium/";
    private static final String EDGE = "Edge/";
    private static final String IE = "Trident/7";
    private static final String FIREFOX = "Firefox/";
    private static final String SEAMONKEY = "Seamonkey/";
    private static final String OCULUS = "OculusBrowser/";

    private static final String ANDROID = "Linux; Android ";

    private BrowserDetector(){
    }

    static WebBrowserType getBrowserType(String userAgent) {
        if(isSafari(userAgent)){
            return WebBrowserType.SAFARI;
        }else if(isChrome(userAgent)){
            return WebBrowserType.CHROME;
        }else if(isEdge(userAgent)) {
            return WebBrowserType.EDGE;
        }else if(isOculus(userAgent)){
            return WebBrowserType.OCULUS;
        }else if(isIE(userAgent)){
            return WebBrowserType.IE;
        }else if(isFirefox(userAgent)){
            return WebBrowserType.FIREFOX;
        }
        return WebBrowserType.UNKNOWN;
    }

    static boolean isSafari(String userAgent) {
        return userAgent.contains(SAFARI) && !userAgent.contains(CHROME) && !userAgent.contains(CHROMIUM) && !userAgent.contains(OCULUS);
    }

    static boolean isChrome(String userAgent) {
        return userAgent.contains(CHROME) && !userAgent.contains(CHROMIUM) && !userAgent.contains(OCULUS);
    }

    static boolean isEdge(String userAgent) {
        return userAgent.contains(EDGE);
    }

    static boolean isIE(String userAgent) {
        return userAgent.contains(IE);
    }

    static boolean isFirefox(String userAgent) {
        return userAgent.contains(FIREFOX) && !userAgent.contains(SEAMONKEY);
    }

    static boolean isOculus(String userAgent){
        return userAgent.contains(OCULUS);
    }

    public static boolean isNativeSupported(String userAgent) {
        return isSafari(userAgent) || isEdge(userAgent);
    }

    public static boolean isAlternativeSupported(String userAgent) {
        return isChrome(userAgent) || isOculus(userAgent) || isIE(userAgent) || isFirefox(userAgent);
    }

    static boolean isAndroidChrome(String userAgent){
        return isChrome(userAgent) && userAgent.contains(ANDROID);
    }
}
