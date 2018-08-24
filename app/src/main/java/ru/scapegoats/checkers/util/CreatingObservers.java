package ru.scapegoats.checkers.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.activity.game.Game;
import ru.scapegoats.checkers.activity.game.ThreadTurnEndWaiting;
import ru.scapegoats.checkers.activity.internet.InternetMenu;
import ru.scapegoats.checkers.activity.internet.ListAdapter;
import ru.scapegoats.checkers.activity.main.Main;
import ru.scapegoats.checkers.activity.session.SessionActivity;

import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.util.responsetypes.SessionListResponse;
import ru.scapegoats.checkers.util.responsetypes.UserInfoResponse;


/**
 * Created by scapegoat on 04/04/2018.
 */

public class CreatingObservers {
    static public Observer<Integer> getToken(Context context){

        return new Observer<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer token) {
                context.getSharedPreferences("User",0).edit().putInt("Token",token).apply();
                Log.e("gg",token+"");
                context.startActivity(new Intent(context,Main.class));
            }

            @Override
            public void onError(Throwable e) {
                Log.e("ss","er");

                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                Log.e("ss","compl");
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<Integer> createSession(Context context){

        return new Observer<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer token) {
                Intent intent=new Intent(context,SessionActivity.class);
                intent.putExtra(Keywords.session,token+"");
                context.startActivity(intent);
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<List<SessionListResponse>> getSessionList(InternetMenu activity){

        return new Observer<List<SessionListResponse>>(){
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(List<SessionListResponse> list) {
                Log.e("count of sessions",list.size()+"");

                if(list.get(0)!=null) {
                    activity.recyclerView.setAdapter(new ListAdapter(list, activity));
                } else activity.recyclerView.setAdapter(null);
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<UserInfoResponse> displayUserInfo(SessionActivity activity, String role){

        return new Observer<UserInfoResponse>(){
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UserInfoResponse info) {
                if(role.equals(Keywords.Host)){
                    activity.rate1.setText(info.getRating());
                    activity.nick1.setText(info.getNickname());
                }
                if(role.equals(Keywords.Guest)){
                    activity.rate2.setText(info.getRating());
                    activity.nick2.setText(info.getNickname());
                    activity.cardView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<Integer> updatedResult(SessionActivity activity){

        return new Observer<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Integer result) {
                Log.e("gg",result+"");
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<UserInfoResponse> checkGuest(SessionActivity activity){

        return new Observer<UserInfoResponse>(){
            @Override
            public void onSubscribe(Disposable d) {

            }

            @TargetApi(16)
            @Override
            public void onNext(UserInfoResponse info) {
                Log.e("result guest",info.getRating()+"/"+info.getNickname());
                if(!info.getRating().equals("-1")){
                    //если гость зашел
                    Log.e("guest is --",info.getNickname());
                    activity.rate2.setText(info.getRating());
                    activity.nick2.setText(info.getNickname());
                    activity.cardView.setVisibility(View.VISIBLE);
                    //activity.thread.stopRun();
                    if(info.getGuestReady().equals("1")){
                        //если гость нажал на кнопку готов
                        activity.start.setEnabled(true);
                        activity.butEnabled(activity.ready,activity.getResources().getDrawable(R.color.green),false);
                        activity.butEnabled(activity.start,activity.getResources().getDrawable(R.color.black),true);
                    }  else {
                        //if guest clicked ready again
                        activity.butEnabled(activity.ready,activity.getResources().getDrawable(R.color.grey),false);
                        activity.butEnabled(activity.start,activity.getResources().getDrawable(R.color.grey),false);
                        activity.start.setEnabled(false);
                    }
                } else {
                    //если гостя нет или он вышел
                    activity.cardView.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<Integer> startGame(SessionActivity activity,String role){

        return new Observer<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer result) {
                Log.e("start game",result+"");
                if(result==-1){
                    activity.startActivity(new Intent(activity,InternetMenu.class));
                    activity.threadGameStarted.stopRun();
                } else if(result!=-2){
                    Intent intent=new Intent(activity,Game.class);
                    intent.putExtra(Keywords.gameMode,Mode.INTERNET);
                    intent.putExtra(Keywords.gameid,result+"");
                    if(role.equals(Keywords.Guest)){
                        activity.threadGameStarted.stopRun();
                        intent.putExtra(Keywords.role,Keywords.Guest);
                    } else{
                        intent.putExtra(Keywords.role,Keywords.Host);
                    }
                    activity.startActivity(intent);
                }
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<Integer> makeTurn(Game game, String gameid){

        return new Observer<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer result) {
                Log.e("make turn",result+"");
                if(result!=-1){
                    game.threadWaiting=new ThreadTurnEndWaiting(game,gameid);
                    game.threadWaiting.start();
                }
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<String> waitTurn(Game game){

        return new Observer<String>(){
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String result) {
                Log.e("make turn",result+"");
                game.recieveMessage(result);
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }

    static public Observer<Integer> connectToSession(Context context,Intent intent){

        return new Observer<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer result) {
                Log.e("connect to ses",result+"");
                if(result!=-1){
                    context.startActivity(intent);
                } else{
                    Toast.makeText(context,context.getString(R.string.badRate),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }
    static public Observer<Integer> guestReady(SessionActivity activity){

        return new Observer<Integer>(){
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer result) {
                Log.e("connect to ses",result+"");
                if(result!=-1){
                    if(activity.guestReady) {
                        activity.butEnabled(activity.ready,activity.getResources().getDrawable(R.color.black),true);
                        activity.guestReady=false;
                    }
                    else{
                        activity.butEnabled(activity.ready,activity.getResources().getDrawable(R.color.green),true);
                        activity.guestReady=true;
                    }
                } else{
//                    Toast.makeText(context,context.getString(R.string.badRate),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                BaseActivity.hideProgress();
            }

            @Override
            public void onComplete() {
                BaseActivity.hideProgress();
            }

        };
    }


}
