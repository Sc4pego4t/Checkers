package ru.scapegoats.checkers.util;

/**
 * Created by Андрей on 02.10.2017.
 */

public class Pole {
    int stolbez;
    int mesto;
    int zvetShashki;
    boolean focus;
    boolean podsvet;
    int ryad;

    boolean way1to32;
    boolean way2to24;
    boolean way9to31;
    boolean way3to16;
    boolean way17to30;

    boolean way4to25;
    boolean way8to29;
    boolean way3to17;
    boolean way16to30;
    boolean way2to9;
    boolean way24to31;


       //------------------------//
      //----------get-----------//
     //------------------------//
    public int getRyad()
    {
        return ryad;
    }

    public int getStolbez()
    {
        return stolbez;
    }

    public boolean getWay20to29()
    {
        return way8to29;
    }

    public boolean getWay12to30()
    {
        return way4to25;
    }

    public boolean getWay4to31()
    {
        return way3to17;
    }

    public boolean getWay0to27()
    {
        return way2to24;
    }

    public boolean getWay1to19()
    {
        return way24to31;
    }

    public boolean getWay2to11()
    {
        return way2to9;
    }

    public boolean getWay1to12()
    {
        return way16to30;
    }

    public boolean getWay2to20()
    {
        return way9to31;
    }

    public boolean getWay3to28()
    {
        return way3to16;
    }

    public boolean getWay11to29()
    {
        return way17to30;
    }

    public boolean getWay19to30()
    {
        return way1to32;
    }

    public int getMesto()
    {
        return ryad;
    }

    public int getZvet()
    {
        return zvetShashki;
    }
    public boolean getFocus()
    {
        return focus;
    }
    public boolean getPodsvet()
    {
        return podsvet;
    }

    //-------------------------///
    //------------------------//
    //----------set-----------//
    //------------------------//
    public void setRyad(int x)
    {
        ryad=x;
    }

    public void setStolbez(int x)
    {
        stolbez=x;
    }

    public void setWay20to29(boolean x)
    {
        way8to29=x;
    }

    public void setWay12to30(boolean x)
    {
        way4to25=x;
    }

    public void setWay4to31(boolean x)
    {
        way3to17=x;
    }

    public void setWay0to27(boolean x)
    {
        way2to24=x;
    }

    public void setWay1to19(boolean x)
    {
        way24to31=x;
    }

    public void setWay2to11(boolean x)
    {
        way2to9=x;
    }

    public void setWay1to12(boolean x)
    {
        way16to30=x;
    }

    public void setWay2to20(boolean x)
    {
        way9to31=x;
    }

    public void setWay3to28(boolean x)
    {
        way3to16=x;
    }

    public void setWay11to29(boolean x)
    {
        way17to30=x;
    }

    public void setWay19to30(boolean x)
    {
        way1to32=x;
    }

    public void setMesto(int x)
    {
        ryad=x;
    }

    public void setZvet(int x)
    {
        zvetShashki=x;
    }
    public void setFocus(boolean x)
    {
        focus=x;
    }
    public void setPodsvet(boolean x)
    {
        podsvet=x;
    }
}
