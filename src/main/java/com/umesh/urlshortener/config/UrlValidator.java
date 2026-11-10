package com.umesh.urlshortener.config;

import com.umesh.urlshortener.exceptions.FieldValidationException;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.InetAddress;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlValidator {

    private static final int MAX_LENGTH = 2048;

    private static final String FIELD = "originalUrl";

    private static final Pattern IPV4 = Pattern.compile(
            "^(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})$"
    );

    private final boolean blockPrivateHosts;

    private final String ownHost;

    public UrlValidator(ShortenerProperties props) {

        this.blockPrivateHosts = props.blockPrivateHosts();

        this.ownHost = extractHost(props.baseUrl());

        if (this.ownHost == null) {
            throw new IllegalStateException(
                    "app.shortener.base-url must contain a valid host"
            );
        }
    }

    public String validate(String raw) {

        if (raw == null || raw.isBlank()) {
            throw fail("URL is required");
        }

        String url = raw.trim();

        if (url.length() > MAX_LENGTH) {
            throw fail("URL must be at most " + MAX_LENGTH + " characters");
        }

        URI uri;

        try {
            uri = new URI(url);
        } catch (URISyntaxException ex) {
            throw fail("URL is not valid");
        }

        // Only HTTP and HTTPS are allowed.
        String scheme = uri.getScheme();

        if (scheme == null ||
                !(scheme.equalsIgnoreCase("http")
                        || scheme.equalsIgnoreCase("https"))) {

            throw fail("Only http and https URLs are allowed");
        }

        // Require an actual hostname.
        String host = uri.getHost();

        if (host == null || host.isBlank()) {
            throw fail("URL must contain a valid host name");
        }

        // Reject embedded username/password.
        if (uri.getRawUserInfo() != null) {
            throw fail("URLs with embedded credentials are not allowed");
        }

        host = normalizeHost(host);

        // Prevent redirect loops back to this service.
        if (host.equals(ownHost)) {
            throw fail("You cannot shorten a link that points to this service");
        }

        // Optionally block local/private network destinations.
        if (blockPrivateHosts && isPrivateHost(host)) {
            throw fail("Links to local or private network addresses are not allowed");
        }

        return url;
    }

    private static String normalizeHost(String host) {

        String normalized = host.toLowerCase(Locale.ROOT);

        // Remove IPv6 brackets for address classification.
        if (normalized.startsWith("[") && normalized.endsWith("]")) {
            normalized = normalized.substring(1, normalized.length() - 1);
        }

        // A trailing DNS dot represents the same hostname.
        if (normalized.endsWith(".")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }

        return normalized;
    }

    private static boolean isPrivateHost(String host) {

        // Local hostnames.
        if (host.equals("localhost")
                || host.endsWith(".localhost")
                || host.endsWith(".local")
                || host.endsWith(".internal")
                || host.endsWith(".localdomain")) {

            return true;
        }

        // IPv4 literal.
        Matcher matcher = IPV4.matcher(host);

        if (matcher.matches()) {
            return isPrivateIpv4(matcher);
        }

        // IPv6 literal. Only call this for colon-containing literals;
        // ordinary domain names are never sent to DNS here.
        if (host.contains(":")) {
            return isPrivateIpv6(host);
        }

        return false;
    }

    private static boolean isPrivateIpv4(Matcher matcher) {

        int a = Integer.parseInt(matcher.group(1));
        int b = Integer.parseInt(matcher.group(2));
        int c = Integer.parseInt(matcher.group(3));
        int d = Integer.parseInt(matcher.group(4));

        // Reject invalid IPv4 octets.
        if (a > 255 || b > 255 || c > 255 || d > 255) {
            return true;
        }

        return a == 0
                || a == 10
                || a == 127
                || (a == 169 && b == 254)
                || (a == 172 && b >= 16 && b <= 31)
                || (a == 192 && b == 168)
                || (a == 100 && b >= 64 && b <= 127)
                || (a == 198 && (b == 18 || b == 19))
                || a >= 224;
    }

    private static boolean isPrivateIpv6(String host) {

        try {

            // The input contains ':' and is therefore an IPv6 literal,
            // not an ordinary DNS hostname.
            InetAddress address = InetAddress.getByName(host);

            byte[] bytes = address.getAddress();

            if (bytes.length != 16) {
                return isPrivateAddress(address);
            }

            // IPv6 loopback and unspecified addresses.
            if (address.isLoopbackAddress()
                    || address.isAnyLocalAddress()
                    || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress()
                    || address.isMulticastAddress()) {

                return true;
            }

            // Unique local IPv6 addresses: fc00::/7.
            return (bytes[0] & 0xFE) == 0xFC;

        } catch (Exception ex) {
            // Malformed address should not be accepted as public.
            return true;
        }
    }

    private static boolean isPrivateAddress(InetAddress address) {

        return address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress();
    }

    private static String extractHost(String url) {

        try {

            URI uri = new URI(url);

            String host = uri.getHost();

            return host == null
                    ? null
                    : normalizeHost(host);

        } catch (URISyntaxException | NullPointerException ex) {
            return null;
        }
    }

    private static FieldValidationException fail(String message) {

        return new FieldValidationException(FIELD, message);
    }
}
