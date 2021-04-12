package com.zebra.jamesswinton.wfctileinterfacepoc.networking;

import com.zebra.jamesswinton.wfctileinterfacepoc.data.AnnouncerResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnnouncerApi {

  // Send Message (Inc with Image / Video / Audio )
  @Multipart
  @POST("gw/announce/msg")
  Call<AnnouncerResponse> sendMessage(
          @Query("api_password") String api_key,
          @Part("message") String message,
          @Part("eid") int eid
  );

  // Send Message (Inc with Image / Video / Audio )
  @POST("gw/announce/msg")
  Call<AnnouncerResponse> sendMessageWithContent(
          @Query("api_password") String api_key,
          @Body RequestBody body
  );


}
