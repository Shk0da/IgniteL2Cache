package com.github.shk0da.demo.util.ignite;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.spi.IgniteSpiException;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinderAdapter;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Setter
public class TcpDiscoveryKubernetesIpFinder extends TcpDiscoveryIpFinderAdapter {

    /**
     * Trust manager.
     */
    private TrustManager[] trustAll = new TrustManager[]{
            new X509TrustManager() {
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }

                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
    };

    /**
     * Host verifier.
     */
    private HostnameVerifier trustAllHosts = (hostname, session) -> true;

    /**
     * Ignite's Kubernetes Service name.
     */
    private String serviceName = "ignite";

    /**
     * Ignite Pod setNamespace name.
     */
    private String namespace = "default";

    /**
     * Kubernetes API server URL in a string form.
     */
    private String master = "https://kubernetes.default.svc.cluster.local:443";

    /**
     * Account token location.
     */
    private String accountToken = "/var/run/secrets/kubernetes.io/serviceaccount/token";

    /**
     * Kubernetes API server URL.
     */
    private URL url;

    /**
     * SSL context
     */
    private SSLContext ctx;

    /**
     * Creates an instance of Kubernetes IP finder.
     */
    public TcpDiscoveryKubernetesIpFinder() {
        setShared(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<InetSocketAddress> getRegisteredAddresses() throws IgniteSpiException {
        init();
        Collection<InetSocketAddress> addresses = new ArrayList<>();
        try {
            log.debug("Getting Apache Ignite endpoints from: " + url);

            HttpURLConnection httpURLConnection;
            if ("https".equals(url.getProtocol())) {
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setHostnameVerifier(trustAllHosts);
                conn.setSSLSocketFactory(ctx.getSocketFactory());
                conn.addRequestProperty("Authorization", "Bearer " + serviceAccountToken(accountToken));
                httpURLConnection = conn;
            } else {
                httpURLConnection = (HttpURLConnection) url.openConnection();
            }

            // Sending the request and processing a response.
            ObjectMapper mapper = new ObjectMapper();
            Endpoints endpoints = mapper.readValue(httpURLConnection.getInputStream(), Endpoints.class);
            if (endpoints != null) {
                if (endpoints.subsets != null && !endpoints.subsets.isEmpty()) {
                    for (Subset subset : endpoints.subsets) {
                        if (subset.addresses != null && !subset.addresses.isEmpty()) {
                            for (Address address : subset.addresses) {
                                addresses.add(new InetSocketAddress(address.ip, 0));
                                log.debug("Added an address to the list: " + address.ip);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to retrieve Ignite pods IP addresses.", e);
        }

        return addresses;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerAddresses(Collection<InetSocketAddress> addrs) throws IgniteSpiException {
        // No-op
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unregisterAddresses(Collection<InetSocketAddress> addrs) throws IgniteSpiException {
        // No-op
    }

    /**
     * Sets the name of Kubernetes service for Ignite pods' IP addresses lookup. The name of the service must be equal
     * to the name set in service's Kubernetes configuration. If this parameter is not changed then the name of the
     * service has to be set to 'ignite' in the corresponding Kubernetes configuration.
     *
     * @param service Kubernetes service name for IP addresses lookup. If it's not set then 'ignite' is used by default.
     */
    public void setServiceName(String service) {
        this.serviceName = service;
    }

    /**
     * Sets the namespace the Kubernetes service belongs to. By default, it's supposed that the service is running under
     * Kubernetes `default` namespace.
     *
     * @param namespace The Kubernetes service namespace for IP addresses lookup.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * Sets the host name of the Kubernetes API server. By default the following host name is used:
     * 'https://kubernetes.default.svc.cluster.local:443'.
     *
     * @param master The host name of the Kubernetes API server.
     */
    public void setMasterUrl(String master) {
        this.master = master;
    }

    /**
     * Specifies the path to the service token file. By default the following account token is used:
     * '/var/run/secrets/kubernetes.io/serviceaccount/token'.
     *
     * @param accountToken The path to the service token file.
     */
    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    /**
     * Kubernetes IP finder initialization.
     *
     * @throws IgniteSpiException In case of error.
     */
    private void init() throws IgniteSpiException {
        if (serviceName == null || serviceName.isEmpty() ||
                namespace == null || namespace.isEmpty() ||
                master == null || master.isEmpty() ||
                accountToken == null || accountToken.isEmpty()) {
            throw new IgniteSpiException(
                    "One or more configuration parameters are invalid [setServiceName=" +
                            serviceName + ", setNamespace=" + namespace + ", setMasterUrl=" +
                            master + ", setAccountToken=" + accountToken + "]");
        }

        try {
            // Preparing the URL and SSL context to be used for connection purposes.
            String path = String.format("/api/v1/namespaces/%s/endpoints/%s", namespace, serviceName);
            url = new URL(master + path);
            ctx = SSLContext.getInstance("SSL");
            ctx.init(null, trustAll, new SecureRandom());
        } catch (Exception e) {
            throw new IgniteSpiException("Failed to connect to Ignite's Kubernetes Service.", e);
        }
    }

    /**
     * Reads content of the service account token file.
     *
     * @param file The path to the service account token.
     * @return Service account token.
     */
    private String serviceAccountToken(String file) {
        try {
            return new String(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            throw new IgniteSpiException("Failed to load services account token [setAccountToken= " + file + "]", e);
        }
    }

    /**
     * Object used by Jackson for processing of Kubernetes lookup service's response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Address {
        public String ip;
    }

    /**
     * Object used by Jackson for processing of Kubernetes lookup service's response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Subset {
        public List<Address> addresses;
    }

    /**
     * Object used by Jackson for processing of Kubernetes lookup service's response.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Endpoints {
        public List<Subset> subsets;
    }
}
