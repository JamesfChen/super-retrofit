package retrofit2.callFactory;

import okhttp3.Headers;
import okhttp3.internal.http2.Header;
import okio.Buffer;
import okio.BufferedSink;
import okio.Okio;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Request;
import okhttp3.ResponseBody;
import okio.Timeout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;

import javax.net.ssl.HttpsURLConnection;

/**
 * Copyright ® $ 2020
 * All right reserved.
 */
public final class UrlConnectionCallFactory implements Call.Factory {
    /*
          connection.setRequestMethod(method);
            connection.addRequestProperty("Authorization", auth);
            connection.addRequestProperty("Content-Type", contentType);
            if ("GET".equals(method)) {
                connection.setDoInput(true);
                connection.setInstanceFollowRedirects(false);
            } else if ("POST".equals(method)) {
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setDefaultUseCaches();
            }
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            connection.setHostnameVerifier();
            connection.setSSLSocketFactory();

            connection.setChunkedStreamingMode();
            connection.setFixedLengthStreamingMode();

            connection.setIfModifiedSince();
            connection.setAllowUserInteraction();
            connection.setInstanceFollowRedirects();
            connection.setRequestMethod();
            connection.setRequestProperty();
            connection.addRequestProperty();
     */
    public static UrlConnectionCallFactory create() {
        return new UrlConnectionCallFactory();
    }

    @Override
    public <T> Call<T> newCall(@NotNull Request request, @NotNull Converter<ResponseBody, T> responseConverter) {
        return new UrlConnectionCall(request, responseConverter);
    }

    private static final class UrlConnectionCall<T> implements Call<T> {
        private Request request = null;
        private URLConnection urlConnection;
//        private Converter<ResponseBody, T> responseConverter = null;

        UrlConnectionCall(
                Request request,
                Converter<ResponseBody, T> responseConverter) {
            this.request = request;
//            this.responseConverter = responseConverter;
        }

        @Override
        public Response<T> execute() throws IOException {
            HttpURLConnection connection;
            if (request.url().scheme().equals("https")) {
                connection = (HttpsURLConnection) request.url().url().openConnection();
            } else {
                connection = (HttpURLConnection) request.url().url().openConnection();
            }
            try {
                String method = request.method();
                connection.setRequestMethod(method);
                for (int i = 0, size = request.headers().size(); i < size; ++i) {
                    String name = request.headers().name(i);
                    String value = request.headers().value(i);
                    connection.addRequestProperty(name, value);
                }
                if ("GET".equals(method)) {
                    connection.setDoInput(true);
                    connection.setInstanceFollowRedirects(false);
                } else if ("POST".equals(method)) {
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);
                    OutputStream outputStream = connection.getOutputStream();
                    Buffer buffer = new Buffer();
                    assert request.body() != null;
                    request.body().writeTo(buffer);
                    outputStream.write(buffer.readByteArray());
                    outputStream.flush();
                    outputStream.close();
                }
//                connection.setConnectTimeout(timeout);
//                connection.setReadTimeout(timeout);
//                connection.setHostnameVerifier();
//                connection.setSSLSocketFactory();
//                connection.setAllowUserInteraction();
//                connection.setChunkedStreamingMode();
//                connection.setDefaultUseCaches();
//                connection.setFixedLengthStreamingMode();
//                connection.setIfModifiedSince();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                String reportResponse = readInputStream(inputStream);
                int responseCode = connection.getResponseCode();
                if (responseCode ==200){
                    Response.success(reportResponse);
                }
                connection.disconnect();
                System.out.println("cjf:resp:" + reportResponse);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        public static byte[] readInputStream(InputStream inputStream) {
            int count = 0;
            byte[] buff = new byte[4096];
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                while ((count = inputStream.read(buff, 0, buff.length)) != -1) {
                    baos.write(buff, 0, count);
                }
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return new String(baos.toByteArray());
        }

        @Override
        public void enqueue(Callback<T> callback) {

        }

        @Override
        public boolean isExecuted() {
            return false;
        }

        @Override
        public void cancel() {
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public Call<T> clone() {
            return null;
        }

        @Override
        public Request request() {
            return request;
        }

        @Override
        public Timeout timeout() {
            return null;
        }


    }
}
