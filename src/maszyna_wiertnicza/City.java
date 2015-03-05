package maszyna_wiertnicza;
/**
 *
 * @author Maciej
 */
public class City {

    private int _numer;
    private double _x;
    private double _y;


    public City(int numer,double x, double y){
        _x = x;
        _y = y;
        _numer=numer;
    }


    public City getCity(){
        return this;
    }

    public double getX(){
        return _x;
    }

    public double getY(){
        return _y;
    }
    
    public int getNumber(){
        return _numer;
    }
    
    public void setNumber(int numer){
        _numer=numer;
    }
    
    public boolean cequals(City m){
        return _numer==m.getNumber();
    }

    @Override
    public boolean equals(Object m){
        City c = (City) m;
        return _numer==c.getNumber();
    }

    public City clone(){
        return new City(_numer,_x,_y);
    }
    
    public String toString(){
    	return _numer+","+_x+","+_y;
    }

    
}
