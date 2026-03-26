import java.util.*;

public class ParkingLotSystem {
    enum Status { EMPTY, OCCUPIED, DELETED }

    static class Spot {
        String licensePlate;
        long entryTime;
        Status status = Status.EMPTY;

        Spot() {}
    }

    private final Spot[] spots;
    private final int capacity;
    private int occupiedCount = 0;
    private int totalProbes = 0;
    private int totalVehiclesParked = 0;
    private final double HOURLY_RATE = 5.0;

    public ParkingLotSystem(int capacity) {
        this.capacity = capacity;
        this.spots = new Spot[capacity];
        for (int i = 0; i < capacity; i++) {
            spots[i] = new Spot();
        }
    }

    // Custom hash function to map license plate to a spot
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    /**
     * Assigns a spot using Linear Probing.
     */
    public String parkVehicle(String licensePlate) {
        if (occupiedCount >= capacity) return "Error: Parking Lot Full";

        int preferredSpot = hash(licensePlate);
        int currentSpot = preferredSpot;
        int probes = 0;

        // Linear Probing: spot, spot+1, spot+2...
        while (spots[currentSpot].status == Status.OCCUPIED) {
            currentSpot = (currentSpot + 1) % capacity;
            probes++;
            totalProbes++;
        }

        spots[currentSpot].licensePlate = licensePlate;
        spots[currentSpot].entryTime = System.currentTimeMillis();
        spots[currentSpot].status = Status.OCCUPIED;
        
        occupiedCount++;
        totalVehiclesParked++;

        return String.format("Assigned spot #%d (%d probes)", currentSpot, probes);
    }

    /**
     * Frees a spot and calculates billing.
     */
    public String exitVehicle(String licensePlate) {
        int preferredSpot = hash(licensePlate);
        int currentSpot = preferredSpot;
        int checked = 0;

        while (checked < capacity) {
            if (spots[currentSpot].status == Status.EMPTY) break;
            
            if (spots[currentSpot].status == Status.OCCUPIED && 
                spots[currentSpot].licensePlate.equals(licensePlate)) {
                
                long durationMillis = System.currentTimeMillis() - spots[currentSpot].entryTime;
                double hours = Math.max(1.0, durationMillis / 3600000.0); // Min 1 hour charge
                double fee = hours * HOURLY_RATE;

                spots[currentSpot].status = Status.DELETED; // Mark as deleted for probing logic
                spots[currentSpot].licensePlate = null;
                occupiedCount--;

                return String.format("Spot #%d freed. Fee: $%.2f", currentSpot, fee);
            }
            currentSpot = (currentSpot + 1) % capacity;
            checked++;
        }
        return "Vehicle not found.";
    }

    public void getStatistics() {
        double occupancyRate = (double) occupiedCount / capacity * 100;
        double avgProbes = totalVehiclesParked == 0 ? 0 : (double) totalProbes / totalVehiclesParked;
        
        System.out.println("\n--- Parking Statistics ---");
        System.out.printf("Occupancy: %.1f%%%n", occupancyRate);
        System.out.printf("Average Probes per Entry: %.2f%n", avgProbes);
        System.out.println("--------------------------\n");
    }

    public static void main(String[] args) {
        ParkingLotSystem lot = new ParkingLotSystem(500);

        // Simulate collisions by using plates that might hash similarly
        System.out.println(lot.parkVehicle("ABC-1234"));
        System.out.println(lot.parkVehicle("ABC-1235")); 
        System.out.println(lot.parkVehicle("XYZ-9999"));

        lot.getStatistics();

        System.out.println(lot.exitVehicle("ABC-1234"));
    }
}
