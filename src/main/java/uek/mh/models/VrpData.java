package uek.mh.models;

import lombok.Data;

@Data
public class VrpData {
    public int vehicles;
    public int iterations = 10000;
    public int tabuHorizonSize = 10;

    public int dimension;
    public int vehicleCapacity;
    public double[][] coordinates;
    public double[][] distance;
    public int[] demand;


}
