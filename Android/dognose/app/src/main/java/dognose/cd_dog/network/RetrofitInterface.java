package dognose.cd_dog.network;

import dognose.cd_dog.model.Dog;
import dognose.cd_dog.model.Res;
import dognose.cd_dog.model.User;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import rx.Observable;

public interface RetrofitInterface {

    @POST("users")
    Observable<Res> register(@Body User user);

    @POST("dogs")
    Observable<Res> registerDog(@Body Dog dog);

    @POST("authenticate")
    Observable<Res> login();

    @GET("users/{email}")
    Observable<User> getProfile(@Path("email") String email);

    @GET("dogs/{ownerId}")
    Observable<Dog[]> getDogProfile(@Path("ownerId") String ownerId);

    @PUT("users/{email}")
    Observable<Res> changePassword(@Path("email") String email, @Body User user);

    @POST("users/{email}/password")
    Observable<Res> resetPasswordInit(@Path("email") String email);

    @POST("users/{email}/password")
    Observable<Res> resetPasswordFinish(@Path("email") String email, @Body User user);

    @Multipart
    @POST("/images/upload")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part image, @Body Dog dog);
}
