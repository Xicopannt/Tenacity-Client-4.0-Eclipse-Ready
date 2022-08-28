package dev.utils.thealtening;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.NonNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SSLController {
    static final Logger log = LogManager.getLogger();
    @NonNull
    private final SSLSocketFactory allTrustingFactory;
    @NonNull
    private final SSLSocketFactory originalFactory;
    @NonNull
    private final HostnameVerifier originalHostVerifier;
    private static final TrustManager[] ALL_TRUSTING_TRUST_MANAGER = new TrustManager[]{new X509TrustManager(){

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkClientTrusted(X509Certificate[] certs, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] certs, String authType) {
        }
    }};
    private static final HostnameVerifier ALTENING_HOSTING_VERIFIER = (hostname, session) -> hostname.equals("authserver.thealtening.com") || hostname.equals("sessionserver.thealtening.com");

    public SSLController() throws KeyManagementException, NoSuchAlgorithmException {
        SSLContext context = SSLContext.getInstance("SSL");
        context.init(null, ALL_TRUSTING_TRUST_MANAGER, new SecureRandom());
        this.allTrustingFactory = context.getSocketFactory();
        this.originalFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        this.originalHostVerifier = HttpsURLConnection.getDefaultHostnameVerifier();
    }

    public void enableCertificateValidation() {
        this.updateCertificateValidation(this.originalFactory, this.originalHostVerifier);
    }

    public void disableCertificateValidation() {
        this.updateCertificateValidation(this.allTrustingFactory, ALTENING_HOSTING_VERIFIER);
    }

    private void updateCertificateValidation(@NonNull SSLSocketFactory factory, @NonNull HostnameVerifier hostnameVerifier) {
        if (factory == null) {
            throw new NullPointerException("factory is marked non-null but is null");
        }
        if (hostnameVerifier == null) {
            throw new NullPointerException("hostnameVerifier is marked non-null but is null");
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(factory);
        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
    }
}