package maszyna_wiertnicza;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JPanel;
import org.jfree.chart.*;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.*;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;


public class PlotPanel 
{
	
	public JPanel chartPanel;
	private XYDataset dataset;
	public ArrayList<XYSeries> xyseries;
	private JFreeChart jfreechart;
	private static Color[] colors={Color.red,Color.yellow,Color.CYAN,Color.orange};
	private int lastcolor;
       

        public PlotPanel()
        {
        		lastcolor=0;
        		chartPanel = createDemoPanel();
                //chartPanel.setLayout(null);
                //chartPanel.setVisible(true);
        		xyseries = new ArrayList<XYSeries>();
        }
        
        public void setTitle(String title){
        	jfreechart.setTitle(title);
        }
        

        
        public int addXYSeries(String s){
        	XYSeries newseries= new XYSeries(s);
        	xyseries.add(newseries);
        	int index=xyseries.size()-1;
        	XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(); 
        	renderer.setBaseShapesVisible(false);
        	renderer.setSeriesPaint(0, colors[lastcolor++]);
        	jfreechart.getXYPlot().setRenderer(index, renderer); 
        	return index;
        }
        
        public void resetData(){
        	lastcolor=0;
        	xyseries = new ArrayList<XYSeries>();
        	jfreechart.getXYPlot().setDataset(0,null);
        	jfreechart.getXYPlot().setDataset(1,null);
        	jfreechart.getXYPlot().setDataset(2,null);
        }
        
   

    
        private JFreeChart createChart()
        {
                JFreeChart jfreechart = ChartFactory.createXYLineChart("Wykres przebiegu działania algorytmów", "Cykl", "Długość trasy", dataset, PlotOrientation.VERTICAL, true, true, false);
                XYPlot xyplot = (XYPlot)jfreechart.getPlot();
                xyplot.setDomainPannable(true);
                xyplot.setRangePannable(true);
                NumberAxis numberaxis = new NumberAxis(null);
                xyplot.setDomainAxis(1, numberaxis);
                NumberAxis numberaxis1 = new NumberAxis(null);
                xyplot.setRangeAxis(1, numberaxis1);
                java.util.List list = Arrays.asList(new Integer[] {
                        new Integer(0), new Integer(1)
                });
                xyplot.mapDatasetToDomainAxes(0, list);
                xyplot.mapDatasetToRangeAxes(0, list);
                ChartUtilities.applyCurrentTheme(jfreechart);
                return jfreechart;
        }


        
        public void addPoint(int i,double x, double y){
        	XYSeries seria=xyseries.get(i);
        	seria.add(x,y);
        	dataset=new XYSeriesCollection(seria);
        	jfreechart.getXYPlot().setDataset(i,dataset);
        }

        private JPanel createDemoPanel()
        {
                jfreechart = createChart();
                
                ChartPanel chartpanel = new ChartPanel(jfreechart);
                chartpanel.setMouseWheelEnabled(true);
                return chartpanel;
        }

        

}