package de.opendiabetes.vault.plugin.importer.medtroniccrawler;

/**
 * Model to hold the configuration for the medtroniccrawler.
 */
public class Configuration {

    /**
     * Username of the user to log in.
     */
    private String username;

    /**
     * Password of the user to log in.
     */
    private String password;

    /**
     * Device specification.
     */
    private String device;

    /**
     * Pump specification.
     */
    private String pump;

    /**
     * Serial number of the device.
     */
    private String serialNumber;

    /**
     * Path to the extracted csv file.
     */
    private String csvPath;

    /**
     * Constructor.
     *
     * @param username - username of the user to log in
     * @param password - password of the user to log in
     * @param device - device specification
     * @param pump - pump specification
     * @param serialNumber - serial number of the device
     * @param csvPath - path to the extracted csv file
     */
    public Configuration(final String username,
                         final String password,
                         final String device,
                         final String pump,
                         final String serialNumber,
                         final String csvPath) {
        this.username = username;
        this.password = password;
        this.device = device;
        this.pump = pump;
        this.serialNumber = serialNumber;
        this.csvPath = csvPath;
    }

    /**
     * @return Username of the user to login
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return Password of the user to login
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return Device specification
     */
    public String getDevice() {
        return device;
    }

    /**
     * @return Pump specification
     */
    public String getPump() {
        return pump;
    }

    /**
     * @return Serial number of the device
     */
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * @return Path to the extracted csv file
     */
    public String getCsvPath() {
        return csvPath;
    }
}