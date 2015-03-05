package maszyna_wiertnicza;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;


//parametry: populacja,pokolenia,p_mutacji, p_krzyzowania, metoda selekcji
public class GACore extends Core{
	

	int cAmount; // liczba miast
	int[] cIds; // tablica id miast
	double[][] cCoords; // tablica koordynat�w miast
	City[] cList; // lista miast
	long start; // start licznika
	long stop; //stop licznika
	boolean detailedStatsOn,plotOn,mapOn;
	
	
    static final int SEL_RULETKA = 1;
    static final int SEL_TURNIEJ = 2;
    static final int SEL_RANKING = 3;
    ArrayList<Specimen> populacja;
    ArrayList<Specimen> turniej;
    ArrayList<Specimen> ranking;
    int ranking_iter;
    int ranking_count;
    Specimen bestOsobnik;
    int ilosc_populacji;
    int ilosc_pokolen;
    double p_mutacji;
    double p_krzyzowania;
    int metoda_selekcji;
    int bestPokolenie;
    int dobry_wynik;
    double wynik_max;
    double wynik_min;

    GACore(MainPanel parent) {
    	super(parent);
        populacja = new ArrayList<Specimen>();
        turniej = new ArrayList<Specimen>();
        ranking = new ArrayList<Specimen>();
        
		// tu wczytuj� wsp�czynniki
        ilosc_populacji = Integer.parseInt(parent.gapanel.sAmount.getText());
        ilosc_pokolen = Integer.parseInt(parent.gapanel.genAmount.getText());
        p_mutacji = Double.parseDouble(parent.gapanel.kMutation.getText())/100.0;
        p_krzyzowania =Double.parseDouble(parent.gapanel.kHybrid.getText())/100.0;
        detailedStatsOn=parent.gapanel.statsOn.isSelected();
		plotOn=parent.gapanel.plotOn.isSelected();
		mapOn=parent.gapanel.mapOn.isSelected();
        
        
        
        if(parent.gapanel.srb1.isSelected()){
        	metoda_selekcji = SEL_TURNIEJ;
        } else if(parent.gapanel.srb2.isSelected()){
        	metoda_selekcji = SEL_RULETKA;
        } else {
        	metoda_selekcji = SEL_RANKING;
        }
        
        
        this.parent = parent;
		cAmount = parent.cAmount-1;
		cIds = parent.cIds;
		cCoords = parent.cCoords;



		// inicjalizacja zmiennych
		abort = false;
		cList = new City[cAmount];

		
		for (int i = 0; i < cAmount; i++) {
			cList[i] = new City(cIds[i+1], cCoords[i+1][0], cCoords[i+1][1]);
		}
		
		Specimen zero=new Specimen(this);
		startCity= new City(cIds[0], cCoords[0][0], cCoords[0][1]);
		zero.setRoute(cList);
		zero.shuffleRoute();
		populacja.add(zero);
		


    }

    public void run() {
    	
    			String algorytm ="genetycznego ";
    			if (metoda_selekcji == SEL_RULETKA) {
    		           algorytm+="ruletkowego";
    		        } else if (metoda_selekcji == SEL_TURNIEJ) {
    		        	algorytm+="turniejowego";
    		        } else {
    		        	algorytm+="rankingowego";
    		        }
    			if(plotOn){
    					parent.plot.resetData();
    					parent.plot.setTitle("Przebieg algorytmu "+algorytm);
    					//tu wa�na kolejno�� dodawania serii
    					parent.plot.addXYSeries("Minimalna długość");
    					parent.plot.addXYSeries("Średnia długość");
    					parent.plot.addXYSeries("Maksymalna długość");
    			}
    			
    			if(mapOn){
    				parent.map.clearAll();
    				parent.map.addNode(startCity.getNumber(), startCity.getX(), startCity.getY(),"cyan");
    			for(City c:cList){
    				parent.map.addNode(c.getNumber(), c.getX(), c.getY(),null);
    			}
    			parent.map.setAutoScale();
    			}
    			
    			start=System.currentTimeMillis(); // start licznika czasu
                initOsobniki();

                Collections.sort(populacja);
                wynik_max = getMaxTrasa();
                wynik_min = getMinTrasa();
                bestOsobnik = populacja.get(0).clone();
                parent.pb.setMaximum(ilosc_pokolen); //ustaw maksymaln� warto�� dla paska postepu

                for (int pok = 0; !abort && pok < ilosc_pokolen; pok++) {
                	parent.pb.setValue(pok); // ustawienie warto�ci paska post�pu
                    ArrayList<Specimen> populacja_kolejna = new ArrayList<Specimen>();
                    if(metoda_selekcji == SEL_RANKING){
                    ranking = new ArrayList<Specimen>();
                    ranking_iter=0;
                    int minimum=20;
                	if(ilosc_populacji<minimum){
                		minimum=ilosc_populacji;
                	}
                	double losowi=Math.random()*ilosc_populacji/2;
                	ranking_count = minimum + (int)(losowi);
                    }
                    
                    if (metoda_selekcji == SEL_RULETKA) {
                            double sumaOdwrotnosciOcen = 0.0;
                            for (Specimen os : populacja) {
                                sumaOdwrotnosciOcen += 1/os.getRate();
                            }

                            for (Specimen os : populacja) {
                                os.setP_Roulette((1/os.getRate()) / sumaOdwrotnosciOcen);

                                //System.out.print("Ocena: "+os.getRate()+" Prawd: ");
                                //System.out.println(((1/os.getRate()) / sumaOdwrotnosciOcen));
                            }
                            
                            double sumatest=0.0;
                            for (Specimen os : populacja) {
                                sumatest += os.getP_Roulette();
                            }
                            //System.out.println("SUMA PRAWD RULETKI: "+sumatest);
                        }
                    
                    //printPopulacja();
                    
                    while (populacja_kolejna.size() < populacja.size()) {

                        

                        Specimen k1 = selekcja(metoda_selekcji);
                        Specimen k2 = null;

                        if (ilosc_populacji > (populacja_kolejna.size() + 1) && Math.random() < p_krzyzowania) {  
                            k2 = selekcja(metoda_selekcji);
                            krzyzowanie(k1, k2);
                        }

                        if (Math.random() < p_mutacji) {
                            mutacja(k1);
                            populacja_kolejna.add(k1);
                        } else {
                            populacja_kolejna.add(k1);
                        }

                        if (k2 != null && Math.random() < p_mutacji) {
                            mutacja(k2);
                            populacja_kolejna.add(k2);
                        } else if (k2 != null) {
                            populacja_kolejna.add(k2);
                        }

                    }

                    populacja = populacja_kolejna;
                    Collections.sort(populacja);
                    populacja_kolejna = new ArrayList<Specimen>();

                    if (wynik_max < getMaxTrasa()) {
                        wynik_max = getMaxTrasa();
                    }

                    if (wynik_min > getMinTrasa()) {
                    	bestPokolenie=pok;
                    	bestOsobnik=populacja.get(0).clone();
                        wynik_min = getMinTrasa();
                        
                    }

                   //System.out.println("BEST TRASA:"+populacja.get(0).showTrasa());
                    //System.out.println("NrPok: " + pok + " MAX:" + getMaxTrasa() + " MIN:" + getMinTrasa() + " AVG:" + getAvgTrasa());

                     //if(pok % 50==0)
                         //printPopulacja();
                    double time=System.currentTimeMillis(); //1367415958031
                    double miliseconds= time % 100000;
                    int seconds = (int)(miliseconds / 1000);
                    
                	if((seconds % 2 ==0) && mapOn){
                		//System.out.println(seconds);
            			parent.map.clearEdges();
            			parent.map.addEdge(startCity.getNumber(), bestOsobnik.trasa.get(0).getNumber(),null);
            			//System.out.println("1Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(0).getNumber());
            			int i;
            			for(i=0;i<bestOsobnik.trasa.size()-1;i++){
            				if(((i+1) % interwal_wymiany)==0){
            					parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(),startCity.getNumber(),"blue");
            					//System.out.println("2Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
            					parent.map.addEdge(startCity.getNumber(),bestOsobnik.trasa.get(i+1).getNumber(),"blue");
            					//System.out.println("3Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
            				} else {
            					parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(),bestOsobnik.trasa.get(i+1).getNumber(),null);
            					//System.out.println("4Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
            				}
            			}
            			parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(),startCity.getNumber(),null);
            			//System.out.println("5Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
            			parent.map.plotEdges();
            		}
                         
        			if(plotOn){
        				parent.plot.addPoint(0, pok, getMinTrasa());
        				parent.plot.addPoint(1, pok, getAvgTrasa());
        				parent.plot.addPoint(2, pok, getMaxTrasa());
        				}
                    
        			if(detailedStatsOn){
        			String line = "Pokolenie #" + pok + " -> najlepsza: " + round(getMinTrasa(),2)+ " średnia: "+round(getAvgTrasa(),2)+" najgorsza: "+round(getMaxTrasa(),2);
        			parent.stats.addLine(line);
        			}
        			
        			//String line = "Gen #" + fNum + " -> top: " + fRun + " best: " + fBest + " average: " + fAvg + " worst: " + fWorst + " variation: " + fVar;

        


                }
                
                
                stop=System.currentTimeMillis(); // stop licznika czasu
    			showEffects();

              
                //zad dom: profiler w JAVIE


            }
           
           
            //System.out.println("WYNIKI: MIN#MAX#AVG "+sredniaMin+" # "+sredniaMax+" # "+sredniaAvg);
            
        

        

    

    Specimen selekcja(int metoda) {
        if (metoda == SEL_RULETKA) {
            return selekcja_ruletka();
        } else if (metoda == SEL_TURNIEJ) {
            return selekcja_turniejowa();
        } else {
        	  return selekcja_rankingowa();
        	  
        }
    }

    Specimen selekcja_ruletka() {
        double los = Math.random();
        //System.out.println("LOS="+los);

        double currentSumaP = 0.0;
        int j;
        for (j = 0; j < populacja.size()-1; j++) {
            currentSumaP += populacja.get(j).getP_Roulette();
            if (los <= currentSumaP) {
                break;
            }
        }
        //System.out.println("SELEKCJA: "+j+" currentsump:"+currentSumaP);
        return populacja.get(j);

    }

    Specimen selekcja_turniejowa() {
        if(!turniej.isEmpty()){
            Specimen os=turniej.get(0);
            turniej.remove(0);
            //System.out.println("SELEKCJA OSOBNIKA " + os.showTrasa()+" OCENA: "+os.getRate());
            return os;
        }
        
        int przedzial=2+(int)(Math.random()*((populacja.size()/2)-1));
        //System.out.println("Przedzial:"+przedzial);
        ArrayList<ArrayList<Specimen>> turnieje=new ArrayList<ArrayList<Specimen>>();
        turnieje.add(new ArrayList<Specimen>());
        
        ArrayList<Specimen> populacja_rand=clonePopulacja();
        Collections.shuffle(populacja_rand);
        int licznik_grupy=przedzial;
        for(int i=0;i<populacja_rand.size();i++){
            if(licznik_grupy==0){
                licznik_grupy=przedzial;
                turnieje.add(new ArrayList<Specimen>());
            }
            turnieje.get(turnieje.size()-1).add(populacja_rand.get(i));
            licznik_grupy--;
        }
        
        //System.out.println("GRUP: "+turnieje.size());
        
        for(ArrayList<Specimen> grupa:turnieje){
            Collections.sort(grupa);
            turniej.add(grupa.get(0));
            //System.out.println("SELEKCJA OSOBNIKA TRASA:" + grupa.get(0).showTrasa()+" OCENA: "+grupa.get(0).getRate());
        }
        
        Specimen os=turniej.get(0);
            turniej.remove(0);
            //System.out.println("SELEKCJA OSOBNIKA " + os.showTrasa()+" OCENA: "+os.getRate());
            return os;
        
        //return populacja.get((int) (Math.random() * populacja.size()));
    }
    
    Specimen selekcja_rankingowa() {
    	
    	if(ranking_iter>=ranking_count){
        	ranking_iter=0;
        }
    	
    	if(ranking.isEmpty()){
            ranking=createRanking();
            //Collections.sort(ranking);
    	}
    	
        Specimen os=ranking.get(ranking_iter++);
        

        //System.out.println("SELEKCJA OSOBNIKA " + os.showTrasa()+" OCENA: "+os.getRate());
        return os;

        }
    
    
    Specimen selekcja_rankingowa_old() {
    	
    	if(ranking.isEmpty()){
            ranking=clonePopulacja();
            Collections.sort(ranking);
    	}
    	
        Specimen os=ranking.get(0);
        ranking.remove(0);
        //System.out.println("SELEKCJA OSOBNIKA " + os.showTrasa()+" OCENA: "+os.getRate());
        return os;

        }
       
    

    void krzyzowanie(Specimen o1, Specimen o2) {

        int startindex = (int) (Math.random() * (o1.getRoute().size())); //[0,1) * 10= [0,9]
        int endindex = startindex + (int) (Math.random() * (o1.getRoute().size() - startindex));

        
        /*
        System.out.println("STARTINDEX: " + startindex);
        System.out.println("ENDINDEX: " + endindex);
        System.out.println("PRZED WYMIANA");
        System.out.println("OSOBNIK o1:"+o1.showTrasa());
        System.out.println("OSOBNIK o2:"+o2.showTrasa());
        */
        Specimen ox_o1=new Specimen(this);
        Specimen ox_o2=new Specimen(this);
        
        for(int j=endindex+1;j<o1.getRoute().size();j++){
            ox_o1.addCity(o2.getCity(j).clone());
            ox_o2.addCity(o1.getCity(j).clone());
        }
        
        for(int j=0;j<startindex;j++){
            ox_o1.addCity(o2.getCity(j).clone());
            ox_o2.addCity(o1.getCity(j).clone());
        }
        
        

        for (int i = startindex; i <= endindex; i++) {
            City temp = o1.getCity(i);
            o1.setCity(i, o2.getCity(i));
            ox_o1.deleteCity(o2.getCity(i));
            o2.setCity(i, temp);
            ox_o2.deleteCity(temp);
        }
        /*
        System.out.println("PO WYMIANIE");
        System.out.println("OSOBNIK o1:"+o1.showTrasa());
        System.out.println("OSOBNIK o2:"+o2.showTrasa());
        System.out.println("OSOBNIK oxo1:"+ox_o1.showTrasa());
        System.out.println("OSOBNIK oxo2:"+ox_o2.showTrasa());
        */
        for(int i=endindex+1;i<o1.getRoute().size();i++){
            o1.setCity(i, ox_o1.getCity(0));
            ox_o1.deleteCity(0);
            o2.setCity(i, ox_o2.getCity(0));
            ox_o2.deleteCity(0);
        }
        
        for(int i=0;i<startindex;i++){
            o1.setCity(i, ox_o1.getCity(0));
            ox_o1.deleteCity(0);
            o2.setCity(i, ox_o2.getCity(0));
            ox_o2.deleteCity(0);
        }
        
        /*
        
        for(int i=0;i<o1.getRoute().size();i++){
            if (o1.powtorzeniaMiasta(o1.getCity(i)) > 1) {
                    System.out.println("POWTORZENIE ZOSTALO MIASTA:"+o1.getCity(i).getNumer()+" TRASA:"+o1.showTrasa());
                    throw new Exception("err");
                }
        }
        
        for(int i=0;i<o2.getRoute().size();i++){
            if (o1.powtorzeniaMiasta(o2.getCity(i)) > 1) {
                    System.out.println("POWTORZENIE ZOSTALO MIASTA:"+o2.getCity(i).getNumer()+" TRASA:"+o2.showTrasa());
                    throw new Exception("err");
                }
        }
        
        */
        
        /*
        System.out.println("PO NAPRAWIE");
        System.out.println("OSOBNIK o1:"+o1.showTrasa());
        System.out.println("OSOBNIK o2:"+o2.showTrasa());
        System.out.println("OSOBNIK oxo1:"+ox_o1.showTrasa());
        System.out.println("OSOBNIK oxo2:"+ox_o2.showTrasa());
        */
        
        
        
        
        /*
        
        for (Odwzorowanie odwz : odwzorowania) {
            System.out.println("ODWZ: "+odwz.t1.getNumer()+"<=>"+odwz.t2.getNumer());
        }

        for (Osobnik os : osobnikiDoPoprawy) {
            for (int i = 0; i < os.getRoute().size(); i++) {
                if (i == startindex) {
                    i = endindex;
                    continue;
                }
                if (os.powtorzeniaMiasta(os.getCity(i)) > 1) {
                    System.out.println("Szukam odwz dla miasta: "+os.getCity(i).getNumer());
                    for (Odwzorowanie odwz : odwzorowania) {
                        
                        Miasto m1=null; 
                        if(os==o1){
                        m1= odwz.getParaOs1(os.getCity(i));
                        } else {
                            m1= odwz.getParaOs2(os.getCity(i));
                        }
                        if (m1 != null) {
                            System.out.println("ODWZOROWANIE: " + os.getCity(i).getNumer() + "<=>" + m1.getNumer());
                            os.setCity(i, m1);
                            break;
                        }
                    }
                }
                
                if (os.powtorzeniaMiasta(os.getCity(i)) > 1) {
                    System.out.println("POWTORZENIE ZOSTALO MIASTA:"+os.getCity(i).getNumer()+" TRASA:"+os.showTrasa());
                    throw new Exception("err");
                }
                
                    
            }
            
            
            
        }
*/



    }

    void mutacja(Specimen o) {
        int startindex = (int) (Math.random() * (o.getRoute().size() - 1)); //[0,1) * 10= [0,9]
        int endindex = startindex + 1 + (int) (Math.random() * (o.getRoute().size() - 1 - startindex));

        //System.out.println("STARTINDEX: "+startindex);
        //System.out.println("ENDINDEX: "+endindex);

        ArrayList<City> odwracaneMiasta = new ArrayList<City>();
        for (int i = startindex; i <= endindex; i++) {
            odwracaneMiasta.add(o.getCity(i));
        }

        for (int i = 0; startindex + i <= endindex; i++) {
            o.setCity(startindex + i, odwracaneMiasta.get(odwracaneMiasta.size() - 1 - i));
        }


    }
    
    ArrayList<Specimen> createRanking(){
        ArrayList<Specimen> wynik= new ArrayList<Specimen>();
        for(int i=0;i<ranking_count;i++){
            wynik.add(populacja.get(i).clone());
        }
        return wynik;
    }
    
    ArrayList<Specimen> clonePopulacja(){
        ArrayList<Specimen> wynik= new ArrayList<Specimen>();
        for(Specimen os:populacja){
            wynik.add(os.clone());
        }
        return wynik;
    }

    void initOsobniki() {
        Specimen zero=populacja.get(0);
        zero.shuffleRoute();
        //System.out.println("OSOBNIK SHUFFLE:"+zero.showTrasa());
        populacja=new ArrayList<Specimen>();
        populacja.add(zero);
        for (int i = 0; i < ilosc_populacji; i++) {
            Specimen nowy = populacja.get(0).clone();
            nowy.shuffleRoute();
            populacja.add(nowy);
        }
    }

    protected void finish() {
        populacja = new ArrayList<Specimen>();
    }

    void printPopulacja() {

        for (int i = 0; i < populacja.size(); i++) {
        	//+ populacja.get(i).showRoute()
            System.out.println("OSOBNIK " + i + "=" +" OCENA: "+round(populacja.get(i).getRate(),3)+ " PRAWD RULETKI: "+populacja.get(i).getP_Roulette());
        }
    }

    double getMinTrasa() {
        if(populacja.get(0).getRate()<1){
            System.out.println("ZERO TRASA:"+populacja.get(0).showRoute());
        }
        return populacja.get(0).getRate();
    }

    double getMaxTrasa() {
        return populacja.get(populacja.size() - 1).getRate();
    }

    double getAvgTrasa() {
        double srednia = 0;
        for (Specimen os : populacja) {
            srednia += os.getRate();
        }
        return (srednia / populacja.size());
    }


    
    public void showEffects() {
    	
    	if(mapOn){
/*			parent.map.clearEdges();
			parent.map.addEdge(startCity.getNumber(), bestOsobnik.trasa.get(0).getNumber(),null);
			//System.out.println("1Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(0).getNumber());
			int i;
			for(i=0;i<bestOsobnik.trasa.size()-1;i++){
				if(((i+1) % interwal_wymiany)==0){
					parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(),startCity.getNumber(),"blue");
					//System.out.println("2Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
					parent.map.addEdge(startCity.getNumber(),bestOsobnik.trasa.get(i+1).getNumber(),"blue");
					//System.out.println("3Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
				} else {
					parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(),bestOsobnik.trasa.get(i+1).getNumber(),null);
					//System.out.println("4Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
				}
			}
			parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(),startCity.getNumber(),null);
			//System.out.println("5Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
			parent.map.plotEdges();*/
			parent.map.finishedSimulation();
		}
    	
		// zapis wynik�w do logu i przebiegu
		addLine("============================================================================================================");
		addDate();
		String temp = "";
		String metoda ="";
		if (abort == true) {
			temp = "(z powodu przerwania w " + round(100*parent.pb.getPercentComplete(),2) + " %) ";
		}
		if (metoda_selekcji == SEL_RULETKA) {
           metoda="RULETKOWY";
        } else if (metoda_selekcji == SEL_TURNIEJ) {
        	metoda="TURNIEJOWY";
        } else {
        	metoda="RANKINGOWY";
        }
		addLine(">>> Algorytm GENETYCZNY "+metoda+" zakończył " + temp + "z wynikiem:");
		addPhrase("Czas pracy algorytmu: " + (stop-start)/1000.0+" s");
		newLine();
		addLine("Interwał wymiany wiertła: " + interwal_wymiany);
		addLine("Długość trasy: " + bestOsobnik.getRate());
		String tempS = "";
		if(abort){
			tempS = " ( z " + parent.pb.getValue() + ")";
		}
		addLine("Pokolenie w którym znaleziono najlepszego osobnika: " + bestPokolenie + tempS);
		addPhrase("Najlepsza trasa: " + bestOsobnik.showRoute());
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
