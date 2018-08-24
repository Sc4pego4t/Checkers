package ru.scapegoats.checkers.activity.game;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import ru.scapegoats.checkers.R;
import ru.scapegoats.checkers.activity.bluetooth.BluetoothAll;
import ru.scapegoats.checkers.activity.main.Main;
import ru.scapegoats.checkers.moduls.BaseActivity;
import ru.scapegoats.checkers.moduls.MySocket;
import ru.scapegoats.checkers.util.ApiFactory;
import ru.scapegoats.checkers.util.CreatingObservers;
import ru.scapegoats.checkers.util.Keywords;
import ru.scapegoats.checkers.util.Mode;
import ru.scapegoats.checkers.util.Pole;


public class Game extends BaseActivity implements View.OnClickListener {

    //region INIT
    private static final String TAG = "MY_APP_DEBUG_TAG";//сообщение которое будет высвечиваться в log-e
    public static Handler mHandler; // handler that gets info from Bluetooth service
    ///
    //Для игры по сети internet
    ///
    public String role = "";//HOST или GUEST для игры по сети
    public ThreadTurnEndWaiting threadWaiting;
    public String gameid;
    AlertDialog.Builder ad;//переменная для создание диалогового окна в конце игры
    Context context;
    LinearLayout ll;
    int countB = 0,countW = 0; //количество возможных ходов, для опеределения пата
    LinearLayout.LayoutParams param, param3;
    FrameLayout.LayoutParams param2;
    byte[] masByte = new byte[1024];//переменная для посылки сообщения
    TextView[] tv = new TextView[64];
    TextView[] tv2 = new TextView[64];
    ImageView[] iv = new ImageView[32];
    ImageView iv1, iv2;
    FrameLayout[] fl = new FrameLayout[32];
    boolean estObyazanBlack = false;//переменная для проверки должны ли есть на этом ходу черные true-должны false-не должны
    boolean estObyazanWhite = false;//переменная для проверки должны ли есть на этом ходу белые  true-должны false-не должны
    Pole[] pole = new Pole[32];
    boolean b = true;//1-очищает поля для битья, 0-поля уже были очищены
    boolean JUMP = false;
    boolean blackTurn = false;//определяет чей ход false-ход белых true-ход черных
    int jumpPole = -1;//переменная хранящая адрес съеденного обекта
    boolean click = false;//определяет какое текущее состояние нажатия false-первое true-второе
    int clickColor = 0;//переменная для хранения цвета поля
    int num = 0;
    boolean smena = false;//проверка была ли смена ходов
    boolean elLi = false;//проверка было ли битье на текущем ходу
    TextView t1, t2;
    int black = 0, white = 0;//кол-во белых и черных шашек
    int ai;//переменная определяющяя режим игры 0-легкий бот, 1-сложный бот, 2-игра с другом
    Pole[][] temp = new Pole[300][32];//хранение всех ходов
    int iHod = 0;//индекс хода
    int backHod = 0;//на какое количество ходов необходимо возвращаться
    ArrayList<Boolean> whoseHod = new ArrayList<>();//массив для выяснения чей же ход после нажатия на кнопку "назад"  при игре с другом
    ArrayList<Integer> AIhod = new ArrayList<>();//массив для хранения количество передвижения шашек за каждый ход  сделаных AI
    int iHodBack = 0;//индекс для определения позиции массива
    BluetoothAll ba;
    double padding;//переменная для подсчета отступов для картинок шашек
    int width;//хранит ширину экрана устройства

    //endregion
    //region SETS
    @TargetApi(16)
    public void setFalseB() {
        miEnabF();
    }

    @TargetApi(16)
    public void setTrueB() {
        miEnabT();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final Activity activity = this;
        switch (item.getItemId()) {
            case android.R.id.home: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this)
                        .setTitle(R.string.s16)
                        .setMessage(R.string.s17)
                        .setNegativeButton(R.string.s19, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton(R.string.s18, new OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NavUtils.navigateUpFromSameTask(activity);
                            }
                        });
                builder.show();
            }
            ;
            return true;
            case R.id.item1:
                back();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setSize(View view, int height) {
        param = (LinearLayout.LayoutParams) view.getLayoutParams();
        param.height = height;
    }

    public void setSize2(View view, int height) {
        param = (LinearLayout.LayoutParams) view.getLayoutParams();
        param.height = height;
        param.width = height;
    }

    public void setSize3(View view) {
        param = (LinearLayout.LayoutParams) view.getLayoutParams();
        param.setMargins((int) padding, (int) padding, (int) padding, (int) padding);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ai == Mode.BLUETOOTH) {
            ba.cancelConnected();
            MySocket.server = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar, menu);
        mi = menu.findItem(R.id.item1);
        Log.e("here", "now");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ai == Mode.BLUETOOTH || ai == Mode.INTERNET) {
            miInvis();
        }
        return true;
    }

    @TargetApi(16)
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_act);
        ba = new BluetoothAll(context);

        ll = (LinearLayout) findViewById(R.id.fon);
        loadFon(ll);

        ll = (LinearLayout) findViewById(R.id.gameL);
        Display display = getWindowManager().getDefaultDisplay();
        width = display.getWidth();
        padding = width / 8 * 0.1;
        setSize(ll, width);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);

        iv1 = (ImageView) findViewById(R.id.iv1);
        iv2 = (ImageView) findViewById(R.id.iv2);
        LinearLayout rot, ll1, ll2, ll3, ll4;
        ll3 = (LinearLayout) findViewById(R.id.ll3);
        ll4 = (LinearLayout) findViewById(R.id.ll4);
        rot = findViewById(R.id.rot);

        ll3.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        ll4.setPadding((int) padding, (int) padding, (int) padding, (int) padding);
        setSize3(ll3);
        setSize3(ll4);

        setSize2(iv1, width / 8 - (int) padding * 2);
        setSize2(iv2, width / 8 - (int) padding * 2);
        param = (LinearLayout.LayoutParams) ll.getLayoutParams();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {  //проверяем, были ли переданы данные
            ai = extras.getInt(getString(R.string.key1));
            //setFalseB();
        }


        if (ai == Mode.BLUETOOTH) {
            if (!MySocket.server) rot.setRotationX(180);
            ba.startConnected();
        }

        if (ai == Mode.INTERNET) {
            role = getIntent().getExtras().getString(Keywords.role);
            gameid = getIntent().getExtras().getString(Keywords.gameid);
            threadWaiting = new ThreadTurnEndWaiting(this, gameid);
            if (role.equals(Keywords.Guest)) {
                rot.setRotation(180);
                threadWaiting.start();
            }
        }
        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                String incomingMessage = new String((byte[]) msg.obj, 0, msg.what);
                recieveMessage(incomingMessage);
            }
        };

        ad = new AlertDialog.Builder(this);
        ad.setMessage(R.string.s14); // сообщение
        ad.setPositiveButton(R.string.s15, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent i = new Intent(Game.this, Game.class);
                startActivity(i);
            }
        });
        ad.setNeutralButton(R.string.s16, new OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Intent i = new Intent(Game.this, Main.class);
                startActivity(i);
            }
        });
        ad.setCancelable(true);
        ad.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                //Intent i=new Intent(Game.this, Main.class);
                //startActivity(i);
            }
        }).create();
        sozdPolya();
        zvetPolya();
    }

    //endregion
    private void sendMes(int turn) {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < 32; i++) {
            sb.append(pole[i].getZvet());
        }

        sb.append(turn);
        Log.e("messageee", sb.toString());

        if (ai == Mode.BLUETOOTH) {
            //если играем по bluetooth
            masByte = sb.toString().getBytes();
            //connected(MySocket.bs);
            write(masByte);
        } else {
            //если играем по INTERNET
            ApiFactory.makeTurn(sb.toString(), gameid).subscribe(
                    CreatingObservers.makeTurn(this, gameid)
            );
        }
    }


    //region ALLFUNC
    public void recieveMessage(String mes) {
        Log.e("gg", "recieved mes /" + mes);
        char[] chars = mes.toCharArray();
        String s = String.valueOf(chars[32]);
        int temp = Integer.parseInt(s);
        boolean clearHighligting = true;
        if (temp == 0 && !blackTurn || temp == 1 && blackTurn) {
            clearHighligting = false;
        }
        for (int i = 0; i < 32; i++) {
            String c = String.valueOf(chars[i]);
            pole[i].setZvet(Integer.parseInt(c));
            if (clearHighligting) {
                delPodsvetPolya();
                zvetPolya();
                podsvetPolya();
            }
        }
        count();


        switch (temp) {
            case 0:
                blackTurn = false;
                if (role.equals(Keywords.Host)) threadWaiting.stopRun();
                break;
            case 1:
                blackTurn = true;
                if (role.equals(Keywords.Guest)) threadWaiting.stopRun();
                break;
            case 2:
                ad.setTitle(getString(R.string.s7)).show();
                ApiFactory.gameOver(gameid, Keywords.Host);
                threadWaiting.stopRun();
                break;
            case 3:
                ad.setTitle(getString(R.string.s8)).show();
                ApiFactory.gameOver(gameid, Keywords.Guest);
                threadWaiting.stopRun();
                break;
        }
    }

    @TargetApi(16)
    private void sozdPolya() {
        int j = 0, g = 0, z = 0;
        for (int i = 0; i < 64; i++) {
            if (i % 8 == 0)
                j++;
            switch (j) {
                case 1:
                    ll = (LinearLayout) findViewById(R.id.l1);
                    break;
                case 2:
                    ll = (LinearLayout) findViewById(R.id.l2);
                    break;
                case 3:
                    ll = (LinearLayout) findViewById(R.id.l3);
                    break;
                case 4:
                    ll = (LinearLayout) findViewById(R.id.l4);
                    break;
                case 5:
                    ll = (LinearLayout) findViewById(R.id.l5);
                    break;
                case 6:
                    ll = (LinearLayout) findViewById(R.id.l6);
                    break;
                case 7:
                    ll = (LinearLayout) findViewById(R.id.l7);
                    break;
                case 8:
                    ll = (LinearLayout) findViewById(R.id.l8);
                    break;
            }

            if ((i + (j % 2)) % 2 == 0) {
                tv[g] = new TextView(this);
                tv[g].setOnClickListener(this);
                tv[g].setId(g);

                fl[g] = new FrameLayout(this);
                ll.addView(fl[g]);
                param3 = (LinearLayout.LayoutParams) fl[g].getLayoutParams();
                param3.width = 0;
                param3.weight = 1;
                param3.height = LinearLayout.LayoutParams.MATCH_PARENT;
                fl[g].addView(tv[g]);
                tv[g].setBackground(getResources().getDrawable(R.drawable.rf));
                iv[g] = new ImageView(this);

                fl[g].addView(iv[g]);
                // iv[g].setImageDrawable(getResources().getDrawable(R.drawable.t));
                param2 = (FrameLayout.LayoutParams) iv[g].getLayoutParams();
                param2.height = width / 8 - (int) padding * 2;
                param2.gravity = Gravity.CENTER;
                g++;

            } else {

                tv2[z] = new TextView(this);
                ll.addView(tv2[z]);
                param = (LinearLayout.LayoutParams) tv2[z].getLayoutParams();
                param.width = 0;
                param.weight = 1;
                param.height = 1000;
                //tv2[z].setBackgroundColor(ContextCompat.getColor(this, R.color.white));
                tv2[z].setBackground(getResources().getDrawable(R.drawable.wf));
                z++;
            }
        }
        //функция для прорисовки поля
        int m = 1;
        for (int i = 0; i < 32; i++) {
            pole[i] = new Pole();
            pole[i].setMesto(m);
            if (m > 0 && m < 13)
                pole[i].setZvet(1);//черное
            if (m > 12 && m < 21)
                pole[i].setZvet(0);//пустое
            if (m > 20 && m < 33)
                pole[i].setZvet(2);//белое
            pole[i].setFocus(false);
            pole[i].setPodsvet(false);


            m++;
        }
        pole[3].setStolbez(8);
        pole[11].setStolbez(8);
        pole[19].setStolbez(8);
        pole[27].setStolbez(8);
        pole[4].setStolbez(1);
        pole[12].setStolbez(1);
        pole[20].setStolbez(1);
        pole[28].setStolbez(1);
        for (int i = 0; i < 32; i++) {
            if (i <= 3)
                pole[i].setRyad(1);
            if (i >= 4 && i <= 7)
                pole[i].setRyad(2);
            if (i >= 8 && i <= 11)
                pole[i].setRyad(3);
            if (i >= 12 && i <= 15)
                pole[i].setRyad(4);
            if (i >= 16 && i <= 19)
                pole[i].setRyad(5);
            if (i >= 20 && i <= 23)
                pole[i].setRyad(6);
            if (i >= 24 && i <= 27)
                pole[i].setRyad(7);
            if (i >= 28)
                pole[i].setRyad(8);
        }
        pole[0].setWay0to27(true);
        pole[1].setWay1to19(true);
        pole[1].setWay1to12(true);
        pole[2].setWay2to20(true);
        pole[2].setWay2to11(true);
        pole[3].setWay3to28(true);
        pole[4].setWay4to31(true);
        pole[5].setWay1to12(true);
        pole[5].setWay0to27(true);
        pole[6].setWay1to19(true);
        pole[6].setWay2to20(true);
        pole[7].setWay2to11(true);
        pole[7].setWay3to28(true);
        pole[8].setWay1to12(true);
        pole[8].setWay4to31(true);
        pole[9].setWay2to20(true);
        pole[9].setWay0to27(true);
        pole[10].setWay3to28(true);
        pole[10].setWay1to19(true);
        pole[11].setWay2to11(true);
        pole[11].setWay11to29(true);
        pole[12].setWay1to12(true);
        pole[12].setWay12to30(true);
        pole[13].setWay2to20(true);
        pole[13].setWay4to31(true);
        pole[14].setWay3to28(true);
        pole[14].setWay0to27(true);
        pole[15].setWay11to29(true);
        pole[15].setWay1to19(true);
        pole[16].setWay12to30(true);
        pole[16].setWay2to20(true);
        pole[17].setWay3to28(true);
        pole[17].setWay4to31(true);
        pole[18].setWay0to27(true);
        pole[18].setWay11to29(true);
        pole[19].setWay19to30(true);
        pole[19].setWay1to19(true);
        pole[20].setWay20to29(true);
        pole[20].setWay2to20(true);
        pole[21].setWay3to28(true);
        pole[21].setWay12to30(true);
        pole[22].setWay11to29(true);
        pole[22].setWay4to31(true);
        pole[23].setWay19to30(true);
        pole[23].setWay0to27(true);
        pole[24].setWay3to28(true);
        pole[24].setWay20to29(true);
        pole[25].setWay11to29(true);
        pole[25].setWay12to30(true);
        pole[26].setWay4to31(true);
        pole[26].setWay19to30(true);
        pole[27].setWay0to27(true);
        pole[28].setWay3to28(true);
        pole[29].setWay20to29(true);
        pole[29].setWay11to29(true);
        pole[30].setWay19to30(true);
        pole[30].setWay12to30(true);
        pole[31].setWay4to31(true);
    }

    private void zvetPolya() {
        //функция расстановки шашек на поле
        for (int i = 0; i < 32; i++) {
            if (pole[i].getZvet() == 1) {
                iv[i].setImageDrawable(getResources().getDrawable(R.drawable.t));
                iv[i].setVisibility(View.VISIBLE);
            }
            if (pole[i].getZvet() == 2) {
                iv[i].setImageDrawable(getResources().getDrawable(R.drawable.r));
                iv[i].setVisibility(View.VISIBLE);
            }
            if (pole[i].getZvet() == 3) {
                iv[i].setImageDrawable(getResources().getDrawable(R.drawable.bq));
                iv[i].setVisibility(View.VISIBLE);
            }
            if (pole[i].getZvet() == 4) {
                iv[i].setImageDrawable(getResources().getDrawable(R.drawable.wq));
                iv[i].setVisibility(View.VISIBLE);
            }
            if (pole[i].getZvet() == 0) {
                iv[i].setVisibility(View.INVISIBLE);
            }
        }

    }

    private void delPodsvetPolya() {
        Log.e("delPodsv","&&&&&");
        //функция удаления подсвета поля
        for (int i = 0; i < 32; i++)
            pole[i].setPodsvet(false);

    }

    @TargetApi(16)
    private void podsvetPolya() {
        //функция подсвета полей
        for (int i = 0; i < 32; i++)
            if (pole[i].getPodsvet()) {
                if (blackTurn) {
                    countB++;
                    Log.e("++","======");
                }
                else
                    countW++;
                String podsv = loadPref("Podsv");
                switch (podsv) {
                    case "red":
                        tv[i].setBackgroundColor(ContextCompat.getColor(this, R.color.red));
                        break;
                    case "blue":
                        tv[i].setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                        break;
                    case "yellow":
                        tv[i].setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
                        break;
                    case "green":
                        tv[i].setBackgroundColor(ContextCompat.getColor(this, R.color.green));
                        break;
                    case "purple":
                        tv[i].setBackgroundColor(ContextCompat.getColor(this, R.color.purple));
                        break;
                    default:
                        tv[i].setBackgroundColor(ContextCompat.getColor(this, R.color.blue));
                        break;
                }
            } else {
                tv[i].setBackground(getResources().getDrawable(R.drawable.rf));
            }

    }

    private int opredDiag(int i, int c) {
        switch (c)//проверка на какой диагонали находится шашка
        {
            case 1:
                if (pole[i].getWay20to29()) return 1;
                break;
            case 2:
                if (pole[i].getWay12to30()) return 2;
                break;
            case 3:
                if (pole[i].getWay4to31()) return 3;
                break;
            case 4:
                if (pole[i].getWay0to27()) return 4;
                break;
            case 5:
                if (pole[i].getWay1to19()) return 5;
                break;
            case 6:
                if (pole[i].getWay2to11()) return 6;
                break;
            case 7:
                if (pole[i].getWay1to12()) return 7;
                break;
            case 8:
                if (pole[i].getWay2to20()) return 8;
                break;
            case 9:
                if (pole[i].getWay3to28()) return 9;
                break;
            case 10:
                if (pole[i].getWay11to29()) return 10;
                break;
            case 11:
                if (pole[i].getWay19to30()) return 11;
                break;
        }
        return 0;
    }

    private void hodDamka(int n, boolean u, int c) {
        try {

            //функция для хода дамки
            boolean q = true;
            int i, z = 0;

            if (pole[n].getRyad() != 8)//ходы по диагонали вниз
                for (i = (n + 1); i < 32; i++) {
                    z = opredDiag(i, c);
                    if (z == c)//если шашки на одной диагонали
                    {
                        if (pole[n].getZvet() == 4 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))
                            break;
                        if (pole[n].getZvet() == 3 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))
                            break;
                        int y = 2;//переменная для проверки прибавлять 4 или 5 клеток
                        if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 4 && blackTurn == false) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 3 && blackTurn == true)) {
                            y++;
                            if (pole[i].getRyad() != 8 && pole[i].getStolbez() != 8 && pole[i].getStolbez() != 1) {
                                if (pole[i].getRyad() % 2 == 0)//четный ряд
                                {
                                    if (u)//диагональ слева направо
                                    {

                                        if (pole[i + 4].getZvet() == 0)
                                            jumpPole = i;
                                    } else//диагональ справо налево
                                        if (pole[i + 3].getZvet() == 0)
                                            jumpPole = i;
                                } else//нечетный ряд
                                {
                                    if (u)//диагональ слева направо
                                    {
                                        if (pole[i + 5].getZvet() == 0)
                                            jumpPole = i;
                                    } else//диагональ справо налево
                                        if (pole[i + 4].getZvet() == 0)
                                            jumpPole = i;
                                }
                            }
                            do {

                                if (pole[i].getRyad() % 2 == 0 && pole[i].getRyad() != 8 && pole[i].getStolbez() != 8 && pole[i].getStolbez() != 1)
                                //четный ряд
                                {
                                    if (u)//диагональ слева направо
                                    {
                                        if (pole[i + 4].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            pole[i + 4].setPodsvet(true);
                                            i += 4;
                                        } else q = false;
                                    } else//диагональ справо налево
                                        if (pole[i + 3].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            pole[i + 3].setPodsvet(true);
                                            i += 3;
                                        } else q = false;
                                } else//нечетный ряд
                                    if (pole[i].getRyad() != 8 && pole[i].getStolbez() != 8 && pole[i].getStolbez() != 1) {
                                        if (u)//диагональ слева направо
                                        {
                                            if (pole[i + 5].getZvet() == 0) {
                                                if (b) {
                                                    delPodsvetPolya();
                                                    b = false;
                                                }
                                                pole[i + 5].setPodsvet(true);
                                                i += 5;
                                            } else q = false;
                                        } else//диагональ справо налево
                                        {
                                            if (pole[i + 4].getZvet() == 0) {
                                                if (b) {
                                                    delPodsvetPolya();
                                                    b = false;
                                                }
                                                pole[i + 4].setPodsvet(true);
                                                i += 4;
                                            } else q = false;
                                        }
                                    } else {
                                        break;
                                    }
                            }
                            while (q);
                            break;
                        }
                        if (b)
                            if (blackTurn) {
                                if (!estObyazanBlack) {
                                    if (pole[n].getZvet() == 3)
                                        if (pole[i].getZvet() == 0)
                                            pole[i].setPodsvet(true);
                                }
                            } else if (!estObyazanWhite) {
                                if (pole[n].getZvet() == 4)
                                    if (pole[i].getZvet() == 0)
                                        pole[i].setPodsvet(true);
                            }

                    }
                }
            if (pole[n].getRyad() != 1)
                for (i = (n - 1); i >= 0; i--)//ходы по диагонали вверх
                {

                    //  MessageBox.Show("i--"+Convert.ToString(i));
                    z = opredDiag(i, c);
                    if (z == c) {

                        if (pole[n].getZvet() == 4 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))
                            break;
                        if (pole[n].getZvet() == 3 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))
                            break;

                        if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 4 && !blackTurn) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 3 && blackTurn)) {
                            if (pole[i].getRyad() != 1 && pole[i].getStolbez() != 1 && pole[i].getStolbez() != 8) {
                                if (pole[i].getRyad() % 2 == 0)//четный ряд
                                {
                                    if (u)//диагональ слева направо
                                    {

                                        if (pole[i - 5].getZvet() == 0)
                                            jumpPole = i;
                                    } else//диагональ справо налево
                                        if (pole[i - 4].getZvet() == 0)
                                            jumpPole = i;
                                } else//нечетный ряд
                                    if (u)////диагональ слева направо
                                    {
                                        if (pole[i - 4].getZvet() == 0)
                                            jumpPole = i;
                                    } else//диагональ справо налево
                                        if (pole[i - 3].getZvet() == 0)
                                            jumpPole = i;
                            }
                            do {
                                // MessageBox.Show("i++" + Convert.ToString(i));
                                if (pole[i].getRyad() % 2 == 0 && pole[i].getRyad() != 1 && pole[i].getStolbez() != 1 && pole[i].getStolbez() != 8) {
                                    if (u)//диагональ слева направо
                                    {
                                        if (pole[i - 5].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            pole[i - 5].setPodsvet(true);
                                            i -= 5;
                                        } else q = false;
                                    } else//диагональ справо налево
                                    {
                                        if (pole[i - 4].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            pole[i - 4].setPodsvet(true);
                                            i -= 4;
                                        } else q = false;
                                    }
                                } else if (pole[i].getRyad() != 1 && pole[i].getStolbez() != 1 && pole[i].getStolbez() != 8) {
                                    if (u)//диагональ слева направо
                                    {
                                        if (pole[i - 4].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            pole[i - 4].setPodsvet(true);
                                            i -= 4;
                                        } else q = false;
                                    } else//диагональ справо налево
                                    {
                                        if (pole[i - 3].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            pole[i - 3].setPodsvet(true);
                                            i -= 3;
                                        } else q = false;
                                    }

                                } else
                                    break;

                            }
                            while (q);
                            break;
                        }
                        if (b)
                            if (blackTurn) {
                                if (!estObyazanBlack) {
                                    if (pole[n].getZvet() == 3)
                                        if (pole[i].getZvet() == 0)
                                            pole[i].setPodsvet(true);
                                }
                            } else if (!estObyazanWhite) {
                                if (pole[n].getZvet() == 4)
                                    if (pole[i].getZvet() == 0)
                                        pole[i].setPodsvet(true);
                            }

                    }
                }
            if (blackTurn) {
                if (pole[n].getZvet() == 3) {
                    click = true;
                    clickColor = pole[n].getZvet();
                    num = n;
                }
            } else if (pole[n].getZvet() == 4) {
                click = true;
                clickColor = pole[n].getZvet();
                num = n;
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
    }

    private void hodShashka(int n, boolean u, int c) {
        try {

            //функция для хода дамки
            int i, z = 0;
            int k = 0; //пременная чтобы шашка не могла ecть через все поле
            if (pole[n].getRyad() != 8)//ходы по диагонали вниз
                for (i = (n + 1); i < 32; i++) {
                    k++;
                    if (k > 5)
                        break;
                    z = opredDiag(i, c);
                    if (z == c)//если шашки на одной диагонали
                    {
                        if (pole[n].getZvet() == 2 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))
                            break;
                        if (pole[n].getZvet() == 1 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))
                            break;
                        if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 2 && !blackTurn) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 1 && blackTurn)) {
                            if (pole[i].getRyad() != 8 && pole[i].getStolbez() != 8 && pole[i].getStolbez() != 1) {
                                if (pole[i].getRyad() % 2 == 0)//если четный ряд
                                {
                                    if (u)//если диагональ слево вправо
                                    {

                                        if (pole[i + 4].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            jumpPole = i;
                                            pole[i + 4].setPodsvet(true);
                                        } else
                                            break;
                                    } else//если диагональ справо влево
                                        if (pole[i + 3].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            jumpPole = i;
                                            pole[i + 3].setPodsvet(true);
                                        } else
                                            break;
                                } else//если ряд нечетный
                                {
                                    if (u)//если диагональ слево вправо
                                    {
                                        if (pole[i + 5].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            jumpPole = i;
                                            pole[i + 5].setPodsvet(true);
                                        } else
                                            break;
                                    }//если диагональ справо влево
                                    else if (pole[i + 4].getZvet() == 0) {
                                        if (b) {
                                            delPodsvetPolya();
                                            b = false;
                                        }
                                        jumpPole = i;
                                        pole[i + 4].setPodsvet(true);
                                    } else
                                        break;
                                }
                            }
                        }
                        if (b) {
                            if (blackTurn) {
                                if (!estObyazanBlack) {
                                    if (pole[n].getZvet() == 1) {
                                        if (pole[i].getZvet() == 0)
                                            pole[i].setPodsvet(true);
                                        break;
                                    }
                                }
                            }

                        }
                    }
                }
            k = 0;
            if (pole[n].getRyad() != 1)
                for (i = (n - 1); i >= 0; i--)//ходы по диагонали вверх
                {
                    k++;
                    if (k > 5)
                        break;

                    z = opredDiag(i, c);
                    if (z == c) {

                        if (pole[n].getZvet() == 2 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))
                            break;
                        if (pole[n].getZvet() == 1 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))
                            break;

                        if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 2 && blackTurn == false) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 1 && blackTurn == true)) {
                            if (pole[i].getRyad() != 1 && pole[i].getStolbez() != 1 && pole[i].getStolbez() != 8) {
                                if (pole[i].getRyad() % 2 == 0)//четный ряд
                                {
                                    if (u)//диагональ слево направо
                                    {
                                        if (pole[i - 5].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            jumpPole = i;
                                            pole[i - 5].setPodsvet(true);
                                            break;
                                        } else
                                            break;
                                    } else//диагональ справа налево
                                        if (pole[i - 4].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            jumpPole = i;
                                            pole[i - 4].setPodsvet(true);
                                            break;
                                        } else
                                            break;
                                } else//нечетный ряд
                                {
                                    if (u)//диагональ слево направо
                                    {
                                        if (pole[i - 4].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            jumpPole = i;
                                            pole[i - 4].setPodsvet(true);
                                            break;
                                        } else
                                            break;
                                    } else//диагональ справа налево
                                        if (pole[i - 3].getZvet() == 0) {
                                            if (b) {
                                                delPodsvetPolya();
                                                b = false;
                                            }
                                            jumpPole = i;
                                            pole[i - 3].setPodsvet(true);
                                            break;
                                        } else
                                            break;
                                }
                            }

                        }
                        if (b)
                            if (blackTurn == false)
                                if (estObyazanWhite == false) {
                                    if (pole[n].getZvet() == 2) {
                                        if (pole[i].getZvet() == 0)
                                            pole[i].setPodsvet(true);
                                        break;
                                    }
                                }

                    }
                }
            if (blackTurn == true) {
                if (pole[n].getZvet() == 1) {
                    click = true;
                    clickColor = pole[n].getZvet();
                    num = n;
                }
            } else if (pole[n].getZvet() == 2) {
                click = true;
                clickColor = pole[n].getZvet();
                num = n;
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
    }

    private void hodDlyaVseh(int n, boolean prov) {
        Log.e("hod","+++"+n+"+++");
        //функция для первого клика
        if (ai == Mode.BLUETOOTH && !prov) {
            //если играем через bluetooth или internet то host
            // не может ходить за черных а клиент за белых

            if (blackTurn && MySocket.server)
                return;
            if (!blackTurn && !MySocket.server)
                return;
        }
        if (ai == Mode.INTERNET && !prov) {
            if (blackTurn && role.equals(Keywords.Host)) {
                Log.e("return", Keywords.Host + "/" + role);
                return;
            }
            if (!blackTurn && role.equals(Keywords.Guest)) {
                Log.e("return", Keywords.Guest + "/" + role);
                return;
            }
        }

        estObyazanBlack = false;
        estObyazanWhite = false;//обнуление переменных с каждым запуском функции
        boolean estObyazan;//переменная для проверки должна ли какая-нибудь из сторон есть
        for (int j = 0; j < 32; j++) {
            estObyazan = proverka(j);
            if (estObyazan) {
                if (blackTurn)
                    if (pole[j].getZvet() == 1 || pole[j].getZvet() == 3) {
                        estObyazanBlack = true;

                    }
                if (!blackTurn)
                    if (pole[j].getZvet() == 2 || pole[j].getZvet() == 4) {
                        estObyazanWhite = true;

                    }

            }
        }
        //delPodsvetPolya();
        //------------------------------------------------------//
        //    проверка для ходов на исключительных диагоналях   //
        //------------------------------------------------------//
        if (n == 4) {
            if (!estObyazanBlack)
                if (pole[n].getZvet() == 3)
                    if (pole[0].getZvet() == 0)
                        if (blackTurn)
                            pole[0].setPodsvet(true);
            if (!estObyazanWhite)
                if (pole[n].getZvet() == 4 || pole[n].getZvet() == 2)
                    if (pole[0].getZvet() == 0)
                        if (!blackTurn)
                            pole[0].setPodsvet(true);
        }
        if (n == 0) {
            if (!estObyazanBlack)
                if (pole[n].getZvet() == 3 || pole[n].getZvet() == 1)
                    if (pole[4].getZvet() == 0)
                        if (blackTurn)
                            pole[4].setPodsvet(true);
            if (!estObyazanWhite)
                if (pole[n].getZvet() == 4)
                    if (pole[4].getZvet() == 0)
                        if (!blackTurn)
                            pole[4].setPodsvet(true);
        }

        if (n == 31) {
            if (!estObyazanBlack)
                if (pole[n].getZvet() == 3)
                    if (pole[27].getZvet() == 0)
                        if (blackTurn)
                            pole[27].setPodsvet(true);
            if (!estObyazanWhite)
                if (pole[n].getZvet() == 4 || pole[n].getZvet() == 2)
                    if (pole[27].getZvet() == 0)
                        if (!blackTurn)
                            pole[27].setPodsvet(true);
        }
        if (n == 27) {
            if (!estObyazanBlack)
                if (pole[n].getZvet() == 3 || pole[n].getZvet() == 1)
                    if (pole[31].getZvet() == 0)
                        if (blackTurn)
                            pole[31].setPodsvet(true);
            if (!estObyazanWhite)
                if (pole[n].getZvet() == 4)
                    if (pole[31].getZvet() == 0)
                        if (!blackTurn)
                            pole[31].setPodsvet(true);
        }
        //----------Конец проверки----------------//


        if (pole[n].getZvet() == 3 || pole[n].getZvet() == 4) {
            if (pole[n].getWay20to29())
                hodDamka(n, true, 1);
            if (pole[n].getWay12to30())
                hodDamka(n, true, 2);
            if (pole[n].getWay4to31())
                hodDamka(n, true, 3);
            if (pole[n].getWay0to27())
                hodDamka(n, true, 4);
            if (pole[n].getWay1to19())
                hodDamka(n, true, 5);
            if (pole[n].getWay2to11())
                hodDamka(n, true, 6);

            if (pole[n].getWay1to12())
                hodDamka(n, false, 7);
            if (pole[n].getWay2to20())
                hodDamka(n, false, 8);
            if (pole[n].getWay3to28())
                hodDamka(n, false, 9);
            if (pole[n].getWay11to29())
                hodDamka(n, false, 10);
            if (pole[n].getWay19to30())
                hodDamka(n, false, 11);

        }
        if (pole[n].getZvet() == 1 || pole[n].getZvet() == 2) {
            if (pole[n].getWay20to29())
                hodShashka(n, true, 1);
            if (pole[n].getWay12to30())
                hodShashka(n, true, 2);
            if (pole[n].getWay4to31())
                hodShashka(n, true, 3);
            if (pole[n].getWay0to27())
                hodShashka(n, true, 4);
            if (pole[n].getWay1to19())
                hodShashka(n, true, 5);
            if (pole[n].getWay2to11())
                hodShashka(n, true, 6);
            JUMP = false;
            if (pole[n].getWay1to12())
                hodShashka(n, false, 7);
            if (pole[n].getWay2to20())
                hodShashka(n, false, 8);
            if (pole[n].getWay3to28())
                hodShashka(n, false, 9);
            if (pole[n].getWay11to29())
                hodShashka(n, false, 10);
            if (pole[n].getWay19to30())
                hodShashka(n, false, 11);
        }
        b = true;
        Log.e("end","//////");
        podsvetPolya();
        //MessageBox.Show(Convert.ToString(n) + Convert.ToString(clickColor) + Convert.ToString(num));
    }

    private boolean proverka(int n) {
        //проверка на необходимость бить
        boolean jump = false;
        if (pole[n].getWay20to29())
            jump = proverkaNaJump(n, true, 1);
        if (pole[n].getWay12to30())
            jump = proverkaNaJump(n, true, 2);
        if (pole[n].getWay4to31())
            jump = proverkaNaJump(n, true, 3);
        if (pole[n].getWay0to27())
            jump = proverkaNaJump(n, true, 4);
        if (pole[n].getWay1to19())
            jump = proverkaNaJump(n, true, 5);
        if (pole[n].getWay2to11())
            jump = proverkaNaJump(n, true, 6);

        if (pole[n].getWay1to12())
            jump = proverkaNaJump(n, false, 7);
        if (pole[n].getWay2to20())
            jump = proverkaNaJump(n, false, 8);
        if (pole[n].getWay3to28())
            jump = proverkaNaJump(n, false, 9);
        if (pole[n].getWay11to29())
            jump = proverkaNaJump(n, false, 10);
        if (pole[n].getWay19to30())
            jump = proverkaNaJump(n, false, 11);
        return jump;

    }

    private boolean proverkaNaJump(int n, boolean u, int c) {
        //функциия определяющая можно ли побить шашку соперника
        //если да возвращаем true
        //если нет возвращаем false

        try {
            //JUMP = false;
            if (pole[n].getZvet() == 1 || pole[n].getZvet() == 2)
            //если простая шашка
            {
                int i, z = 0;
                int k = 0; //пременная чтобы шашка не могла ить через все поле
                if (pole[n].getRyad() != 8)//ходы по диагонали вниз
                    for (i = (n + 1); i < 32; i++) {

                        k++;
                        if (k > 5)
                            break;
                        z = opredDiag(i, c);
                        if (z == c)//если шашки на одной диагонали
                        {
                            if (pole[n].getZvet() == 2 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))//если шашка того же цвета то выходим из функции
                                break;
                            if (pole[n].getZvet() == 1 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))//если шашка того же цвета то выходим из функции
                                break;
                            if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 2 && blackTurn == false) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 1 && blackTurn == true))
                            //если шашка другого цвета то мы входим в функцию
                            {
                                if (pole[i].getRyad() != 8 && pole[i].getStolbez() != 8 && pole[i].getStolbez() != 1)//
                                {
                                    if (pole[i].getRyad() % 2 == 0)//если четный ряд
                                    {
                                        if (u)//если диагональ слева на право
                                        {
                                            if (pole[i + 4].getZvet() == 0) {
                                                JUMP = true;
                                            }
                                        } else//диагональ справа налево
                                        {
                                            if (pole[i + 3].getZvet() == 0) {

                                                JUMP = true;

                                            }
                                        }
                                    } else//нечетный ряд
                                    {
                                        if (u)//диагональ слева на право
                                        {
                                            if (pole[i + 5].getZvet() == 0) {

                                                JUMP = true;

                                            }
                                        } else//диагональ справа на лево
                                        {
                                            if (pole[i + 4].getZvet() == 0) {

                                                JUMP = true;

                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }
                k = 0;
                if (pole[n].getRyad() != 1)
                    for (i = (n - 1); i >= 0; i--)//ходы по диагонали вверх
                    {
                        k++;
                        if (k > 5)
                            break;
                        // MessageBox.Show("i--"+Convert.ToString(i));
                        z = opredDiag(i, c);
                        if (z == c) {

                            if (pole[n].getZvet() == 2 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))
                                break;
                            if (pole[n].getZvet() == 1 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))
                                break;

                            if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 2 && blackTurn == false) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 1 && blackTurn == true)) {
                                if (pole[i].getRyad() != 1 && pole[i].getStolbez() != 1 && pole[i].getStolbez() != 8) {
                                    if (pole[i].getRyad() % 2 == 0)//четный ряд
                                    {
                                        if (u)//диагональ слева направо
                                        {
                                            if (pole[i - 5].getZvet() == 0) {
                                                JUMP = true;
                                            }
                                        } else//диагональ спарво на лево
                                        {
                                            if (pole[i - 4].getZvet() == 0) {
                                                JUMP = true;
                                            }
                                        }
                                    } else//нечетный ряд
                                    {
                                        if (u)//диагональ слева направо
                                        {
                                            if (pole[i - 4].getZvet() == 0) {
                                                JUMP = true;
                                            }
                                        } else//диагональ спарво на лево
                                        {
                                            if (pole[i - 3].getZvet() == 0) {
                                                JUMP = true;
                                            }
                                        }
                                    }
                                }

                            }

                        }
                    }
            }
            if (pole[n].getZvet() == 3 || pole[n].getZvet() == 4)
            //если шашка дамка
            {

                boolean q = true;
                int i, z = 0;

                if (pole[n].getRyad() != 8)//ходы по диагонали вниз
                    for (i = (n + 1); i < 32; i++) {
                        z = opredDiag(i, c);
                        if (z == c)//если шашки на одной диагонали
                        {
                            if (pole[n].getZvet() == 4 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))
                                break;
                            if (pole[n].getZvet() == 3 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))
                                break;

                            if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 4 && blackTurn == false) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 3 && blackTurn == true)) {
                                do {

                                    if (pole[i].getRyad() % 2 == 0 && pole[i].getRyad() != 8 && pole[i].getStolbez() != 8 && pole[i].getStolbez() != 1)//четный ряд
                                    {
                                        if (u)//диагональ слева направо
                                        {
                                            if (pole[i + 4].getZvet() == 0) {
                                                JUMP = true;
                                                i += 4;
                                            } else q = false;
                                        } else//диагональ справа налево
                                            if (pole[i + 3].getZvet() == 0) {
                                                JUMP = true;
                                                i += 3;
                                            } else q = false;
                                    } else//нечетный ряд
                                        if (pole[i].getRyad() != 8 && pole[i].getStolbez() != 8 && pole[i].getStolbez() != 1) {
                                            if (u)//диагональ слева направо
                                            {
                                                if (pole[i + 5].getZvet() == 0) {
                                                    JUMP = true;
                                                    i += 5;
                                                } else q = false;
                                            } else//диагональ справа налево
                                            {
                                                if (pole[i + 4].getZvet() == 0) {
                                                    JUMP = true;
                                                    i += 4;
                                                } else q = false;
                                            }
                                        } else {
                                            break;
                                        }
                                }
                                while (q);
                                break;
                            }

                        }
                    }
                if (pole[n].getRyad() != 1)
                    for (i = (n - 1); i >= 0; i--)//ходы по диагонали вверх
                    {
                        z = opredDiag(i, c);
                        if (z == c) {

                            if (pole[n].getZvet() == 4 && (pole[i].getZvet() == 2 || pole[i].getZvet() == 4))
                                break;
                            if (pole[n].getZvet() == 3 && (pole[i].getZvet() == 1 || pole[i].getZvet() == 3))
                                break;

                            if (((pole[i].getZvet() == 1 || pole[i].getZvet() == 3) && pole[n].getZvet() == 4 && blackTurn == false) || ((pole[i].getZvet() == 2 || pole[i].getZvet() == 4) && pole[n].getZvet() == 3 && blackTurn == true)) {
                                do {
                                    // MessageBox.Show("i++" + Convert.ToString(i));
                                    if (pole[i].getRyad() % 2 == 0 && pole[i].getRyad() != 1 && pole[i].getStolbez() != 1 && pole[i].getStolbez() != 8)
                                    //четный ряд
                                    {
                                        if (u)//диагональ слева направо
                                        {
                                            if (pole[i - 5].getZvet() == 0) {
                                                JUMP = true;
                                                i -= 5;
                                            } else q = false;
                                        } else//диагональ справа налево
                                        {
                                            if (pole[i - 4].getZvet() == 0) {
                                                JUMP = true;
                                                i -= 4;
                                            } else q = false;
                                        }
                                    } else//нечетный ряд
                                        if (pole[i].getRyad() != 1 && pole[i].getStolbez() != 1 && pole[i].getStolbez() != 8) {
                                            if (u)//диагональ слева направо
                                            {
                                                if (pole[i - 4].getZvet() == 0) {
                                                    JUMP = true;
                                                    i -= 4;
                                                } else q = false;
                                            } else//диагональ справа налево
                                            {
                                                if (pole[i - 3].getZvet() == 0) {
                                                    JUMP = true;
                                                    i -= 3;
                                                } else q = false;
                                            }

                                        } else
                                            break;

                                }
                                while (q);
                                break;
                            }

                        }
                    }
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
        return JUMP;
    }

    private void sjel2(int n, int c, int v) {
        try {
            //функция для съедения шашки
            if (n > num)
                for (int i = n - 1; i > num; i--) {
                    int z = 0;
                    z = opredDiag(i, c);
                    if (z == c && z == v) {
                        if (pole[num].getZvet() == 1 || pole[num].getZvet() == 3)
                            if (pole[i].getZvet() == 2 || pole[i].getZvet() == 4) {
                                pole[i].setZvet(0);
                                elLi = true;
                            }
                        if (pole[num].getZvet() == 2 || pole[num].getZvet() == 4)
                            if (pole[i].getZvet() == 1 || pole[i].getZvet() == 3) {
                                pole[i].setZvet(0);
                                elLi = true;
                            }
                    }

                }
            if (n < num)
                for (int i = n + 1; i < num; i++) {
                    int z = 0;
                    z = opredDiag(i, c);
                    if (z == c && z == v) {
                        if (pole[num].getZvet() == 1 || pole[num].getZvet() == 3)
                            if (pole[i].getZvet() == 2 || pole[i].getZvet() == 4) {
                                pole[i].setZvet(0);
                                elLi = true;
                            }
                        if (pole[num].getZvet() == 2 || pole[num].getZvet() == 4)
                            if (pole[i].getZvet() == 1 || pole[i].getZvet() == 3) {
                                pole[i].setZvet(0);
                                elLi = true;
                            }
                    }
                }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }

    }

    private void sjel1(int n, int c) {
        //функция определения диагонали
        if (pole[num].getWay20to29())
            sjel2(n, c, 1);
        if (pole[num].getWay12to30())
            sjel2(n, c, 2);
        if (pole[num].getWay4to31())
            sjel2(n, c, 3);
        if (pole[num].getWay0to27())
            sjel2(n, c, 4);
        if (pole[num].getWay1to19())
            sjel2(n, c, 5);
        if (pole[num].getWay2to11())
            sjel2(n, c, 6);

        if (pole[num].getWay1to12())
            sjel2(n, c, 7);
        if (pole[num].getWay2to20())
            sjel2(n, c, 8);
        if (pole[num].getWay3to28())
            sjel2(n, c, 9);
        if (pole[num].getWay11to29())
            sjel2(n, c, 10);
        if (pole[num].getWay19to30())
            sjel2(n, c, 11);
    }

    private int botHodUp(int number, int no) {
        for (int j = number; j < 32; j++) {
            if (!blackTurn) {
                break;
            }
            if (pole[j].getZvet() == 1 || pole[j].getZvet() == 3) {
                hodDlyaVseh(j, false);
                for (int i = 0; i < 32; i++) {
                    if (pole[i].getPodsvet()) {
                        if (i != no) {
                            konezHoda(i);
                            j = 0;
                            break;
                        }
                    }
                }
            }
        }
        return no;
    }

    private int botHodDown(int number, int no) {
        for (int j = number; j < 32; j++) {
            if (!blackTurn) {
                break;
            }
            if (pole[j].getZvet() == 1 || pole[j].getZvet() == 3) {
                hodDlyaVseh(j, false);
                for (int i = 31; i >= 0; i--) {
                    if (pole[i].getPodsvet()) {
                        if (i != no) {
                            konezHoda(i);
                            j = 0;
                            break;
                        }
                    }
                }
            }
        }
        return no;
    }

    private void konezHoda(int n) {
        try {
            for (int i = 0; i < 32; i++) {
                temp[iHod][i] = new Pole();
                temp[iHod][i].setZvet(pole[i].getZvet());
            }

            //функция для конца хода
            if (pole[n].getPodsvet()) {
                if (!blackTurn) {
                    //если ход белых
                    //запись в массив всех ходов белых
                    AIhod.add(iHod);
                    AIhod.set(iHodBack, iHod);
                    iHodBack++;
                }
                if (pole[n].getWay20to29()) sjel1(n, 1);
                if (pole[n].getWay12to30()) sjel1(n, 2);
                if (pole[n].getWay4to31()) sjel1(n, 3);
                if (pole[n].getWay0to27()) sjel1(n, 4);
                if (pole[n].getWay1to19()) sjel1(n, 5);
                if (pole[n].getWay2to11()) sjel1(n, 6);

                if (pole[n].getWay1to12()) sjel1(n, 7);
                if (pole[n].getWay2to20()) sjel1(n, 8);
                if (pole[n].getWay3to28()) sjel1(n, 9);
                if (pole[n].getWay11to29()) sjel1(n, 10);
                if (pole[n].getWay19to30()) sjel1(n, 11);

                whoseHod.add(blackTurn);
                whoseHod.set(iHod, blackTurn);
                iHod++;
                if (ai != Mode.FRIEND) {
                    if (blackTurn) {
                        backHod++;
                        setTrueB();
                    } else backHod = 0;
                } else setTrueB();

                JUMP = false;
                pole[num].setZvet(0);
                pole[n].setZvet(clickColor);
                boolean z = proverka(n);
                smena = blackTurn;
                if (elLi) {
                    if (!z) blackTurn = !blackTurn;
                } else blackTurn = !blackTurn;
                click = false;
                if (pole[n].getRyad() == 1) if (clickColor == 2) pole[n].setZvet(4);
                if (pole[n].getRyad() == 8) if (clickColor == 1) pole[n].setZvet(3);
                //-----------------------//
                //----Проверка на пат----//
                //-----------------------//
                delPodsvetPolya();
                countB = 0;
                countW = 0;
                for (int i = 0; i < 32; i++) {
                    if (blackTurn)
                        if (pole[i].getZvet() == 1 || pole[i].getZvet() == 3) {
                            delPodsvetPolya();
                            zvetPolya();
                            hodDlyaVseh(i, false);
                            Log.e("pole","----"+i+"----");
                            delPodsvetPolya();
                        }
                    if (!blackTurn)
                        if (pole[i].getZvet() == 2 || pole[i].getZvet() == 4) {
                            hodDlyaVseh(i, false);

                            delPodsvetPolya();
                        }
                }
                Log.e("moves count",countB+"//"+countW);
                count();

                if (ai == Mode.BLUETOOTH || ai == Mode.INTERNET) {
                    int turn = (blackTurn) ? 1 : 0;
                    if (black == 0) turn = 2;
                    if (white == 0) turn = 3;


                    if (!smena && (MySocket.server || role.equals(Keywords.Host)))
                        sendMes(turn);
                    if (smena && !MySocket.server || role.equals(Keywords.Guest))
                        sendMes(turn);

                }


                delPodsvetPolya();
                zvetPolya();
                podsvetPolya();


                if (blackTurn && countB == 0 && black > 0) black = 0;
                if (!blackTurn && countW == 0 && white > 0) white = 0;

                if (black == 0) {

                    ad.setTitle(getString(R.string.s7));  // заголовок
                    ad.show();
                }
                if (white == 0) {
                    ad.setTitle(getString(R.string.s8));  // заголовок
                    ad.show();
                }
                elLi = false;

            }
            delPodsvetPolya();
            podsvetPolya();
            //----конец проверки----//
        } catch (Exception e) {
        }
    }

    public void count() {
        black = white = 0;
        for (int i = 0; i < 32; i++) {
            if (pole[i].getZvet() == 1 || pole[i].getZvet() == 3) black++;
            if (pole[i].getZvet() == 2 || pole[i].getZvet() == 4) white++;
        }
        t1.setText(white + "x");
        t2.setText(black + "x");
    }

    public void zamena() {
        for (int i = 0; i < 32; i++) {
            //функция для возвращения хода на предыдущий
            pole[i].setZvet(temp[iHod][i].getZvet());
        }
    }

    //endregion
    //region ALLLISTENERS
    @Override
    public void onClick(View v) {

        TextView b = (TextView) v;
        //b.setText(b.getText());
        konezHoda(b.getId());
        hodDlyaVseh(b.getId(), false);
        Random rnd = new Random();
        int number = rnd.nextInt(32);
        boolean sec = true;
        //region hardAI

        //endregion
        //region easyAI
        if (blackTurn && (ai == Mode.EASY_AI  && sec)) {
            for (int j = number; j < 32; j++) {
                if (!blackTurn) {
                    break;
                }
                if (pole[j].getZvet() == 1 || pole[j].getZvet() == 3) {
                    hodDlyaVseh(j, false);
                    for (int i = number; i < 32; i++) {
                        if (pole[i].getPodsvet()) {
                            konezHoda(i);
                            j = 0;
                            break;
                        }
                    }
                    for (int i = number; i >= 0; i--) {
                        if (pole[i].getPodsvet()) {
                            konezHoda(i);
                            j = 0;
                            break;
                        }
                    }

                }


            }
            for (int j = number; j >= 0; j--) {
                if (!blackTurn) {
                    break;
                }
                if (pole[j].getZvet() == 1 || pole[j].getZvet() == 3) {
                    hodDlyaVseh(j, false);
                    for (int i = number; i < 32; i++) {
                        if (pole[i].getPodsvet()) {
                            konezHoda(i);
                            j = 32;
                            break;
                        }
                    }
                    for (int i = number; i >= 0; i--) {
                        if (pole[i].getPodsvet()) {
                            konezHoda(i);
                            j = 32;
                            break;
                        }
                    }
                }
            }
            delPodsvetPolya();
            podsvetPolya();
        }
        //endregion


    }

    public void back() {
        try {
            if (ai != Mode.FRIEND) {
                iHodBack--;
                iHod = AIhod.get(iHodBack);
                if (iHod == 0)
                    setFalseB();
            } else {
                iHod--;
                blackTurn = whoseHod.get(iHod);
                if (iHod == 0) setFalseB();
            }
            zamena();
            delPodsvetPolya();
            podsvetPolya();
            zvetPolya();
            count();
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG);
        }
    }

    //endregion
    public void write(byte[] out) {
        Log.e(TAG, "write: Write Called.");
        //perform the write
        ba.write(out);
    }
}
