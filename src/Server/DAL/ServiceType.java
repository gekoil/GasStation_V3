package DAL;

public enum ServiceType {
    FUEL, CLEANING;

    public static ServiceType fromInt(int i) {
        switch (i) {
            case 0:
                return FUEL;
            case 1:
                return CLEANING;
        }
        return null;
    }
}