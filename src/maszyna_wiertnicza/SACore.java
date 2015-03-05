package maszyna_wiertnicza;
import java.math.BigDecimal;

class SACore extends Core{



	Specimen currentSpecimen;
	Specimen mutatedSpecimen;
	Specimen bestSpecimen;
	double alpha; //wsp�czynnik ch�odzenia
	double TStart; //temp pocz�tkowa
    double len;		//dlugosc aktualnej trasy
    double bestLen;	//dlugosc najlepszej trasy
    int attempts; 

    int cycles;
    int cAmount; // liczba miast
	int[] cIds; // tablica id miast
	double[][] cCoords; // tablica koordynat�w miast
	City[] cList; // lista miast
	int bestCycle; // nr cyklu z bestSpecimenm osobnikiem
	long start; // start licznika
	long stop; //stop licznika
	boolean detailedStatsOn,plotOn,mapOn;
	
	SACore(MainPanel parent) {
		super(parent);
		
		currentSpecimen = new Specimen(this);
    	mutatedSpecimen = new Specimen(this);
    	bestSpecimen = new Specimen(this);
    	
		this.parent = parent;
		cAmount = parent.cAmount-1;
		cIds = parent.cIds;
		cCoords = parent.cCoords;
		
		// tu wczytuj� wsp�czynniki
		cycles = Integer.parseInt(parent.sapanel.cycles.getText());
		alpha = Double.parseDouble(parent.sapanel.alpha.getText());
		TStart = Double.parseDouble(parent.sapanel.alpha.getText());
		attempts = Integer.parseInt(parent.sapanel.attempts.getText());
		
		detailedStatsOn=parent.sapanel.statsOn.isSelected();
		plotOn=parent.sapanel.plotOn.isSelected();
		mapOn=parent.sapanel.mapOn.isSelected();

		// inicjalizacja zmiennych
		cList = new City[cAmount];
		bestLen = Double.MAX_VALUE;
		len = Double.MAX_VALUE;
		bestCycle = -1;
    	
		
	}
	
	public void run() {
		if(plotOn){
			parent.plot.resetData();
			parent.plot.setTitle("Przebieg algorytmu symulowanego wyżarzania");
			parent.plot.addXYSeries("Długość trasy");
		}
		
		
		for (int i = 0; i < cAmount; i++) {
			cList[i] = new City(cIds[i+1], cCoords[i+1][0], cCoords[i+1][1]);
		}
		startCity= new City(cIds[0], cCoords[0][0], cCoords[0][1]);
		
		if(mapOn){
			parent.map.clearAll();
			parent.map.addNode(startCity.getNumber(), startCity.getX(), startCity.getY(),"cyan");
		for(City c:cList){
			parent.map.addNode(c.getNumber(), c.getX(), c.getY(),null);
		}
		parent.map.setAutoScale();
	}
		
		start=System.currentTimeMillis(); // start licznika czasu
		parent.pb.setMaximum(cycles);
		for (int ep=0; !abort && ep<cycles; ep++) {
			simulate(ep);
			parent.pb.setValue(ep); // ustawienie warto�ci paska post�pu
		}
		stop=System.currentTimeMillis(); // stop licznika czasu
		showEffects();
		
		
	}
	

    
	//losowa inicjalizacja trasy
	void makeTour() {
        currentSpecimen=new Specimen(this);
        currentSpecimen.setRoute(cList);
        currentSpecimen.shuffleRoute();
        len = currentSpecimen.getRate();
   }
        
    //odwraca losowo wybrana podtrase i
    //wstawia za losowo wybranym miastem
   //odwraca losowo wybrany segment
   double mutacja1(double len) {
	   	City[] cMut=new City[cAmount];
    	int p1,p2; //punkty rozciecia
    	int sw=0, i,j,k=0;
    	p1 = (int)(cAmount*Math.random());
     	do {
    		p2 = (int)(Math.random()*cAmount); 
    	} while (Math.abs(p1-p2)<2||Math.abs(p1-p2)==cAmount-1);
    	if (p2<p1) {
    		sw=p1; p1=p2; p2=sw;
    	}
    	i=0;
    	do {
    		if (i<p1 || i>p2) {
    			cMut[k++]=currentSpecimen.getCity(i++);
     			//neigh[k++] = trasa[i++];
     		}
     		else
   			for (j=p2; j>=p1; j--) {
   				cMut[k++]=currentSpecimen.getCity(j);
     			//neigh[k++] = trasa[j];
     			i++;
  			}
    	} while(i<cAmount);
    	mutatedSpecimen=new Specimen(this);
    	mutatedSpecimen.setRoute(cMut);
    	len = mutatedSpecimen.getRate();
     	
    	return len;
    }    
       
   /*
    * wlasciwy algorytm SA
    */ 
	double simulate(int ep) {
		int i=0;

		double tempLen; //dlugosc mutanta;
		double T=TStart;

		
		//wyznaczam startowa trase
		makeTour();
		

		boolean success = false;
		boolean found   = false;
		boolean done    = false;
		int fail = 0;	//liczy porazki
		int succ;		//liczy sukcesy
		while (!done) {
			i = 0;
			succ = 0;
			success = false;
			found = false;
			while (!success) {
				tempLen = mutacja1(len);
				if (accept(tempLen, len,T)) {
					currentSpecimen=mutatedSpecimen.clone();
					//trasa = copy(neigh);
					len = tempLen;
					if (tempLen<bestLen) {
						bestSpecimen=mutatedSpecimen.clone();
						//bestT = copy(neigh);
						bestCycle=ep;
						bestLen = tempLen;
						found = true;
						succ++;
					}
				}
				i++; 
				
				success = (i>100*cAmount||succ>10*cAmount);
			}
			T = T*alpha;
			if (found) {
				fail = 0;
			} else fail++;
   			done = (fail==attempts);
		}
		
		//rysujemy
		if(plotOn){
		parent.plot.addPoint(0, ep, bestLen);
		}
		
		 double time=System.currentTimeMillis(); //1367415958031
         double miliseconds= time % 100000;
         int seconds = (int)(miliseconds / 1000);
         
     	if((seconds % 3 ==0) && mapOn){
     		//System.out.println(seconds);
 			parent.map.clearEdges();
 			parent.map.addEdge(startCity.getNumber(), bestSpecimen.trasa.get(0).getNumber(),null);
 			//System.out.println("1Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(0).getNumber());
 			int s;
 			for(s=0;s<bestSpecimen.trasa.size()-1;s++){
 				if(((s+1) % interwal_wymiany)==0){
 					parent.map.addEdge(bestSpecimen.trasa.get(s).getNumber(),startCity.getNumber(),"blue");
 					//System.out.println("2Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
 					parent.map.addEdge(startCity.getNumber(),bestSpecimen.trasa.get(s+1).getNumber(),"blue");
 					//System.out.println("3Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
 				} else {
 					parent.map.addEdge(bestSpecimen.trasa.get(s).getNumber(),bestSpecimen.trasa.get(s+1).getNumber(),null);
 					//System.out.println("4Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
 				}
 			}
 			parent.map.addEdge(bestSpecimen.trasa.get(s).getNumber(),startCity.getNumber(),null);
 			//System.out.println("5Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
 			parent.map.plotEdges();
 		}
		
		
		if(detailedStatsOn){
		String line = "Cykl #" + ep + " -> długość trasy: " + round(bestLen,2) + " Temperatura końcowa: "+round(T,2);
		addLine(line);
			}
		
		

    	return bestLen;
    }
    

    
    boolean accept(double tempLen, double len, double T) {
    	boolean yes = false;
    	if (tempLen<len) {
    		yes = true;
    	} else {
    		yes = (Math.random() < Math.exp(-(tempLen-len)/T));
    	}
    	return yes;
    }
    

	
	public void showEffects() {
		
		if(mapOn){
			/*parent.map.clearEdges();
			parent.map.addEdge(startCity.getNumber(), bestSpecimen.trasa.get(0).getNumber(),null);
			//System.out.println("1Adding "+startCity.getNumber()+"->"+bestSpecimen.trasa.get(0).getNumber());
			int i;
			for(i=0;i<bestSpecimen.trasa.size()-1;i++){
				if(((i+1) % interwal_wymiany)==0){
					parent.map.addEdge(bestSpecimen.trasa.get(i).getNumber(),startCity.getNumber(),"blue");
					//System.out.println("2Adding "+bestSpecimen.trasa.get(i).getNumber()+"->"+startCity.getNumber());
					parent.map.addEdge(startCity.getNumber(),bestSpecimen.trasa.get(i+1).getNumber(),"blue");
					//System.out.println("3Adding "+startCity.getNumber()+"->"+bestSpecimen.trasa.get(i+1).getNumber());
				} else {
					parent.map.addEdge(bestSpecimen.trasa.get(i).getNumber(),bestSpecimen.trasa.get(i+1).getNumber(),null);
					//System.out.println("4Adding "+bestSpecimen.trasa.get(i).getNumber()+"->"+bestSpecimen.trasa.get(i+1).getNumber());
				}
			}
			parent.map.addEdge(bestSpecimen.trasa.get(i).getNumber(),startCity.getNumber(),null);
			//System.out.println("5Adding "+bestSpecimen.trasa.get(i).getNumber()+"->"+startCity.getNumber());
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
		addLine(">>> Algorytm SYMULOWANEGO WYŻARZANIA zakończył pracę " + temp + "z następującym wynikiem:");
		addPhrase("Czas pracy algorytmu: " + (stop-start)/1000.0+" s");
		newLine();
		addLine("Interwał wymiany wiertła: " + interwal_wymiany);
		addLine("Długość trasy: " + bestLen);
		String tempS = "";
		if(abort){
			tempS = " ( z " + parent.pb.getValue() + ")";
		}
		addLine("Cykl w którym znaleziono najlepszą trasę: " + bestCycle + tempS);
		addPhrase("Najlepsza trasa: " + bestSpecimen.showRoute());
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
		return new BigDecimal(d).setScale(pos, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
}