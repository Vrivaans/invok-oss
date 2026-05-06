package io.invok.core.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.List;

@Component
public class SecurityValidator {

    @Value("${security.ssrf.allow-private-networks:false}")
    private boolean allowPrivateNetworks;

    private static final List<String> BLOCKED_HOSTS = List.of(
            "169.254.169.254", // AWS metadata
            "metadata.google.internal" // GCP metadata
    );

    private static final List<String> PRIVATE_RANGES = List.of(
            "10.", "192.168.", "172.16.", "172.17.", "172.18.", "172.19.", "172.20.", "172.21.", "172.22.", "172.23.",
            "172.24.", "172.25.", "172.26.", "172.27.", "172.28.", "172.29.", "172.30.", "172.31.");

    public void validateUrl(String urlString) {
        try {
            if (urlString == null || urlString.isBlank()) {
                throw new IllegalArgumentException("Invalid URL: URL cannot be empty");
            }
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                throw new IllegalArgumentException(
                        "Invalid URL: Protocol (http/https) is missing. Make sure to use http:// or https://");
            }

            URI uri = new URI(urlString);
            String host = uri.getHost();

            if (host == null) {
                throw new IllegalArgumentException("Invalid URL: Host is missing");
            }
            // Always block cloud metadata services
            if (BLOCKED_HOSTS.contains(host)) {
                throw new SecurityException("Access to restricted host is denied: " + host);
            }

            // If private networks are allowed, skip further checks
            if (allowPrivateNetworks) {
                return;
            }

            // Resolve IP to check for private ranges (SSRF protection)
            InetAddress address = InetAddress.getByName(host);
            String ip = address.getHostAddress();

            if (address.isLoopbackAddress() || address.isAnyLocalAddress() || address.isLinkLocalAddress()
                    || address.isSiteLocalAddress()) {
                throw new SecurityException("Access to local/private network is denied: " + host + " (" + ip + ")");
            }

            // Additional check for private ranges by string prefix if isSiteLocalAddress
            // misses some
            for (String range : PRIVATE_RANGES) {
                if (ip.startsWith(range)) {
                    throw new SecurityException("Access to private network range is denied: " + host + " (" + ip + ")");
                }
            }

        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Could not resolve host: " + e.getMessage());
        } catch (Exception e) {
            if (e instanceof SecurityException || e instanceof IllegalArgumentException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Error validating URL: " + e.getMessage(), e);
        }
    }
}
