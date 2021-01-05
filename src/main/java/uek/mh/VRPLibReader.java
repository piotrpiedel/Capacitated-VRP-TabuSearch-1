package uek.mh;

import java.io.BufferedReader;
import java.io.IOException;

public class VRPLibReader {

    private BufferedReader reader;

    private int dimension;
    private int vehicleCapacity;
    private double[][] coord;
    private double[][] distance;
    private int[] demand;

    public VRPLibReader(BufferedReader reader) throws IOException {
        this.reader = reader;
        readHeader();
        readCoordinates();
        readDemand();
        convertCoordToDistance();
    }

    private static double euclideanDistance(double x1, double y1, double x2, double y2) {
        double xDistance = Math.abs(x1 - x2);
        double yDistance = Math.abs(y1 - y2);

        return Math.sqrt(Math.pow(xDistance, 2) + Math.pow(yDistance, 2));
    }

    private void readHeader() throws IOException {
        String line = reader.readLine();

        while (!line.equalsIgnoreCase("NODE_COORD_SECTION")) {
            String[] split = line.split(":");

            String key = split[0].trim();

            if (key.equalsIgnoreCase("DIMENSION")) {
                dimension = Integer.valueOf(split[1].trim());
            }

            if (key.equalsIgnoreCase("CAPACITY")) {
                vehicleCapacity = Integer.valueOf(split[1].trim());
            }

            line = reader.readLine();

            if (line == null) {
                break;
            }
        }
    }

    private void readCoordinates() throws IOException {
        coord = new double[dimension][2];

        String line = reader.readLine();
        while (!line.equalsIgnoreCase("DEMAND_SECTION")) {
            parseRow(line, coord);

            line = reader.readLine();
        }
    }

    private void parseRow(String line, double[][] coord) {
        String[] split = line.split("\\s+");

        int i = Integer.valueOf(split[0].trim()) - 1;
        coord[i][0] = Double.valueOf(split[1].trim());
        coord[i][1] = Double.valueOf(split[2].trim());
    }

    private void readDemand() throws IOException {
        demand = new int[dimension];

        String line = reader.readLine();
        while (!line.equalsIgnoreCase("DEPOT_SECTION")) {

            String[] split = line.split("\\s+");

            int i = Integer.valueOf(split[0].trim()) - 1;
            demand[i] = Integer.valueOf(split[1].trim());

            line = reader.readLine();
        }
    }

    private void convertCoordToDistance() {
        distance = new double[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = i; j < dimension; j++) {
                if (i != j) {
                    double x1 = coord[i][0];
                    double y1 = coord[i][1];
                    double x2 = coord[j][0];
                    double y2 = coord[j][1];

                    distance[i][j] = euclideanDistance(x1, y1, x2, y2);
                    distance[j][i] = distance[i][j];
                }
            }
        }
    }

    public int getDimension() {
        return dimension;
    }

    public double[][] getDistance() {
        return distance;
    }

    public int getVehicleCapacity() {
        return vehicleCapacity;
    }

    public int[] getDemand() {
        return demand;
    }

}
