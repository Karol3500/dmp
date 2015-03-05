package maszyna_wiertnicza;
/**
 *
 * @author Maciej
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Specimen implements Comparable<Specimen> {

    
    ArrayList<City> trasa;

    double ocena;
    double p_ruletka;
    boolean ocena_aktualna;
    Core alg;


    public Specimen(Core alg){
        trasa = new ArrayList<City>();
        ocena_aktualna=false;
        this.alg=alg;
    }
    
    
    public String showRoute(){
        String s="";

       
        //start ze startowego
       s+="("+alg.startCity.getNumber()+") ";
        for(int i=0;i<trasa.size();i++) {
        	if(((i+1) % alg.interwal_wymiany)==0){
        		//wykonaj powr�t do punktu startowego oraz przejdz do nast�pnego punktu z punktu startowego
        		  s+="<<"+alg.startCity.getNumber()+">> ";
        		  s+=trasa.get(i).getNumber()+" ";
        	} else { 
        		s+=trasa.get(i).getNumber()+" ";
        	}
        }
        //na koniec wracamy do punktu startowego
        s+="("+alg.startCity.getNumber()+")";
        return s;
    }
    
    public void setRoute(City[] cArray){
        for(City c:cArray){
        	trasa.add(c);
        }
    }
    
    public void setRoute(ArrayList<City> cArray){
    	trasa=cArray;
    }
    
    public ArrayList<City> getRoute(){
        return trasa;
    }
    
    public void swapCities(int i, int j){
    	City c1=trasa.get(i).clone();
    	City c2=trasa.get(j).clone();
    	trasa.set(i, c2);
    	trasa.set(j, c1);
    }


    public void shuffleRoute(){
    Collections.shuffle(trasa);   
    ocena_aktualna=false;
}

    public void addCity(City m){
        trasa.add(m);
        ocena_aktualna=false;
    }
    public void deleteCity(int x){
        trasa.remove(x);
        ocena_aktualna=false;
    }
    public void deleteCity(City m){
        for(int i=0;i<trasa.size();i++){
            if(trasa.get(i).getNumber()==m.getNumber()){
                trasa.remove(i);
                break;
            }
        }
        ocena_aktualna=false;
    }
    
    public void setCity(int x, City t){
        trasa.set(x, t);
        ocena_aktualna=false;
    }
    public City getCity(int x){
        return trasa.get(x);
    }
    
    public double getRate(){
        if(ocena_aktualna){
            return ocena;
        }
        double odleglosc=0.0;
        int i;
        
        //przechodzimy z punktu startowego do pierwszego punktu wiercenia
        odleglosc+=Math.sqrt(Math.pow((double)(alg.startCity.getX()-trasa.get(0).getX()),2)+Math.pow((double)(alg.startCity.getY()-trasa.get(0).getY()),2));
        
        for(i=0;i<trasa.size()-1;i++) {
        	if(((i+1) % alg.interwal_wymiany)==0){
        		//wykonaj powr�t do punktu startowego oraz przejdz do nast�pnego punktu z punktu startowego
        		odleglosc+=Math.sqrt(Math.pow((double)(trasa.get(i).getX()-alg.startCity.getX()),2)+Math.pow((double)(trasa.get(i).getY()-alg.startCity.getY()),2));
        		odleglosc+=Math.sqrt(Math.pow((double)(alg.startCity.getX()-trasa.get(i+1).getX()),2)+Math.pow((double)(alg.startCity.getY()-trasa.get(i+1).getY()),2));
        	} else {
            odleglosc+=Math.sqrt(Math.pow((double)(trasa.get(i).getX()-trasa.get(i+1).getX()),2)+Math.pow((double)(trasa.get(i).getY()-trasa.get(i+1).getY()),2));
        	}
        }
        //na koniec wracamy do punktu startowego
        odleglosc+=Math.sqrt(Math.pow((double)(trasa.get(i).getX()-alg.startCity.getX()),2)+Math.pow((double)(trasa.get(i).getY()-alg.startCity.getY()),2));
        
        ocena_aktualna=true;
        ocena=odleglosc;
        return ocena;
    }
    
    public int cityRepeats(City miastoZliczane){
        int powtorzenia=0;
        for(City m:trasa){
            if(miastoZliczane.cequals(m)){
                powtorzenia++;
            }
        }
        return powtorzenia;
    }
    
    public double getP_Roulette(){
        return p_ruletka;
    }
    
    public void setP_Roulette(double p){
        p_ruletka=p;
    }



    public Specimen clone(){
        
        Specimen o = new Specimen(alg);
        for(int i = 0; i < trasa.size(); i++){
            o.addCity(new City(getCity(i).getNumber(),getCity(i).getX(), getCity(i).getY()));
        }
        o.ocena=ocena;
        o.ocena_aktualna=ocena_aktualna;
        o.p_ruletka=p_ruletka;

        return o;
    }

    @Override
    public int compareTo(Specimen t) {
        double diff=getRate()-t.getRate();
        int comp;
        if(diff>0){
            comp=1;
        } else if(diff<0){
            comp=-1;
        } else {
            comp=0;
        }
        return comp;
    }

    public void Inver() {
        Random generator = new Random();
        int startCity = generator.nextInt(trasa.size()-1);//i=0...trasa.size-2
        int endCity = generator.nextInt(trasa.size()-startCity)+startCity;//j=startCity...trasa.size()-1
        for ( int start = startCity, end = endCity ;
              start < startCity+((endCity-startCity)/2);
              start++, end-- )
        {
            swapCities(start, end );
        }
        ocena_aktualna=false;
    }

    public void InverOver(ArrayList<Specimen> population, int distance, double crossingProbability) {
        Random generator = new Random();
        for(int i=0;i<distance;i++){
            if(generator.nextDouble()>crossingProbability){
                this.Inver();
                continue;
            }
            Specimen otherSpecimen = population.get(generator.nextInt(population.size()));
            int startCityIndex = generator.nextInt(trasa.size()-1);
            City startCity = trasa.get(startCityIndex);
            int endCityIndexInOtherSpecimen = otherSpecimen.getRoute().indexOf(startCity)+1;
            if(endCityIndexInOtherSpecimen==otherSpecimen.getRoute().size())//if selected city was last item in otherspecimen route
                continue;
            int endCityIndex = trasa.indexOf(otherSpecimen.getCity(endCityIndexInOtherSpecimen));

            if(endCityIndex<startCityIndex){
                int x = endCityIndex;
                endCityIndex = startCityIndex;
                startCityIndex=x;
            }
            for ( int start = startCityIndex, end = endCityIndex ;
                  start < startCityIndex+((endCityIndex-startCityIndex)/2);
                  start++, end-- )
                swapCities(start, end );

        }
        ocena_aktualna=false;
    }

    public void Inver2(int distance) {
        Random generator = new Random();
        int startCity = generator.nextInt(trasa.size()-1-distance);//i=0...trasa.size-2
        int endCity = startCity+distance;
        for ( int start = startCity, end = endCity ;
              start < startCity+((endCity-startCity)/2);
              start++, end-- )
        {
            swapCities(start, end );
        }
        ocena_aktualna=false;
    }
}
