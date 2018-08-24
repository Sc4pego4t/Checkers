package ru.scapegoats.checkers.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import okhttp3.CertificatePinner;
import okhttp3.CipherSuite;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.util.requestapi.MyBackendRequests;
import ru.scapegoats.checkers.util.responsetypes.SessionListResponse;
import ru.scapegoats.checkers.util.responsetypes.UserInfoResponse;


public class ApiFactory {

    private static OkHttpClient CLIENT;
    private static Gson gson;
    public final static String mysite="http://lucullean-movements.000webhostapp.com/multiplayer/";

    static {

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        CLIENT = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .build();

         gson = new GsonBuilder()
                .setLenient()
                .create();

    }


    private static Retrofit getRetrofit(boolean showProgress) {
        if(showProgress) {
            BaseActivity.showProgress();
        }

        return new Retrofit.Builder()
                .baseUrl(mysite)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(CLIENT).build();
    }


    static public Observable<Integer> register(String password, String nick){
        return RetrofitFunctions.getToken(getRetrofit(true).
                create(MyBackendRequests.class).registerUser(password,nick));
    }

    static public Observable<Integer> autorize(String password, String nick){
        return RetrofitFunctions.getToken(getRetrofit(true).
                create(MyBackendRequests.class).autorizeUser(password,nick));
    }

    static public Observable<Integer> createSession(String title, String creator, String minRate, String maxRate){
        return RetrofitFunctions.setSession(getRetrofit(true).
                create(MyBackendRequests.class).createSession(title,creator,minRate,maxRate));
    }

    static public Observable<List<SessionListResponse>> getSessions(){
        return RetrofitFunctions.getSessions(getRetrofit(true).
                create(MyBackendRequests.class).sessionList());
    }

    static public Observable<UserInfoResponse> getPlayerInfo(String token,Boolean showProgress){
        return RetrofitFunctions.getPlayerInfo(getRetrofit(showProgress).
                create(MyBackendRequests.class).playerInfo(token));
    }

    static public Observable<Integer> updateSession(String sesid, String guest){
        return RetrofitFunctions.updateSession(getRetrofit(true).
                create(MyBackendRequests.class).updateSession(sesid,guest));
    }

    static public Observable<UserInfoResponse> checkGuest(String sesid){
        return RetrofitFunctions.getPlayerInfo(getRetrofit(false).
                create(MyBackendRequests.class).checkGuest(sesid));
    }

    static public Observable<Integer> gameStart(String sesid){
        return RetrofitFunctions.updateSession(getRetrofit(true).
                create(MyBackendRequests.class).startGame(sesid));
    }

    static public Observable<Integer> checkGameStarted(String sesid){
        return RetrofitFunctions.updateSession(getRetrofit(false).
                create(MyBackendRequests.class).checkForGame(sesid));
    }

    static public Observable<Integer> makeTurn(String board, String gameid){
        Log.e("make TURN",board+"/"+gameid+"/");
        return RetrofitFunctions.updateSession(getRetrofit(false).
                create(MyBackendRequests.class).makeTurn(board,gameid));
    }

    static public Observable<String> waitTurn(String gameid){
        Log.e("wait TURN",gameid+"/");
        return RetrofitFunctions.waitTurn(getRetrofit(false).
                create(MyBackendRequests.class).waitTurn(gameid));
    }
    static public void gameOver(String gameid, String whowin){
        Log.e("game over",gameid+"/");
        RetrofitFunctions.updateSession(getRetrofit(false).
                create(MyBackendRequests.class).gameOver(gameid,whowin)).subscribe();
    }

    static public void deleteSesssion(String sesid){
        Log.e("delete ses",sesid);
        RetrofitFunctions.updateSession(getRetrofit(false).
                create(MyBackendRequests.class).deleteSession(sesid)).subscribe();
    }

    static public Observable<Integer> connectToSession(String sesid, String guestid){
        Log.e("connect to ses",sesid);
        return RetrofitFunctions.updateSession(getRetrofit(true).
                create(MyBackendRequests.class).connectToSession(sesid,guestid));
    }

    static public Observable<Integer> guestReady(String sesid, String ready, boolean showProgress){
        Log.e("guest ready",sesid);
        return RetrofitFunctions.updateSession(getRetrofit(showProgress).
                create(MyBackendRequests.class).guestReady(sesid,ready));
    }

    static public void guestLeave(String sesid){
        Log.e("guest leaves",sesid);
        RetrofitFunctions.updateSession(getRetrofit(false).
                create(MyBackendRequests.class).guestLeave(sesid)).subscribe();
    }



}
