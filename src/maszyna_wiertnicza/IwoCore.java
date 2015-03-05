package maszyna_wiertnicza;

import java.io.Console;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * Created by Karol on 2014-05-25.
 */
public class IwoCore extends Core {

    int metoda_selekcji;
    int cAmount; // liczba miast
    int[] cIds; // tablica id miast
    double[][] cCoords; // tablica koordynat�w miast
    City[] cList; // lista miast
    long start; // start licznika
    long stop; //stop licznika
    boolean detailedStatsOn,plotOn,mapOn;

    static final int SEL_INWERSJA = 1;
    static final int SEL_INVEROVER= 2;

    ArrayList<Specimen> populacja;
    Specimen bestOsobnik;
    double nonLinearCoefficient;
    double initStandardDeviation;
    double finalStandardDeviation;
    int minSeed;
    int maxSeed;
    int iloscOsobnikowWPopulacji;
    int maxIloscOsobnikowWPopulacji;
    int iloscIteracji;
    int bestPokolenie;
    int maksymalnaLiczbaZiaren;//na osobnika
    int dobry_wynik;
    double wynik_max;
    double wynik_min;
    double crossingProbability = 0.2;

    IwoCore(MainPanel parent) {
        super(parent);

        populacja = new ArrayList<Specimen>();

        // tu wczytuj� wsp�czynniki
        iloscIteracji = Integer.parseInt(parent.iwoPanel.iterationCount.getText());
        iloscOsobnikowWPopulacji = Integer.parseInt(parent.iwoPanel.startWeedCount.getText());
        maxIloscOsobnikowWPopulacji = Integer.parseInt(parent.iwoPanel.maxWeedCount.getText());
        maksymalnaLiczbaZiaren = Integer.parseInt(parent.iwoPanel.maxSeedNumber.getText());
        maxSeed = Math.max(Integer.parseInt(parent.iwoPanel.maxSeedNumber.getText()), 5 );
        nonLinearCoefficient = Double.parseDouble(parent.iwoPanel.nonLinearCoefficient.getText());
        initStandardDeviation = Integer.parseInt(parent.iwoPanel.initStandardDeviation.getText());
        finalStandardDeviation = Integer.parseInt(parent.iwoPanel.finalStandardDeviation.getText());
        minSeed=maxSeed/5;

        if(parent.iwoPanel.srb1.isSelected()){
            metoda_selekcji = SEL_INWERSJA;
        } else if(parent.iwoPanel.srb2.isSelected()){
            metoda_selekcji = SEL_INVEROVER;
        }

        detailedStatsOn=parent.iwoPanel.statsOn.isSelected();
        plotOn=parent.iwoPanel.plotOn.isSelected();
        mapOn=parent.iwoPanel.mapOn.isSelected();

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

        String algorytm = "IWO ";

        if (metoda_selekcji == SEL_INWERSJA) {
            algorytm+="Inwersja";
        } else if (metoda_selekcji == SEL_INVEROVER) {
            algorytm+="Inver-over";
        }
        if (plotOn) {
            parent.plot.resetData();
            parent.plot.setTitle("Przebieg algorytmu " + algorytm);
            //tu wa�na kolejno�� dodawania serii
            parent.plot.addXYSeries("Minimalna długość");
            parent.plot.addXYSeries("Średnia długość");
            parent.plot.addXYSeries("Maksymalna długość");
        }

        if (mapOn) {
            parent.map.clearAll();
            parent.map.addNode(startCity.getNumber(), startCity.getX(), startCity.getY(), "cyan");
            for (City c : cList) {
                parent.map.addNode(c.getNumber(), c.getX(), c.getY(), null);
            }
            parent.map.setAutoScale();
        }

        start = System.currentTimeMillis(); // start licznika czasu
        initOsobniki(); // inicjalizuje poczatkowa populacje losowo

        Collections.sort(populacja);
        wynik_max = getMaxTrasa();
        wynik_min = getMinTrasa();
        bestOsobnik = populacja.get(0).clone();
        parent.pb.setMaximum(iloscIteracji); //ustaw maksymaln� warto�� dla paska postepu
        for (int pok = 0; !abort && pok < iloscIteracji; pok++) {//dopoki nie wykonano zadanej ilosci iteracji

            parent.pb.setValue(pok); // ustawienie wartości paska postępu
            //obliczanie ilosci ziaren rzucanych przez kazdego osobnika
            int distance = calculateDistance(pok,iloscIteracji);
            //<Double> fitness = calculateFitnessValue(populacja); //oblicza wartosc dopasowania dla kazdego osobnika
            ArrayList<Specimen> newSpecimens = new ArrayList<>();
            for(int specIndex = 0; specIndex < populacja.size();specIndex++)//Dla kazdego osobnika w populacji
            {
                Specimen aktualny = populacja.get(specIndex);
                int seedNumber = calculateSeedNumber(specIndex,populacja);//oblicz  ilosc ziaren

                if(seedNumber> maxSeed)
                    seedNumber = maxSeed;

                for(int childSpecimenCount = 0;childSpecimenCount<seedNumber;childSpecimenCount++)
                {
                    Specimen childSpecimen = aktualny.clone();
                    if(metoda_selekcji == SEL_INWERSJA) {
                        for (int distanceCounter = 0; distanceCounter < distance; distanceCounter++) {
                            childSpecimen.Inver();
                        }
                    }

                    if(metoda_selekcji == SEL_INVEROVER){
                        Random generator = new Random();
                        for (int distanceCounter = 0; distanceCounter < distance; distanceCounter++) {
                            childSpecimen.InverOver(populacja,distance, crossingProbability);
                        }
                    }

                    newSpecimens.add(childSpecimen);
//                    System.out.println("Iteracja: "+pok+"osobnik: "+specIndex+" dziecko: "+childSpecimenCount);
                }
            }
            populacja.addAll(newSpecimens);
            newSpecimens.clear();
            Collections.sort(populacja);
            if(populacja.size()>maxIloscOsobnikowWPopulacji)
            {
                while(populacja.size()!=maxIloscOsobnikowWPopulacji)
                {
                    populacja.remove(populacja.size()-1);
                }
            }
            if (wynik_max < getMaxTrasa()) {
                wynik_max = getMaxTrasa();
            }
            if (wynik_min > getMinTrasa()) {
                bestPokolenie=pok;
                bestOsobnik=populacja.get(0).clone();
                wynik_min = getMinTrasa();

            }

                double time = System.currentTimeMillis(); //1367415958031
                double miliseconds = time % 100000;
                int seconds = (int) (miliseconds / 1000);

                if ((seconds % 2 == 0) && mapOn) {
                    //System.out.println(seconds);
                    parent.map.clearEdges();
                    parent.map.addEdge(startCity.getNumber(), bestOsobnik.trasa.get(0).getNumber(), null);
                    //System.out.println("1Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(0).getNumber());
                    int i;
                    for (i = 0; i < bestOsobnik.trasa.size() - 1; i++) {
                        if (((i + 1) % interwal_wymiany) == 0) {
                            parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(), startCity.getNumber(), "blue");
                            //System.out.println("2Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
                            parent.map.addEdge(startCity.getNumber(), bestOsobnik.trasa.get(i + 1).getNumber(), "blue");
                            //System.out.println("3Adding "+startCity.getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
                        } else {
                            parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(), bestOsobnik.trasa.get(i + 1).getNumber(), null);
                            //System.out.println("4Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+bestOsobnik.trasa.get(i+1).getNumber());
                        }
                    }
                    parent.map.addEdge(bestOsobnik.trasa.get(i).getNumber(), startCity.getNumber(), null);
                    //System.out.println("5Adding "+bestOsobnik.trasa.get(i).getNumber()+"->"+startCity.getNumber());
                    parent.map.plotEdges();
                }

                if (plotOn) {
                    parent.plot.addPoint(0, pok, getMinTrasa());
                    parent.plot.addPoint(1, pok, getAvgTrasa());
                    parent.plot.addPoint(2, pok, getMaxTrasa());
                }

                if (detailedStatsOn) {
                    String line = "Pokolenie #" + pok + " -> najlepsza: " + round(getMinTrasa(), 2) + " średnia: " + round(getAvgTrasa(), 2) + " najgorsza: " + round(getMaxTrasa(), 2);
                    parent.stats.addLine(line);
                }

                //String line = "Gen #" + fNum + " -> top: " + fRun + " best: " + fBest + " average: " + fAvg + " worst: " + fWorst + " variation: " + fVar;


            }


            stop = System.currentTimeMillis(); // stop licznika czasu
            showEffects();
        }

    private int calculateDistance(int aktualnaIteracja, int iloscIteracji) {
        return (int)Math.round(Math.pow((iloscIteracji - aktualnaIteracja)/iloscIteracji,nonLinearCoefficient)*(initStandardDeviation-finalStandardDeviation)+finalStandardDeviation);
    }


    ArrayList<Double> calculateFitnessValue(ArrayList<Specimen> populacja)
    {
        ArrayList<Double> fitness = new ArrayList<>();
        for(Specimen individual : populacja)
        {
            fitness.add(individual.getRate());
        }
        return fitness;
    }

    int calculateSeedNumber(int specimenIndex, ArrayList<Specimen> populacja)
    {
        double specRate = (populacja.get(specimenIndex)).getRate();
        double minRate  = populacja.get(populacja.size()-1).getRate();
        double maxRate  = populacja.get(0).getRate();
        return (minSeed + (int)java.lang.Math.floor((minRate - specRate) * ((maxSeed - minSeed) / (minRate - maxRate))));
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
        for (int i = 0; i < iloscOsobnikowWPopulacji; i++) {
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

        addLine(">>> Algorytm IWO "+metoda+" zakończył " + temp + "z wynikiem:");
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
