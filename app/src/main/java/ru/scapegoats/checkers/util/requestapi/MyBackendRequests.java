package ru.scapegoats.checkers.util.requestapi;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import ru.scapegoats.checkers.util.responsetypes.RegistrationResponse;
import ru.scapegoats.checkers.util.responsetypes.SessionListResponse;
import ru.scapegoats.checkers.util.responsetypes.TurnResponse;
import ru.scapegoats.checkers.util.responsetypes.UserInfoResponse;

public interface MyBackendRequests {

    @FormUrlEncoded
    @POST("register.php")
    Call<RegistrationResponse> registerUser(@Field("pas") String password
                                            ,@Field("nick") String nick);

    @FormUrlEncoded
    @POST("auth.php")
    Call<RegistrationResponse> autorizeUser(@Field("pas") String password
                                            ,@Field("nick") String nick);

    @FormUrlEncoded
    @POST("create_session.php")
    Call<RegistrationResponse> createSession(@Field("title") String title
                                            ,@Field("creator") String creator
                                             ,@Field("minRate") String minRate
                                             ,@Field("maxRate") String maxRate);

    @POST("return_sessions.php")
    Call<List<SessionListResponse>> sessionList();

    @FormUrlEncoded
    @POST("player_info.php")
    Call<UserInfoResponse> playerInfo(@Field("token") String token);

    @FormUrlEncoded
    @POST("update_session.php")
    Call<RegistrationResponse> updateSession(@Field("sesid") String sesid
            ,@Field("guest") String guest);

    @FormUrlEncoded
    @POST("check_guest.php")
    Call<UserInfoResponse> checkGuest(@Field("sesid") String sesid);

    @FormUrlEncoded
    @POST("start_game.php")
    Call<RegistrationResponse> startGame(@Field("sesid") String sesid);

    @FormUrlEncoded
    @POST("check_for_game_starting.php")
    Call<RegistrationResponse> checkForGame(@Field("sesid") String sesid);

    @FormUrlEncoded
    @POST("make_turn.php")
    Call<RegistrationResponse> makeTurn(@Field("board") String board
            ,@Field("gameid") String gameid);

    @FormUrlEncoded
    @POST("wait_turn.php")
    Call<TurnResponse> waitTurn(@Field("gameid") String gameid);

    @FormUrlEncoded
    @POST("game_over.php")
    Call<RegistrationResponse>gameOver(@Field("gameid") String gameid
            ,@Field("gameid") String whowin);

    @FormUrlEncoded
    @POST("delete_session.php")
    Call<RegistrationResponse> deleteSession(@Field("sesid") String sesid);

    @FormUrlEncoded
    @POST("connect_to_session.php")
    Call<RegistrationResponse> connectToSession(@Field("sesid") String sesid
                                                ,@Field("guestid") String guestid);

    @FormUrlEncoded
    @POST("guest_ready.php")
    Call<RegistrationResponse> guestReady(@Field("sesid") String sesid
                                                ,@Field("ready") String ready);

    @FormUrlEncoded
    @POST("guest_leave.php")
    Call<RegistrationResponse> guestLeave(@Field("sesid") String sesid);


}
