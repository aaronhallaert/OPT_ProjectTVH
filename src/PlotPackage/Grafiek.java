package PlotPackage;


import Entities.Depot;
import Entities.Location;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.util.List;

public class Grafiek extends ApplicationFrame{

    public Grafiek(String title, List<Location> locations, List<Depot> depots){
        super(title);


        final XYSeries locationPoints = new XYSeries("Locations");

        for (Location location : locations) {
            double lat= location.getLatitude();
            double lng= location.getLongitude();
            locationPoints.add(lat, lng);
        }

        final XYSeries depotPoints = new XYSeries("Depots");

        for (Depot depot : depots) {
            double lat= depot.getLocation().getLatitude();
            double lng= depot.getLocation().getLongitude();
            depotPoints.add(lat, lng);
        }

        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(depotPoints);
        data.addSeries(locationPoints);
        final JFreeChart chart = ChartFactory.createScatterPlot("Locations", "Longitude", "Latitude",
                data, PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel= new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }


}
