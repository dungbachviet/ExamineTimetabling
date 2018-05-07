/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui_timetabling;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import util.DataIO;
import util.UtilManager;
import static util.UtilManager.randomDouble;

/**
 *
 * @author quancq
 */
public class MultipleLinesChart extends JFrame { // the class extends the JFrame class

    private String applicationTitle;
    private String chartTitle;
    private String xAxisLabel;
    private String yAxisLabel;
    private ArrayList<Line> lineList;

    public MultipleLinesChart(String applicationTitle) {   // the constructor will contain the panel of a certain size and the close operations 
        super(applicationTitle); // calls the super class constructor
        this.applicationTitle = applicationTitle;

//        JPanel chartPanel = createChartPanel();
//        add(chartPanel, BorderLayout.CENTER);
//
//        setSize(640, 480);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
    }

    public String getApplicationTitle() {
        return applicationTitle;
    }

    public String getChartTitle() {
        return chartTitle;
    }

    public ArrayList<Line> getLineList() {
        return lineList;
    }

    public String getxAxisLabel() {
        return xAxisLabel;
    }

    public String getyAxisLabel() {
        return yAxisLabel;
    }

    public void setApplicationTitle(String applicationTitle) {
        this.applicationTitle = applicationTitle;
        setTitle(applicationTitle);
    }

    public void setChartTitle(String chartTitle) {
        this.chartTitle = chartTitle;
    }

    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }

    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }

    public void setLineList(ArrayList<Line> lineList) {
        this.lineList = lineList;
    }

    public void renderGraph(boolean isShowGui, String pathSaveImage) {

//        String chartTitle = this.chartTitle;
//        String xAxisLabel = this.xAxisLabel;
//        String yAxisLabel = this.yAxisLabel;
        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle, xAxisLabel, yAxisLabel,
                dataset, PlotOrientation.VERTICAL,
                true, true, false);

        // saves the chart as an image files
        File imageFile = new File(pathSaveImage);
        int width = 640;
        int height = 480;

        customizeChart(chart);
        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
            System.out.println("Save image to " + imageFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isShowGui) {
            showGui(chart);
        }

    }

    public void showGui(JFreeChart chart) {

        customizeChart(chart);
        JPanel chartPanel = new ChartPanel(chart);
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }


    private XYDataset createDataset() {    // this method creates the data as time seris 
        XYSeriesCollection dataset = new XYSeriesCollection();

        ArrayList<XYSeries> xySeriesList = new ArrayList<>();
        for (Line line : lineList) {
            XYSeries series = new XYSeries(line.getLineName());

            ArrayList<Double> xList = line.getxList();
            ArrayList<Double> yList = line.getyList();

            for (int i = 0; i < xList.size(); ++i) {
                series.add(xList.get(i), yList.get(i));
            }

            dataset.addSeries(series);
        }

        return dataset;
    }

    private void customizeChart(JFreeChart chart) {   // here we make some customization
        XYPlot plot = chart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.GREEN);
        renderer.setSeriesPaint(1, Color.BLUE);
        renderer.setSeriesPaint(2, Color.YELLOW);
        renderer.setSeriesPaint(3, Color.ORANGE);
        renderer.setSeriesPaint(4, Color.PINK);
        renderer.setSeriesPaint(5, Color.RED);
        renderer.setSeriesPaint(6, Color.MAGENTA);
        renderer.setSeriesPaint(7, Color.CYAN);

        // sets thickness for series (using strokes)
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        renderer.setSeriesStroke(1, new BasicStroke(3.0f));
        renderer.setSeriesStroke(2, new BasicStroke(2.0f));

        // sets paint color for plot outlines
        plot.setOutlinePaint(Color.BLUE);
        plot.setOutlineStroke(new BasicStroke(2.0f));

        // sets renderer for lines
        plot.setRenderer(renderer);

        // sets plot background
        plot.setBackgroundPaint(Color.DARK_GRAY);

        // sets paint color for the grid lines
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

    }

    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new MultipleLinesChart("Demo").setVisible(true);
//            }
//        });

        MultipleLinesChart mlc = new MultipleLinesChart("Demo");
        mlc.setxAxisLabel("Loop");
        mlc.setyAxisLabel("Fitness");
        mlc.setChartTitle("Tabu algorithm");

        ArrayList<Line> lineList = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            ArrayList<Double> xList = new ArrayList<>();
            ArrayList<Double> yList = new ArrayList<>();

            for (int j = 0; j < 20; ++j) {
                xList.add(randomDouble(0, 100));
                yList.add(randomDouble(0, 100));
            }

            Line line = new Line("Parameter" + (i + 1), xList, yList);
            lineList.add(line);
        }

        mlc.setLineList(lineList);

        String pathSaveImage = "src/statistics/test_1.png";
        mlc.renderGraph(false, pathSaveImage);
    }
}
