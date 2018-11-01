package PlotPackage;


import Entities.Depot;
import Entities.Job;
import Entities.Location;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.util.*;

public class GraphBuilder extends ApplicationFrame{

    public GraphBuilder(String title, HashMap<Depot, Set<Job>> clusters, HashMap<Location, Depot> depots){
        super(title);


        // PLOTTEN VAN CLUSTERS
        ArrayList<XYSeries> clusterPoints= new ArrayList<>();

        int i= 1;
        for (Map.Entry<Depot, Set<Job>> entry : clusters.entrySet()) {
            final XYSeries locationPoints = new XYSeries("cluster "+i);
            for (Job job : entry.getValue()) {
                double lat= job.getLocation().getLatitude();
                double lng= job.getLocation().getLongitude();
                locationPoints.add(lat, lng);
            }
            clusterPoints.add(locationPoints);

            i++;
        }


        // PLOTTEN VAN DEPOTS
        final XYSeries depotPoints = new XYSeries("Depots");

        for (Map.Entry<Location, Depot> entry : depots.entrySet()) {
            double lat= entry.getKey().getLatitude();
            double lng= entry.getKey().getLongitude();
            depotPoints.add(lat, lng);
        }

        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(depotPoints);
        for (XYSeries clusterPoint : clusterPoints) {
            data.addSeries(clusterPoint);
        }
        final JFreeChart chart = ChartFactory.createScatterPlot("Locations", "Longitude", "Latitude",
                data, PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel= new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }


}
