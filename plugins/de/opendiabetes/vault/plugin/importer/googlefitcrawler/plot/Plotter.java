package de.opendiabetes.vault.plugin.importer.googlefitcrawler.plot;

import de.opendiabetes.vault.plugin.importer.googlefitcrawler.location.LocationHistory;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.models.Activity;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.models.ActivityTypes;
import de.opendiabetes.vault.plugin.importer.googlefitcrawler.models.HeartRate;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class Plotter extends JFrame {
    private JFreeChart chart;
    private String timeframe;


    public Plotter(String timeframe) {
        super(timeframe);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.timeframe = timeframe;

        GregorianCalendar start = new GregorianCalendar();
        GregorianCalendar end = new GregorianCalendar();

        if (timeframe.contains("-")) {
            String[] help = timeframe.split("-");

            String[] startDate = help[0].split("\\.");
            start.set(Integer.parseInt(startDate[2]), Integer.parseInt(startDate[1]) - 1, Integer.parseInt(startDate[0]), 0, 0, 0);
            start.set(Calendar.MILLISECOND, 0);

            String[] endDate = help[1].split("\\.");
            end.set(Integer.parseInt(endDate[2]), Integer.parseInt(endDate[1]) - 1, Integer.parseInt(endDate[0]), 23, 59, 59);
            end.set(Calendar.MILLISECOND, 999);
        } else {
            String[] date = timeframe.split("\\.");
            start.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]), 0, 0, 0);
            start.set(Calendar.MILLISECOND, 0);

            end.set(Integer.parseInt(date[2]), Integer.parseInt(date[1]) - 1, Integer.parseInt(date[0]), 23, 59, 59);
            end.set(Calendar.MILLISECOND, 999);
        }

        List<Activity> activities = LocationHistory.getInstance().getActivitiesForMultipleDays(start.getTimeInMillis(), end.getTimeInMillis());

        int activityTypeCount = 0;
        List<Integer> seenActivityTypes = new ArrayList<>();
        for (int i = 0; i < activities.size(); i++) {
            if (!seenActivityTypes.contains(activities.get(i).getActivity())) {
                seenActivityTypes.add(activities.get(i).getActivity());
                activityTypeCount++;
            }
        }

        Collections.sort(seenActivityTypes);

        String[] tmp = new String[activityTypeCount];
        for (int i = 0; i < activityTypeCount; i++) {
            tmp[i] = ActivityTypes.getInstance().getReadableActivityType(seenActivityTypes.get(i));
        }


        XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();

        //Create series. Start and end times are used as y intervals, and the room is represented by the x value
        XYIntervalSeries[] series = new XYIntervalSeries[activityTypeCount];
        for (int i = 0; i < activityTypeCount; i++) {
            series[i] = new XYIntervalSeries(seenActivityTypes.get(i));
            dataset.addSeries(series[i]);
        }

        List<XYTextAnnotation> annotations = new ArrayList<>();

        for (Activity act : activities) {//int k = 0; k < activities.size(); k++) {
            int currentActivity = act.getActivity();

            series[seenActivityTypes.indexOf(currentActivity)].add(seenActivityTypes.indexOf(currentActivity), seenActivityTypes.indexOf(currentActivity) - 0.2, seenActivityTypes.indexOf(currentActivity) + 0.2, act.getStartTime(), act.getStartTime(), act.getEndTime());

            if (!act.getLocation().equals("")) {
                XYTextAnnotation mark = new XYTextAnnotation(act.getLocation().substring(0, Math.min(15, act.getLocation().length())), seenActivityTypes.indexOf(currentActivity), act.getStartTime() + ((act.getEndTime() - act.getStartTime()) / 2));//seenActivityTypes.indexOf(currentActivity), activities.get(k).getStartTime()+(activities.get(k).getEndTime()/2));
                mark.setPaint(Color.black);
                annotations.add(mark);
            }

            if (act.getIntensity() > -1 && (act.getActivity() != 3 || act.getActivity() != 72 || act.getActivity() != 109 || act.getActivity() != 110 || act.getActivity() != 111)) {
                XYTextAnnotation mark = new XYTextAnnotation(String.valueOf(act.getIntensity()), seenActivityTypes.indexOf(currentActivity) - 0.25, act.getStartTime() + ((act.getEndTime() - act.getStartTime()) / 2));//seenActivityTypes.indexOf(currentActivity), activities.get(k).getStartTime()+(activities.get(k).getEndTime()/2));
                mark.setPaint(Color.black);
                annotations.add(mark);
            }


        }

        XYBarRenderer renderer = new XYBarRenderer();
        renderer.setUseYInterval(true);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardXYBarPainter());

        XYPlot plot = new XYPlot();//dataset, new SymbolAxis("Activities", tmp), new DateAxis("Time"), renderer); //new SymbolAxis("Activities", tmp)
        plot.setOrientation(PlotOrientation.HORIZONTAL);

        plot.setDataset(0, dataset);
        plot.setRenderer(renderer);
        DateAxis dateAxis = new DateAxis("Time");
        plot.setRangeAxis(dateAxis);
        plot.setDomainAxis(new SymbolAxis("Activities", tmp));
        plot.mapDatasetToRangeAxis(0, 0);

        // plot heart rate data
        List<HeartRate> heartRates = LocationHistory.getInstance().getHeartRatesForMultipleDays(start.getTimeInMillis(), end.getTimeInMillis());
        TimeSeries heartRateSeries = new TimeSeries("Heart rate");
        if (heartRates.size() != 0) {
            for (HeartRate hr : heartRates) {
                heartRateSeries.add(new Millisecond(new Date(hr.getTimestamp())), hr.getRate());
            }
            TimeSeriesCollection heartRateDataset = new TimeSeriesCollection(heartRateSeries);
            XYItemRenderer xyItemRenderer = chart.getXYPlot().getRenderer();

            plot.setDataset(1, heartRateDataset);
            plot.setRenderer(1, xyItemRenderer);
            plot.setDomainAxis(new NumberAxis("Heart rate"));
            plot.mapDatasetToRangeAxis(1, 1);
        }

        for (XYTextAnnotation an : annotations)
            plot.addAnnotation(an);

        chart = new JFreeChart(plot);
        chart.removeLegend();
    }

    public void viewPlot() {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(1000, 600));
        setContentPane(chartPanel);

        this.pack();
        RefineryUtilities.centerFrameOnScreen(this);
        this.setVisible(true);
    }

    public void export() {
        try {
            BufferedImage chartImage = chart.createBufferedImage(1000, 600, null);
            ImageIO.write(chartImage, "png", new FileOutputStream(System.getProperty("user.dir") + "/plot_" + timeframe + ".png"));
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

}
