import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import java.util.ArrayList;
import java.util.List;

public class Graph extends ApplicationFrame{

    public Graph(String title, List<Cluster> clusters, List<Depot> depots){
        super(title);


        List<XYSeries> allClusters = new ArrayList<>();
        int i = 0;
        for (Cluster cluster : clusters) {
            final XYSeries clusterPoints = new XYSeries("Cluster "+i);
            allClusters.add(clusterPoints);
            for(Location loc : cluster.members){
                clusterPoints.add(loc.getLatitude(), loc.getLongitude());
            }
            i++;
        }

        final XYSeries depotPoints = new XYSeries("Depots");

        for (Depot depot : depots) {
            double lat= depot.getLocation().getLatitude();
            double lng= depot.getLocation().getLongitude();
            depotPoints.add(lat, lng);
        }

        final XYSeriesCollection data = new XYSeriesCollection();
        data.addSeries(depotPoints);
        for(XYSeries series : allClusters){
            data.addSeries(series);
        }
        final JFreeChart chart = ChartFactory.createScatterPlot("Locations", "Longitude", "Latitude",
                data, PlotOrientation.VERTICAL, true, true, false);

        final ChartPanel chartPanel= new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        setContentPane(chartPanel);

    }


}