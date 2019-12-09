package de.escapefromtarkov.eftwikicrawler.adapter.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpUtil {

    private static final String DOMAIN_NAME_PATTERN = "([a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,15}";
    private static Pattern patern = Pattern.compile(DOMAIN_NAME_PATTERN);

    public static String getDomainName(String url) {
        String domainName = "";
        Matcher matcher = patern.matcher(url);
        if (matcher.find()) {
            domainName = matcher.group(0).toLowerCase().trim();
        }
        return domainName;
    }

}
