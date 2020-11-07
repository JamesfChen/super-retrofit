Super Retrofit
========
This project fork [Retrofit](https://github.com/square/retrofit),but refactor project.

You can use some call factorys(UrlConnectionCallFactory 、OkHttpCallFactory )) to create call , and add you custom protocol to this project.
```java
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
``` 

```java
Retrofit retrofit =
                new Retrofit.Builder()
                        .baseUrl(server.url("/"))
                        .addConverterFactory(new ToStringConverterFactory())
                        .callFactory(OkHttpCallFactory.create())
                        .build();
        Service example = retrofit.create(Service.class);

        server.enqueue(new MockResponse().setBody("Hi"));

        Response<String> response = example.getString().execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body()).isEqualTo("Hi");
```
