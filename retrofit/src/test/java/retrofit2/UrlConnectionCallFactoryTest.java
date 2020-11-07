package retrofit2;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.Test;
import retrofit2.callFactory.UrlConnectionCallFactory;
import retrofit2.helpers.ToStringConverterFactory;
import retrofit2.http.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class UrlConnectionCallFactoryTest {
    @Rule
    public final MockWebServer server = new MockWebServer();

    interface Service {
        @GET("/")
        Call<String> getString();

        @GET("/")
        Call<ResponseBody> getBody();

        @GET("/")
        @Streaming
        Call<ResponseBody> getStreamingBody();

        @POST("/")
        Call<String> postString(@Body String body);

        @POST("/{a}")
        Call<String> postRequestBody(@Path("a") Object a);
    }

    @Test
    public void http200Sync() throws IOException {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(server.url("/"))
                        .addConverterFactory(new ToStringConverterFactory())
                        .callFactory(UrlConnectionCallFactory.create())
                        .build();
        Service example = retrofit.create(Service.class);

        server.enqueue(new MockResponse().setBody("Hi"));

        Response<String> response = example.getString().execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body()).isEqualTo("Hi");
    }

    @Test
    public void testPostString() throws IOException {
        Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(server.url("/"))
                        .callFactory(UrlConnectionCallFactory.create())
                        .addConverterFactory(new ToStringConverterFactory())
                        .build();
        Service example = retrofit.create(Service.class);
        server.enqueue(new MockResponse().setBody("ok post"));
        try {


            Call<String> call = example.postString("Hi testPostString");
            Response<String> respo = call.execute();

            System.out.println("[server request body]:" + server.takeRequest().getBody().readByteString().toString());
            System.out.println("[client response body]:" + respo.body());
        } catch (UnsupportedOperationException ignored) {
            System.out.println("testPostString error");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
