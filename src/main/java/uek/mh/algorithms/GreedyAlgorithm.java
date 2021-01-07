package uek.mh.algorithms;

import lombok.Getter;
import uek.mh.models.City;
import uek.mh.models.Vehicle;
import uek.mh.models.VrpDataConfig;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GreedyAlgorithm {
    private final int numberOfVehicles;
    private final List<City> cities;
    private final double[][] distances;
    private final int numberOfCities;
    private final List<Vehicle> vehicles;
    private int finalNumberOfUsedVehicles;

    private double cost;

    public GreedyAlgorithm(VrpDataConfig vrpDataConfig) {
        this.numberOfCities = vrpDataConfig.getNumberOfCities();
        this.numberOfVehicles = vrpDataConfig.getVehicles();
        this.distances = vrpDataConfig.getDistance();
        this.cost = 0;

        cities = createCitiesWithDemandsFromFile(vrpDataConfig);
        vehicles = createVehiclesWithCapacitiesFromFile(vrpDataConfig);
    }

    private List<City> createCitiesWithDemandsFromFile(VrpDataConfig vrpDataConfig) {
        List<City> cities = new ArrayList<>();
        for (int i = 0; i < numberOfCities; i++) {
            cities.add(new City(i, vrpDataConfig.getDemandForCity(i)));
        }
        return cities;
    }

    private List<Vehicle> createVehiclesWithCapacitiesFromFile(VrpDataConfig vrpDataConfig) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (int i = 0; i < this.numberOfVehicles; i++) {
            vehicles.add(new Vehicle(vrpDataConfig.getVehicleCapacity()));
        }
        return vehicles;
    }

    private boolean isAnyCityUnassigned(List<City> cities) {
        for (int i = 1; i < cities.size(); i++) {
            if (!cities.get(i).isRouted)
                return true;
        }
        return false;
    }

    public void runAlgorithm() throws Exception {
        double endCost;
        double distanceBeetweenCities;
        int currentVehicle = 0;

        while (isAnyCityUnassigned(cities)) {
            int currentBestCityCandidateIndex = 0;
            City bestCityCandidate = null;
            double currentBestCost = Double.MAX_VALUE;

            for (int cityIndex = 1; cityIndex < numberOfCities; cityIndex++) {
                if (isCityRouted(cityIndex)) {
                    if (vehicles.get(currentVehicle).checkIfCapacityFits(getDemandForCityWithId(cityIndex))) {
                        distanceBeetweenCities = distances[vehicles.get(currentVehicle).currentLocation][cityIndex];
                        if (currentBestCost > distanceBeetweenCities) {
                            currentBestCost = distanceBeetweenCities;
                            currentBestCityCandidateIndex = cityIndex;
                            bestCityCandidate = cities.get(currentBestCityCandidateIndex);
                        }
                    }
                }
            }

            if (bestCityCandidate == null) {
                //Not a single Customer Fits
                if (currentVehicle + 1 < vehicles.size()) { //We have more vehicles to assign

                    if (vehicles.get(currentVehicle).currentLocation != 0) {//End this route
                        endCost = distances[vehicles.get(currentVehicle).currentLocation][0];
                        vehicles.get(currentVehicle).addStopPointToVehicle(getDepot());
                        this.cost += endCost;
                    }
                    currentVehicle = currentVehicle + 1; //Go to next Vehicle
                } else {
                    throw new Exception("Not enough vehicles to solve this problem");
                }
            } else {
                vehicles.get(currentVehicle).addStopPointToVehicle(bestCityCandidate);//If a fitting Customer is Found
                cities.get(currentBestCityCandidateIndex).isRouted = true;
                this.cost += currentBestCost;
            }
        }

        endCost = distances[vehicles.get(currentVehicle).currentLocation][0];
        vehicles.get(currentVehicle).addStopPointToVehicle(getDepot());
        this.cost += endCost;

        finalNumberOfUsedVehicles = currentVehicle;
    }

    private int getDemandForCityWithId(int i) {
        return cities.get(i).demand;
    }

    private City getDepot() {
        return cities.get(0);
    }

    private boolean isCityRouted(int i) {
        return !cities.get(i).isRouted;
    }

    public void print() {
        System.out.println("===============GREEDY ALGORITHM==============================");

        for (int vehicleIndex = 0; vehicleIndex < numberOfVehicles; vehicleIndex++) {
            if (!vehicles.get(vehicleIndex).stopPoints.isEmpty()) {
                System.out.print("Vehicle " + (vehicleIndex + 1)  + " Load for vehicle " + vehicles.get(vehicleIndex).load + ": 0->");
                int routSize = vehicles.get(vehicleIndex).stopPoints.size();
                for (int k = 0; k < routSize; k++) {
                    if (k == routSize - 1) {
                        System.out.print(vehicles.get(vehicleIndex).stopPoints.get(k).cityId);
                    } else {
                        System.out.print(vehicles.get(vehicleIndex).stopPoints.get(k).cityId + "->");
                    }
                }
                System.out.println();
            }
        }
        System.out.println("\nBest Value: " + this.cost + "\n");
    }
}


