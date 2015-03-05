package maszyna_wiertnicza;
import java.math.BigDecimal;

public class RandomCore extends Core{


	int cAmount; // liczba miast
	int[] cIds; // tablica id miast
	double[][] cCoords; // tablica koordynat�w miast
	City[] cList; // lista miast
	double[][] lengths; // macie� d�ugo�ci tras mi�dzy poszczeg�lnymi miastami
	double currentLen; // warto�� funkcji oceny najepszego osobnika
	int bestCycle; // nr cyklu z najlepszym osobnikiem
	Specimen best; // najlepszy osobnik ze wszystkich
	Specimen currentSpecimen; // obecny osobnik
	long start; // start licznika
	long stop; //stop licznika
	boolean detailedStatsOn,plotOn,mapOn;
	

	int cycles; // liczba cykli

	
	
	// zmienne na potrzeby statystyki
	double bestLen;
	double cycleLen[];


	public RandomCore(MainPanel parent) {
		super(parent);
		// buduj� randomizer


		
		// tu wczytuj� miasta
		this.parent = parent;
		cAmount = parent.cAmount-1;
		cIds = parent.cIds;
		cCoords = parent.cCoords;
		
		// tu wczytuj� wsp�czynniki
		cycles = Integer.parseInt(parent.randpanel.cycles.getText());
		detailedStatsOn=parent.randpanel.statsOn.isSelected();
		plotOn=parent.randpanel.plotOn.isSelected();
		mapOn=parent.randpanel.mapOn.isSelected();
		// inicjalizacja zmiennych
		cList = new City[cAmount];
		bestLen = Double.POSITIVE_INFINITY;
		currentLen = Double.POSITIVE_INFINITY;
		bestCycle = -1;
		
		cycleLen = new double[cycles + 1];

		//genSumPow, gebSum
		
	}

	public void run() {
		
		//utworzenie serii danych na wykresie
		if(plotOn){
			parent.plot.resetData();
			parent.plot.setTitle("Przebieg algorytmu losowego");
			parent.plot.addXYSeries("Długość trasy");
		}
		// utworzenie listy miast
		for (int i = 0; i < cAmount; i++) {
			cList[i] = new City(cIds[i+1], cCoords[i+1][0], cCoords[i+1][1]);
		}
		startCity= new City(cIds[0], cCoords[0][0], cCoords[0][1]);
		currentSpecimen = new Specimen(this);
		currentSpecimen.setRoute(cList);
		
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
		parent.pb.setMaximum(cycles); //ustaw maksymaln� warto�� dla paska postepu
		try{
			while (!abort && i <= cycles) {
				
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
		// generowanie n-tej populacji
	
			
		
			//randomizuj i zrob nowego
			currentSpecimen.shuffleRoute();
			currentLen=currentSpecimen.getRate();
			
			

			//sprawdz czy lepszy od poprzednik�w
			if(currentLen<bestLen){
			best=currentSpecimen;
			bestLen=currentLen;
			bestCycle=n;
			}
			
			
			
			//rysujemy
			if(plotOn){
			parent.plot.addPoint(0, n, currentLen);
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
				


				if(detailedStatsOn){
				String line = "Los #" + n + " -> długość trasy: " + round(currentLen,2);
				addLine(line);
				}
			
			
		}
		


	

	
	public void showEffects() {
		
		if(mapOn){
			/*parent.map.clearEdges();
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
			parent.map.plotEdges();*/
			parent.map.finishedSimulation();
		}
		
		// zapis wynik�w do logu i przebiegu
		addLine("============================================================================================================");
		addDate();
		String temp = "";
		if (abort == true) {
			temp = "(z powodu przerwania w " + round(100*parent.pb.getPercentComplete(),2) + " %) ";
		}
		addLine(">>> Algorytm LOSOWY zakończył pracę " + temp + "z następującym wynikiem:");
		addPhrase("Czas pracy algorytmu: " + (stop-start)/1000.0+" s");
		newLine();
		addLine("Interwał wymiany wiertła: " + interwal_wymiany);
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
	

}