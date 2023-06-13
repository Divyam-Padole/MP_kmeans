package org.example;



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// for plotting
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

public class KMeans1 {
    public static void main(String args[]) throws IOException {
        Scanner sc = new Scanner(System.in);
        String filePath = "";
        System.out.print("Enter the name of the CSV file: ");
        String fileName = sc.nextLine();

        // Open the file just to count the number of records
        int records = getRecords(filePath, fileName);

        // Open file again to read the records
        double[][] points = new double[records][];
        readRecords(filePath, fileName, points);

        // Sort the points based on X-coordinate values
        sortPointsByX(points);

        // Input the number of iterations
        System.out.print("Enter the maximum number of iterations: ");
        int maxIterations = sc.nextInt();

        // Input number of clusters
        System.out.print("Enter the number of clusters to form: ");
        int clusters = sc.nextInt();

        // Calculate initial means
        double[][] means = new double[clusters][points[0].length];
        for (int i = 0; i < means.length; i++) {
            means[i] = points[(int) (Math.floor((records * 1.0 / clusters) / 2) + i * records / clusters)].clone();
        }

        // Create skeletons for clusters
        ArrayList<Integer>[] oldClusters = new ArrayList[clusters];
        ArrayList<Integer>[] newClusters = new ArrayList[clusters];

        for (int i = 0; i < clusters; i++) {
            oldClusters[i] = new ArrayList<>();
            newClusters[i] = new ArrayList<>();
        }

        // Make the initial clusters
        formClusters(oldClusters, means, points);
        int iterations = 0;

        // Showtime
        while (true) {
            updateMeans(oldClusters, means, points);
            formClusters(newClusters, means, points);

            iterations++;

            if (iterations > maxIterations || checkEquality(oldClusters, newClusters))
                break;
            else
                resetClusters(oldClusters, newClusters);
        }

        // Display the output
        System.out.println("\nThe final clusters are:");
        displayOutput(oldClusters, points);

        // Create a dataset for the scatter plot
        XYDataset dataset = createDataset(oldClusters, points);

        // Create the scatter plot chart
        JFreeChart chart = ChartFactory.createScatterPlot(
                "K-means Clustering", // Chart title
                "X", // X-axis label
                "Y", // Y-axis label
                dataset, // Dataset
                PlotOrientation.VERTICAL,
                true, // Include legend
                true, // Include tooltips
                false // Include URLs
        );

        // Customize the plot's appearance
        XYPlot plot = (XYPlot) chart.getPlot();
        XYItemRenderer renderer = plot.getRenderer();
        renderer.setSeriesPaint(0, java.awt.Color.BLUE);
        renderer.setSeriesPaint(1, java.awt.Color.RED);
        renderer.setSeriesPaint(2, java.awt.Color.GREEN);

        // Display the scatter plot
        ChartFrame frame = new ChartFrame("K-means Clustering", chart);
        frame.pack();
        frame.setVisible(true);
    }

    public static int getRecords(String filePath, String fileName) throws IOException {
        int records = 0;
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileName));
        while (br.readLine() != null)
            records++;
        br.close();
        return records;
    }

    public static void readRecords(String filePath, String fileName, double[][] points) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath + fileName));
      //  BufferedReader br = new BufferedReader(new CsvFileSelector());
        String line;
        int index = 0;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            points[index] = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                points[index][i] = Double.parseDouble(values[i]);
            }
            index++;
        }
        br.close();
    }

    public static void sortPointsByX(double[][] points) {
        // Bubble sort based on X-coordinate
        int n = points.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (points[j][0] > points[j + 1][0]) {
                    // Swap points[j] and points[j+1]
                    double[] temp = points[j];
                    points[j] = points[j + 1];
                    points[j + 1] = temp;
                }
            }
        }
    }

    public static double calculateDistance(double[] point1, double[] point2) {
        double sum = 0;
        for (int i = 0; i < point1.length; i++) {
            sum += Math.pow((point1[i] - point2[i]), 2);
        }
        return Math.sqrt(sum);
    }

    public static void formClusters(ArrayList<Integer>[] clusters, double[][] means, double[][] points) {
        for (int i = 0; i < points.length; i++) {
            int clusterIndex = 0;
            double minDistance = Double.MAX_VALUE;

            for (int j = 0; j < means.length; j++) {
                double distance = calculateDistance(points[i], means[j]);
                if (distance < minDistance) {
                    minDistance = distance;
                    clusterIndex = j;
                }
            }

            clusters[clusterIndex].add(i);
        }
    }

    public static void updateMeans(ArrayList<Integer>[] clusters, double[][] means, double[][] points) {
        for (int i = 0; i < clusters.length; i++) {
            double[] sum = new double[points[0].length];

            for (int j = 0; j < clusters[i].size(); j++) {
                int pointIndex = clusters[i].get(j);

                for (int k = 0; k < points[pointIndex].length; k++) {
                    sum[k] += points[pointIndex][k];
                }
            }

            for (int k = 0; k < sum.length; k++) {
                means[i][k] = sum[k] / clusters[i].size();
            }
        }
    }

    public static boolean checkEquality(ArrayList<Integer>[] oldClusters, ArrayList<Integer>[] newClusters) {
        for (int i = 0; i < oldClusters.length; i++) {
            if (!oldClusters[i].equals(newClusters[i]))
                return false;
        }
        return true;
    }

    public static void resetClusters(ArrayList<Integer>[] oldClusters, ArrayList<Integer>[] newClusters) {
        for (int i = 0; i < oldClusters.length; i++) {
            oldClusters[i].clear();
            oldClusters[i].addAll(newClusters[i]);
            newClusters[i].clear();
        }
    }

    public static void displayOutput(ArrayList<Integer>[] clusters, double[][] points) {
        for (int i = 0; i < clusters.length; i++) {
            System.out.print("Cluster " + (i + 1) + ": ");
            for (int j = 0; j < clusters[i].size(); j++) {
                int pointIndex = clusters[i].get(j);
                System.out.print("(");
                for (int k = 0; k < points[pointIndex].length; k++) {
                    System.out.print(points[pointIndex][k]);
                    if (k != points[pointIndex].length - 1)
                        System.out.print(", ");
                }
                System.out.print(")");
                if (j != clusters[i].size() - 1)
                    System.out.print(", ");
            }
            System.out.println();
        }
    }

    public static XYDataset createDataset(ArrayList<Integer>[] clusters, double[][] points) {
        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][][] data = new double[clusters.length][][];

        for (int i = 0; i < clusters.length; i++) {
            data[i] = new double[2][clusters[i].size()];
            for (int j = 0; j < clusters[i].size(); j++) {
                int pointIndex = clusters[i].get(j);
                data[i][0][j] = points[pointIndex][0];
                data[i][1][j] = points[pointIndex][1];
            }
        }

        for (int i = 0; i < clusters.length; i++) {
            dataset.addSeries("Cluster " + (i + 1), data[i]);
        }

        return dataset;
    }
}
