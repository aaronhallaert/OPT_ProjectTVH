package PlotPackage;


import Entities.Depot;
import Entities.Client;
import Entities.Location;
import SolutionEntities.Cluster;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GraphBuilder extends ApplicationFrame{

    public GraphBuilder(String title, HashMap<Depot, Cluster> clusters, HashMap<Location, Depot> depots){
        super(title);


        // PLOTTEN VAN CLUSTERS
        ArrayList<XYSeries> clusterPoints= new ArrayList<>();

        for (Map.Entry<Depot, Cluster> entry : clusters.entrySet()) {
            final XYSeries locationPoints = new XYSeries("cluster "+entry.getValue().getId());
            for (Client client : entry.getValue().getClusterClients()) {
                double lat= client.getLocation().getLatitude();
                double lng= client.getLocation().getLongitude();
                locationPoints.add(lng, lat);
            }
            clusterPoints.add(locationPoints);

        }


        // PLOTTEN VAN DEPOTS
        final XYSeries depotPoints = new XYSeries("Depots");

        for (Map.Entry<Location, Depot> entry : depots.entrySet()) {
            double lat= entry.getKey().getLatitude();
            double lng= entry.getKey().getLongitude();
            depotPoints.add(lng, lat);
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
