package net.tetrakoopa.mdu4j.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLUtil {

	private static TrustManager blindValidationTrustManager = null;

	public static TrustManager createBlindValidatorTrustManager() {
		return new X509TrustManager() {
			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			@Override
			public void checkClientTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
			}

			@Override
			public void checkServerTrusted(final java.security.cert.X509Certificate[] certs, final String authType) {
			}
		};
	}

	public static void disableHostnameVerification() {
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
			@Override
			public boolean verify(final String hostname, final SSLSession session) {
				return true;
			}
		});
	}

	public static void disableSSLCheck() throws NoSuchAlgorithmException, KeyManagementException {
		if (blindValidationTrustManager == null) {
			blindValidationTrustManager = createBlindValidatorTrustManager();
		}
		final SSLContext sslContext = SSLContext.getInstance("SSL");
		sslContext.init(null, new TrustManager[] { blindValidationTrustManager }, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
	}

}
