package ru.scapegoats.checkers.util;


import android.util.Log;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ru.scapegoats.checkers.util.responsetypes.RegistrationResponse;
import ru.scapegoats.checkers.util.responsetypes.SessionListResponse;
import ru.scapegoats.checkers.util.responsetypes.TurnResponse;
import ru.scapegoats.checkers.util.responsetypes.UserInfoResponse;

public class RetrofitFunctions {

    public static Observable<Integer> getToken(Call<RegistrationResponse> call) {
        Log.e("gg",call.request().toString());
        return Observable.create(e -> {
            call.enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call1, Response<RegistrationResponse> response) {
                    e.onNext(response.body().getResult());
                    e.onComplete();
                }

                @Override
                public void onFailure(Call<RegistrationResponse> call1, Throwable t) {
                    Log.e("dd","why");
                    t.printStackTrace();
                    e.onError(t);
                }
            });
        });

    }

    public static Observable<Integer> setSession(Call<RegistrationResponse> call) {
        Log.e("gg",call.request().toString());
        return Observable.create(e -> {
            call.enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call1, Response<RegistrationResponse> response) {
                    e.onNext(response.body().getResult());
                    e.onComplete();
                }

                @Override
                public void onFailure(Call<RegistrationResponse> call1, Throwable t) {
                    e.onError(t);
                }
            });
        });

    }

    public static Observable<List<SessionListResponse>> getSessions(Call<List<SessionListResponse>> call) {
        Log.e("gg",call.request().toString());
        return Observable.create(e -> {
            call.enqueue(new Callback<List<SessionListResponse>>() {
                @Override
                public void onResponse(Call<List<SessionListResponse>> call1, Response<List<SessionListResponse>> response) {
                    e.onNext(response.body());
                    e.onComplete();
                }

                @Override
                public void onFailure(Call<List<SessionListResponse>> call1, Throwable t) {
                    e.onError(t);
                }
            });
        });
    }

    public static Observable<UserInfoResponse> getPlayerInfo(Call<UserInfoResponse> call) {
        Log.e("gg",call.request().toString());
        return Observable.create(e -> {
            call.enqueue(new Callback<UserInfoResponse>() {
                @Override
                public void onResponse(Call<UserInfoResponse> call1, Response<UserInfoResponse> response) {
                    e.onNext(response.body());
                    e.onComplete();
                }

                @Override
                public void onFailure(Call<UserInfoResponse> call1, Throwable t) {
                    e.onError(t);
                }
            });
        });

    }

    public static Observable<Integer> updateSession(Call<RegistrationResponse> call) {
        Log.e("gg",call.request().toString());
        return Observable.create(e -> {
            call.enqueue(new Callback<RegistrationResponse>() {
                @Override
                public void onResponse(Call<RegistrationResponse> call1, Response<RegistrationResponse> response) {
                    e.onNext(response.body().getResult());
                    Log.e("RESULT",response.body().getResult()+"");
                    e.onComplete();
                }

                @Override
                public void onFailure(Call<RegistrationResponse> call1, Throwable t) {
                    e.onError(t);
                }
            });
        });

    }
    public static Observable<String> waitTurn(Call<TurnResponse> call) {
        Log.e("gg",call.request().toString());
        return Observable.create(e -> {
            call.enqueue(new Callback<TurnResponse>() {
                @Override
                public void onResponse(Call<TurnResponse> call1, Response<TurnResponse> response) {
                    e.onNext(response.body().getResult());
                    e.onComplete();
                }

                @Override
                public void onFailure(Call<TurnResponse> call1, Throwable t) {
                    e.onError(t);
                }
            });
        });

    }

}