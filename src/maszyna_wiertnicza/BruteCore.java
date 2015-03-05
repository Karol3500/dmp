package maszyna_wiertnicza;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class BruteCore extends Core{



	int cAmount; // liczba miast
	int[] cIds; // tablica id miast
	double[][] cCoords; // tablica koordynat�w miast
	List<City> cList; // lista miast
	double currentLen; // warto�� funkcji oceny najepszego osobnika
	int bestCycle; // nr cyklu z najlepszym osobnikiem
	Specimen best; // najlepszy osobnik ze wszystkich
	Specimen currentSpecimen; // obecny osobnik
	long start; // start licznika
	long stop; //stop licznika
	boolean detailedStatsOn,plotOn,mapOn;
	Permutations<City> permutationEngine;

	long permutationCount; // liczba permutacji
	
	
	// zmienne na potrzeby statystyki
	double bestLen;
	double cycleLen[];


	public BruteCore(MainPanel parent) {
		super(parent);
		
		// tu wczytuj� miasta
		this.parent = parent;
		cAmount = parent.cAmount-1;
		cIds = parent.cIds;
		cCoords = parent.cCoords;
		
		//permutationCount=silnia(cAmount);
		
		
		  BigInteger silnia = BigInteger.ONE;
	      String s = ""+cAmount;
	      BigInteger n=new BigInteger(s);
	 
	        while (n.compareTo(BigInteger.ONE)>0) {
	            silnia = silnia.multiply(n);
	            n = n.subtract(BigInteger.ONE);
	        }
	        
	        permutationCount=silnia.longValue();
		
		
		
		
		// tu wczytuj� wsp�czynniki
			detailedStatsOn=parent.brutepanel.statsOn.isSelected();
			plotOn=parent.brutepanel.plotOn.isSelected();
			mapOn=parent.brutepanel.mapOn.isSelected();

		// inicjalizacja zmiennych
		abort = false;
		bestLen = Double.POSITIVE_INFINITY;
		currentLen = Double.POSITIVE_INFINITY;
		bestCycle = -1;
		


		//genSumPow, gebSum
		
	}

	public void run() {
		
		if(plotOn){
			parent.plot.resetData();
			parent.plot.setTitle("Przebieg algorytmu brutalnego");
			parent.plot.addXYSeries("Długość trasy");
		}
		
		// utworzenie listy miast
		
		
		cList=new ArrayList<City>();
		for (int i = 0; i < cAmount; i++) {
			cList.add(new City(cIds[i+1], cCoords[i+1][0], cCoords[i+1][1]));
			
		}
		startCity= new City(cIds[0], cCoords[0][0], cCoords[0][1]);
		
		permutationEngine=new Permutations<City>(cList);
		
		if(mapOn){
				parent.map.clearAll();
				parent.map.addNode(startCity.getNumber(), startCity.getX(), startCity.getY(),"cyan");
			for(City c:cList){
				parent.map.addNode(c.getNumber(), c.getX(), c.getY(),null);
			}
			parent.map.setAutoScale();
		}
		
		

		start=System.currentTimeMillis(); // start licznika czasu
		// rozpoczynamy obliczenia
		int i = 0;
		
		parent.pb.setMaximum((int)permutationCount); //ustaw maksymaln� warto�� dla paska postepu
		try{
			while (!abort && permutationEngine.hasNext()) {
				
				generateSpecimen(i); // budowa i-tej populacji
				parent.pb.setValue(i); // ustawienie warto�ci paska post�pu
				i++;
			}
			stop=System.currentTimeMillis(); // stop licznika czasu
			showEffects();

		}catch(Exception e){
			System.out.println("Wystąpił następujący błąd:");
			System.out.println(e.getClass().getName());
			System.out.println(e.getMessage());
			addLine("============================================================================================================");
			addLine("Wystąpił następujący błąd:");
			addLine(e.getClass().getName());
			addLine(e.getMessage());
			addLine("============================================================================================================");
			e.printStackTrace();
		}		
	}


	

	

	public void generateSpecimen(int n) {
		// generowanie n-tego osobnika

			ArrayList<City> permutacja=(ArrayList)permutationEngine.next();
			currentSpecimen=new Specimen(this);
			currentSpecimen.setRoute(permutacja);
			currentLen=currentSpecimen.getRate();
			
			
		
			
			

			
			if(currentLen<bestLen){
			best=currentSpecimen;
			bestLen=currentLen;
			bestCycle=n;
			
		}
			
            double time=System.currentTimeMillis(); //1367415958031
            double miliseconds= time % 100000;
            int seconds = (int)(miliseconds / 1000);
            
        	if((seconds % 3 ==0) && mapOn){
        		//System.out.println(seconds);
    			parent.map.clearEdges();
    			parent.map.addEdge(startCity.getNumber(), best.trasa.get(0).getNumber(),null);
    			//System.out.println("1Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(0).getNumber());
    			int i;
    			for(i=0;i<best.trasa.size()-1;i++){
    				if(((i+1) % interwal_wymiany)==0){
    					parent.map.addEdge(best.trasa.get(i).getNumber(),startCity.getNumber(),"blue");
    					//System.out.println("2Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
    					parent.map.addEdge(startCity.getNumber(),best.trasa.get(i+1).getNumber(),"blue");
    					//System.out.println("3Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
    				} else {
    					parent.map.addEdge(best.trasa.get(i).getNumber(),best.trasa.get(i+1).getNumber(),null);
    					//System.out.println("4Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
    				}
    			}
    			parent.map.addEdge(best.trasa.get(i).getNumber(),startCity.getNumber(),null);
    			//System.out.println("5Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
    			parent.map.plotEdges();
    		}
			
		

			if(plotOn){
			parent.plot.addPoint(0, n, currentLen);
			}


			if(detailedStatsOn){
			String line = "Permutacja #" + n + " -> długość trasy: " + round(currentLen,2);
			addLine(line);
			}

			
			
			

	}
	

	

	
	public void showEffects() {
		
		
		if(mapOn){
/*			parent.map.clearEdges();
			parent.map.addEdge(startCity.getNumber(), best.trasa.get(0).getNumber(),null);
			//System.out.println("1Adding "+startCity.getNumber()+"->"+best.trasa.get(0).getNumber());
			int i;
			for(i=0;i<best.trasa.size()-1;i++){
				if(((i+1) % interwal_wymiany)==0){
					parent.map.addEdge(best.trasa.get(i).getNumber(),startCity.getNumber(),"blue");
					//System.out.println("2Adding "+best.trasa.get(i).getNumber()+"->"+startCity.getNumber());
					parent.map.addEdge(startCity.getNumber(),best.trasa.get(i+1).getNumber(),"blue");
					//System.out.println("3Adding "+startCity.getNumber()+"->"+best.trasa.get(i+1).getNumber());
				} else {
					parent.map.addEdge(best.trasa.get(i).getNumber(),best.trasa.get(i+1).getNumber(),null);
					//System.out.println("4Adding "+best.trasa.get(i).getNumber()+"->"+best.trasa.get(i+1).getNumber());
				}
			}
			parent.map.addEdge(best.trasa.get(i).getNumber(),startCity.getNumber(),null);
			//System.out.println("5Adding "+best.trasa.get(i).getNumber()+"->"+startCity.getNumber());
			 parent.map.plogEdges();
*/			
			
			
			parent.map.finishedSimulation();
		}
		
		// zapis wynik�w do logu i przebiegu
		addLine("============================================================================================================");
		addDate();
		String temp = "";
		if (abort == true) {
			temp = "(z powodu przerwania w " + round(100*parent.pb.getPercentComplete(),2) + " %) ";
		}
		addLine(">>> Algorytm BRUTALNY zakończył pracę " + temp + "z następującym wynikiem:");
		addPhrase("Czas pracy algorytmu: " + (stop-start)/1000.0+" s");
		newLine();
		addLine("Interwał wymiany wiertła: " + interwal_wymiany);
		addLine("Liczba wszystkich permutacji="+permutationCount);
		addLine("Długość trasy: " + bestLen);
		String tempS = "";
		if(abort){
			tempS = " ( z " + parent.pb.getValue() + ")";
		}
		addLine("Cykl w którym znaleziono najlepszą trasę: " + bestCycle + tempS);
		addPhrase("Najlepsza trasa: " + best.showRoute());
		newLine();

		addLine("============================================================================================================");

		// ustawienie przycisk�w g��wnego okna do stanu pozwalaj�cego na dalsze badania
		parent.pb.setVisible(false);
		parent.b1.setEnabled(true);
		parent.b2.setEnabled(true);
		parent.b3.setEnabled(false);
		parent.b_tour.setEnabled(true);
		parent.running = false;
		System.gc(); 
	}

	public void addPhrase(String s){
		parent.stats.addPhrase(s);
	}
	
	public void addDate(){
		parent.stats.addDate();
	}

	public void addLine(String s){
		parent.stats.addLine(s);
	}
	
	public void newLine(){
		parent.stats.newLine();
	}
	
	public double round(double d,int pos){
		if(Double.isInfinite(d) || Double.isNaN(d)){
			return -1;
		} else {
		return new BigDecimal(d).setScale(pos, BigDecimal.ROUND_HALF_UP).doubleValue();
		}
	}
	
	
	/*
	  public long silnia(int i) 
	  {
	    if (i == 0) 
	      return 1;
	    else 
	      return i * silnia(i - 1);
	  }
	*/

}