package uek.mh;

import uek.mh.algorithms.HaversineDistanceCalculator;
import uek.mh.models.VrpData;
import uek.mh.utils.FileFromResourcesReader;

import java.io.*;

public class DataFromFileConverterToInitialVrpData {

    private final BufferedReader reader;
    private final VrpData vrpData;
    private final HaversineDistanceCalculator haversineDistanceCalculator;

    /**
     * @param pathToFileFromResources the location of the file, relative resource folder
     *                                eg. datasets/big/Golden_20.vrp
     */
    public static VrpData convert(String pathToFileFromResources) throws IOException {
        DataFromFileConverterToInitialVrpData dataFromFileConverterToInitialVrpData = new DataFromFileConverterToInitialVrpData(pathToFileFromResources);
        return dataFromFileConverterToInitialVrpData.loadFileToVrpData();
    }

    private DataFromFileConverterToInitialVrpData(String pathToFileFromResources) throws FileNotFoundException {
        FileFromResourcesReader fileFromResourcesReader = new FileFromResourcesReader();
        File file = fileFromResourcesReader.loadFile(pathToFileFromResources);
        reader = new BufferedReader(new FileReader(file));
        vrpData = new VrpData();
        haversineDistanceCalculator = new HaversineDistanceCalculator();
    }

    private VrpData loadFileToVrpData() throws IOException {
        readHeader();
        readCoordinates();
        readDemand();
        convertCoordinatesToDistance();
        return vrpData;
    }

    private void readHeader() throws IOException {
        String line;
        while (!((line = reader.readLine()).equalsIgnoreCase("COORDINATES"))) {
            String[] split = line.split(":");

            String key = split[0].trim();

            if (key.equalsIgnoreCase("DIMENSION")) {
                vrpData.dimension = Integer.parseInt(split[1].trim());
            }

            if (key.equalsIgnoreCase("VEHICLE_CAPACITY")) {
                vrpData.vehicleCapacity = Integer.parseInt(split[1].trim());
            }

            if (key.equalsIgnoreCase("NUMBER_OF_VEHICLES")) {
                vrpData.vehicles = Integer.parseInt(split[1].trim());
            }
        }
    }

    private void readCoordinates() throws IOException {
        vrpData.coordinates = new double[vrpData.dimension][2];
        String line;
        while (!((line = reader.readLine()).equalsIgnoreCase("CITY_DEMAND"))) {
            parseRow(line, vrpData.coordinates);
        }
    }

    private void parseRow(String line, double[][] coordinates) {
        String[] split = line.split("\\s+");

        int i = Integer.valueOf(split[0].trim()) - 1;
        coordinates[i][0] = Double.valueOf(split[1].trim());
        coordinates[i][1] = Double.valueOf(split[2].trim());
    }

    private void readDemand() throws IOException {
        vrpData.demand = new int[vrpData.dimension];
        String line;
        while (!((line = reader.readLine()).equalsIgnoreCase("DEPOT_COORDINATES"))) {
            String[] split = line.split("\\s+");
            int i = Integer.valueOf(split[0].trim()) - 1;
            vrpData.demand[i] = Integer.valueOf(split[1].trim());
        }
    }

    private void convertCoordinatesToDistance() {
        vrpData.distance = new double[vrpData.dimension][vrpData.dimension];

        for (int i = 0; i < vrpData.dimension; i++) {
            for (int j = i; j < vrpData.dimension; j++) {
                if (i != j) {
                    double x1 = vrpData.coordinates[i][0];
                    double y1 = vrpData.coordinates[i][1];
                    double x2 = vrpData.coordinates[j][0];
                    double y2 = vrpData.coordinates[j][1];

                    vrpData.distance[i][j] = haversineDistanceCalculator.calculateDistance(x1, y1, x2, y2);
                    vrpData.distance[j][i] = vrpData.distance[i][j];
                }
            }
        }
    }
}
