/*
 * Copyright 2012 - 2016 Manuel Laggner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tinymediamanager.scraper.http;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;

/**
 * The class HttpClient. To construct our HTTP client for internet access
 * 
 * @author Manuel Laggner
 * @since 1.0
 */
public class TmmHttpClient {
  private static final Logger LOGGER    = LoggerFactory.getLogger(TmmHttpClient.class);
  public static final String  CACHE_DIR = "cache/http";
  private static Cache        CACHE     = new Cache(new File(CACHE_DIR), 5 * 1024 * 1024);
  private static OkHttpClient client    = createHttpClient();

  /**
   * instantiates a new OkHttpClient
   * 
   * @return OkHttpClient
   */
  private static OkHttpClient createHttpClient() {
    OkHttpClient.Builder builder = new OkHttpClient.Builder();

    // pool
    builder.connectionPool(new ConnectionPool(5, 5000, TimeUnit.MILLISECONDS));

    // timeouts
    builder.connectTimeout(10, TimeUnit.SECONDS);
    builder.writeTimeout(10, TimeUnit.SECONDS);
    builder.readTimeout(30, TimeUnit.SECONDS);

    // log http calls
    if (LOGGER.isTraceEnabled()) {
      HttpLoggingInterceptor logging = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
        @Override
        public void log(String message) {
          LOGGER.trace(message);
        }
      });
      logging.setLevel(Level.HEADERS);
      builder.addInterceptor(logging);
    }

    // proxy
    if ((ProxySettings.INSTANCE.useProxy())) {
      setProxy(builder);
    }

    // accept untrusted/self signed SSL certs
    if (Boolean.parseBoolean(System.getProperty("tmm.trustallcerts", "false"))) {
      try {
        final TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
          @Override
          public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
          }

          @Override
          public void checkServerTrusted(final X509Certificate[] chain, final String authType) {
          }

          @Override
          public void checkClientTrusted(final X509Certificate[] chain, final String authType) {
          }
        } };
        // Install the all-trusting trust manager
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // Create an ssl socket factory with our all-trusting manager
        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
      }
      catch (Exception ignored) {
      }
    }

    return builder.build();
  }

  /**
   * create a new OkHttpClient.Builder along with all our settings set
   *
   * @return the newly created builder
   */
  public static OkHttpClient.Builder newBuilder() {
    return newBuilder(false);
  }

  /**
   * create a new OkHttpClient.Builder along with all our settings set
   * 
   * @param withCache
   *          create the builder with a cache set
   * @return the newly created builder
   */
  public static OkHttpClient.Builder newBuilder(boolean withCache) {
    OkHttpClient.Builder builder = client.newBuilder();

    if (withCache) {
      builder.cache(CACHE);
    }

    return builder;
  }

  /**
   * Gets the pre-configured http client.
   * 
   * @return the http client
   */
  public static OkHttpClient getHttpClient() {
    return client;
  }

  /**
   * re-create the http client due to settings changes
   */
  public static void recreateHttpClient() {
    // recreate a new client instance
    client = createHttpClient();
  }

  private static void setProxy(OkHttpClient.Builder builder) {
    Proxy proxyHost;

    if (ProxySettings.INSTANCE.getPort() > 0) {
      proxyHost = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(ProxySettings.INSTANCE.getHost(), ProxySettings.INSTANCE.getPort()));
    }
    else if (StringUtils.isNotBlank(ProxySettings.INSTANCE.getHost())) {
      proxyHost = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(ProxySettings.INSTANCE.getHost(), 80));
    }
    else {
      // no proxy settings found. return
      return;
    }

    builder.proxy(proxyHost);
    // authenticate
    if (StringUtils.isNotBlank(ProxySettings.INSTANCE.getUsername()) && StringUtils.isNotBlank(ProxySettings.INSTANCE.getPassword())) {
      builder.authenticator(new Authenticator() {
        @Override
        public Request authenticate(Route route, Response response) throws IOException {
          String credential = Credentials.basic(ProxySettings.INSTANCE.getUsername(), ProxySettings.INSTANCE.getPassword());
          return response.request().newBuilder().header("Proxy-Authorization", credential).build();
        }
      });
    }
  }
}
