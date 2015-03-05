package maszyna_wiertnicza;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Karol on 2014-05-25.
 */
public class IwoPanel extends JPanel implements ActionListener {
    JRadioButton srb3;
    JRadioButton srb2;
    JRadioButton srb1;
    Graphics g = getGraphics();
    ButtonGroup selectionMethod;
    JTextField interval;
    JLabel notice;
    JCheckBox statsOn;
    JCheckBox mapOn;
    JCheckBox plotOn;
    JTextField startWeedCount,maxWeedCount,iterationCount, maxSeedNumber, nonLinearCoefficient, initStandardDeviation, finalStandardDeviation;
    JLabel lStartWeedCount,lMaxWeedCount,lIterationCount,lMaxSeedNumber, lNonLinearCoefficient, lInitStandardDeviation, lFinalStandardDeviation;
    JPanel selSettings;
    JPanel basicSettings;
    JPanel otherSettings;
    JLabel warning;

    public IwoPanel()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets =  new Insets(6,5,0,5);
        gbc.gridx = 0;
        gbc.gridy = 99;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        JLabel placeholder = new JLabel("");
        add(placeholder, gbc);
        gbc.weighty = 0.0;


        // kontener na ustawienia selekcji
        selSettings = new JPanel();
        selSettings.setLayout(new GridBagLayout());
        GridBagConstraints gbcSS = new GridBagConstraints();
        gbcSS.fill = GridBagConstraints.NONE;
        gbcSS.anchor = GridBagConstraints.WEST;
        gbcSS.insets =  new Insets(3,5,3,5);
        gbcSS.gridx = 99;
        gbcSS.gridy = 3;
        gbcSS.weightx = 1.0;
        gbcSS.weighty = 1.0;
        JLabel placeholder2 = new JLabel("");
        selSettings.add(placeholder2, gbcSS);
        gbcSS.weightx = 0.0;
        gbcSS.weighty = 0.0;

        //grupa przycisk�w (selekcja)
        selectionMethod = new ButtonGroup();
        srb1 = new JRadioButton("Inwersja");
        selectionMethod.add(srb1);
        gbcSS.gridx = 0;
        gbcSS.gridy = 0;
        selSettings.add(srb1, gbcSS);
        srb2 = new JRadioButton("Inver-over");
        selectionMethod.add(srb2);
        gbcSS.gridx = 0;
        gbcSS.gridy = 1;
        selSettings.add(srb2, gbcSS);

        srb1.setSelected(true);


        TitledBorder selSetBorder = BorderFactory.createTitledBorder("Operator odwracania trasy");
        selSettings.setBorder(selSetBorder);
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(selSettings, gbc);


        // kontener na ustawienia podstawowe algorytmu
        basicSettings = new JPanel();
        basicSettings.setLayout(new GridBagLayout());
        GridBagConstraints gbcBS = new GridBagConstraints();
        gbcBS.fill = GridBagConstraints.NONE;
        gbcBS.anchor = GridBagConstraints.WEST;
        gbcBS.insets =  new Insets(3,5,3,5);
        gbcBS.gridx = 99;
        gbcBS.gridy = 4;
        gbcBS.weightx = 1.0;
        gbcBS.weighty = 1.0;
        JLabel placeholder0 = new JLabel("");
        basicSettings.add(placeholder0, gbcBS);
        gbcBS.weightx = 0.0;
        gbcBS.weighty = 0.0;

        startWeedCount = new JTextField("10", 5);
        lStartWeedCount = new JLabel("Początkowa liczba osobników");
        lStartWeedCount.setLabelFor(startWeedCount);
        gbcBS.gridx = 0;
        gbcBS.gridy = 0;
        basicSettings.add(startWeedCount, gbcBS);
        gbcBS.gridx = 1;
        gbcBS.gridy = 0;
        basicSettings.add(lStartWeedCount, gbcBS);

        maxWeedCount = new JTextField("50", 5);
        lMaxWeedCount = new JLabel("Maksymalna liczba osobników ");
        lMaxWeedCount.setLabelFor(maxWeedCount);
        gbcBS.gridx = 0;
        gbcBS.gridy = 1;
        basicSettings.add(maxWeedCount, gbcBS);
        gbcBS.gridx = 1;
        gbcBS.gridy = 1;
        basicSettings.add(lMaxWeedCount, gbcBS);

        iterationCount = new JTextField("50", 5);
        lIterationCount= new JLabel("Liczba pokoleń");
        lIterationCount.setLabelFor(iterationCount);
        gbcBS.gridx = 0;
        gbcBS.gridy = 2;
        basicSettings.add(iterationCount, gbcBS);
        gbcBS.gridx = 1;
        gbcBS.gridy = 2;
        basicSettings.add(lIterationCount, gbcBS);

        maxSeedNumber = new JTextField("10", 5);
        lMaxSeedNumber = new JLabel("Maksymalna liczba ziaren");
        lMaxSeedNumber.setLabelFor(maxSeedNumber);
        gbcBS.gridx = 0;
        gbcBS.gridy = 3;
        basicSettings.add(maxSeedNumber, gbcBS);
        gbcBS.gridx = 1;
        gbcBS.gridy = 3;
        basicSettings.add(lMaxSeedNumber, gbcBS);

        nonLinearCoefficient = new JTextField("1", 5);
        lNonLinearCoefficient = new JLabel("Współczynnik modulacji nieliniowej");
        lNonLinearCoefficient.setLabelFor(nonLinearCoefficient);
        gbcBS.gridx = 0;
        gbcBS.gridy = 4;
        basicSettings.add(nonLinearCoefficient, gbcBS);
        gbcBS.gridx = 1;
        gbcBS.gridy = 4;
        basicSettings.add(lNonLinearCoefficient, gbcBS);

        initStandardDeviation = new JTextField("15", 5);
        lInitStandardDeviation = new JLabel("Początkowa ilość przekształceń na ziarnach");
        lInitStandardDeviation.setLabelFor(initStandardDeviation);
        gbcBS.gridx = 0;
        gbcBS.gridy = 5;
        basicSettings.add(initStandardDeviation, gbcBS);
        gbcBS.gridx = 1;
        gbcBS.gridy = 5;
        basicSettings.add(lInitStandardDeviation, gbcBS);

        finalStandardDeviation = new JTextField("2", 5);
        lFinalStandardDeviation = new JLabel("Końcowa ilość przekształceń na ziarnach");
        lFinalStandardDeviation.setLabelFor(finalStandardDeviation);
        gbcBS.gridx = 0;
        gbcBS.gridy = 6;
        basicSettings.add(finalStandardDeviation, gbcBS);
        gbcBS.gridx = 1;
        gbcBS.gridy = 6;
        basicSettings.add(lFinalStandardDeviation, gbcBS);


        TitledBorder basicSetBorder = BorderFactory.createTitledBorder("Ustawienia algorytmu IWO");
        basicSettings.setBorder(basicSetBorder);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(basicSettings, gbc);





        //kontener na pozosta�e ustawienia
        otherSettings = new JPanel();
        otherSettings.setLayout(new GridBagLayout());
        GridBagConstraints gbcOS = new GridBagConstraints();
        gbcOS.fill = GridBagConstraints.NONE;
        gbcOS.anchor = GridBagConstraints.EAST;
        gbcOS.insets =  new Insets(3,5,3,5);
        gbcOS.gridx = 99;
        gbcOS.gridy = 4;
        gbcOS.weightx = 1.0;
        gbcOS.weighty = 1.0;
        JLabel placeholderx = new JLabel("");
        otherSettings.add(placeholderx, gbcOS);
        gbcOS.weightx = 0.0;
        gbcOS.weighty = 0.0;

        statsOn = new JCheckBox("Dokładne statystyki");
        statsOn.setSelected(false);
        gbcOS.gridx = 0;
        gbcOS.gridy = 0;
        otherSettings.add(statsOn, gbcOS);

        plotOn = new JCheckBox("Rysowanie wykresu");
        plotOn.setSelected(false);
        gbcOS.gridx = 1;
        gbcOS.gridy = 0;
        otherSettings.add(plotOn, gbcOS);

        mapOn = new JCheckBox("Rysowanie mapy");
        mapOn.setSelected(false);
        gbcOS.gridx = 2;
        gbcOS.gridy = 0;
        otherSettings.add(mapOn, gbcOS);


        TitledBorder otherSetBorder = BorderFactory.createTitledBorder("Pozostałe");
        otherSettings.setBorder(otherSetBorder);
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(otherSettings, gbc);

        warning=new JLabel("Uwaga: Użycie dodatkowych ustawień może zmniejszyć prędkość działania programu.");
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(warning,gbc);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.black);

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
