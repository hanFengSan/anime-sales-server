package tk.mybatis.springboot;

import okhttp3.*;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.*;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.util.List;

/**
 * Created by Yakami
 * on 2016/8/3.
 */
public class ServerAPI {

    public final static String mOriconHost = "https://ranking.oricon.co.jp";

    public final static int loginX = 34;
    public final static int loginY = 24;

    private static String mCookies = "";

    private static OriconAPI mOriconAPI;

    public static OriconAPI getOriconAPI() {
        if (mOriconAPI == null) {
            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor((Interceptor.Chain chain) -> {
                                Request original = chain.request();
                                switch (original.url().url().getPath()) {
                                    case "/login/check.asp":
                                        Response response = chain.proceed(original);
                                        setCookies(response.headers("Set-Cookie"));
                                        return response;
                                    default:
                                        Application.logger.info("set cookies on http request");
                                        return chain.proceed(original.newBuilder()
                                                .header("Cookie", mCookies)
                                                .method(original.method(), original.body())
                                                .build());
                                }
                            }
                    ).followRedirects(false)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ServerAPI.mOriconHost)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .build();

            mOriconAPI = retrofit.create(OriconAPI.class);
        }
        return mOriconAPI;
    }

    public static void setCookies(List<String> cookies) {
        mCookies = "";
        for (String item : cookies) {
            mCookies = mCookies + item + ";";
        }
        Application.logger.info(mCookies);
    }

    public static Observable<Boolean> isLoggedIn() {
        return Observable.create(subscriber -> {
            if (!ServerAPI.mCookies.contains("UserID")) {
                subscriber.onNext(false);
                subscriber.onCompleted();
                return;
            }
            ServerAPI.getOriconAPI()
                    .index()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe(body -> {
                        subscriber.onNext(true);
                        subscriber.onCompleted();
                    }, throwable -> {
                        subscriber.onNext(false);
                        subscriber.onCompleted();
                    });
        });
    }

    /**
     * 屏蔽底层登录细节
     *
     * @return
     */
    public static Observable<Boolean> withPermission() {
        return Observable.create(subscriber -> {
            //是否已登录
            isLoggedIn().subscribe(isLoggedIn -> {
                if (isLoggedIn) {
                    subscriber.onNext(true);
                    subscriber.onCompleted();
                } else {
                    // 重登录
                    ServerAPI.getOriconAPI()
                            .login(OriconID.oriconID, OriconID.oriconPW)
                            .subscribe(Void -> {
                                subscriber.onError(new Throwable("登录失败"));
                                subscriber.onCompleted();
                            }, throwable -> {
                                isLoggedIn().subscribe(tmp -> {
                                    if (tmp) {
                                        Application.logger.info("登录成功");
                                        subscriber.onNext(true);
                                        subscriber.onCompleted();
                                    } else {
                                        subscriber.onError(new Throwable("登录失败"));
                                        subscriber.onCompleted();
                                    }
                                });
                            });
                }
            }, throwable -> {
                Application.logger.error("登录失败, ERR001");
                subscriber.onError(throwable);
                subscriber.onCompleted();
            });
        });
    }

    /**
     * 输出retrofit中request的body内容
     *
     * @param request
     * @return
     */
    private static String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

    public static String getCookies() {
        return mCookies;
    }


    public interface OriconAPI {
        @FormUrlEncoded
        @POST("/login/check.asp")
        Observable<Void> login(@Field("USERID") String userId,
                               @Field("PASSWD") String userPw);

        @GET("/contents/index.asp")
        Observable<ResponseBody> index();

        @GET("/contents/ranking/daily/index.asp?chart_kbn=106103")
        Observable<ResponseBody> getDailyAnimeBDRank();

        @GET("/contents/ranking/weekly/index.asp?chart_kbn=116103")
        Observable<ResponseBody> getWeeklyAnimeBDRank();

        @GET
        Observable<ResponseBody> getCutomUrl(@Url String url);

    }

}
