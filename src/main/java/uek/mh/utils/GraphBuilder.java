package uek.mh.utils;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import uek.mh.models.City;
import uek.mh.models.Vehicle;

import java.util.List;

public class GraphBuilder {
    private final int numberOfVehicles;
    private final List<Vehicle> vehicles;
    private final List<City> cities;

    public GraphBuilder(int numberOfVehicles, List<Vehicle> vehicles, List<City> cities) {
        this.numberOfVehicles = numberOfVehicles;
        this.vehicles = vehicles;
        this.cities = cities;
    }

    public Graph buildGraph() {
        Graph graph = new SingleGraph("CVRP visualization");
        for (City city : cities) {

            Node node = graph.addNode(String.valueOf(city.getName()));
            node.setAttribute("xy", city.getCoordinates().getLongitude(), city.getCoordinates().getLatitude());
            node.setAttribute("ui.label",  city.getName());

        }
        for (int vehicleIndex = 0; vehicleIndex < numberOfVehicles; vehicleIndex++) {
            if (!vehicles.get(vehicleIndex).stopPoints.isEmpty()) {
                int routSize = vehicles.get(vehicleIndex).stopPoints.size();
                for (int k = 0; k < routSize - 1; k++) {
                    graph.addEdge(vehicles.get(vehicleIndex).stopPoints.get(k).getName() + vehicles.get(vehicleIndex).stopPoints.get(k + 1).getName(),
                            String.valueOf(vehicles.get(vehicleIndex).stopPoints.get(k).getName()), String.valueOf(vehicles.get(vehicleIndex).stopPoints.get(k + 1).getName()));

                }
            }
        }
        return graph;
    }
}
