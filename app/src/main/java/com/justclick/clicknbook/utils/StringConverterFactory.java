package com.justclick.clicknbook.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

public class StringConverterFactory extends Converter.Factory {
//  private static final MediaType MEDIA_TYPE = MediaType.parse("application/json");
//private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");

  public static StringConverterFactory create() {
    return new StringConverterFactory();
  }

  /*@Override
  public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
    if (String.class.equals(type)) {
      return new Converter<ResponseBody, String>() {

        @Override
        public String convert(ResponseBody value) throws IOException {
          return value.string();
        }
      };
    }
    return null;
  }

  @Override
  public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
    if (String.class.equals(type)) {
      return new Converter<String, RequestBody>() {
        @Override
        public RequestBody convert(String value) throws IOException {
          return RequestBody.create(MEDIA_TYPE, value);
        }
      };
    }

    return null;

  }*/
}