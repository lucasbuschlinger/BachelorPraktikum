package de.opendiabetes.vault.plugin.importer.googlecrawler.models;

/**
 * ActivityTypes model.
 */
public final class ActivityTypes {

    /**
     * Singleton instance.
     */
    private static ActivityTypes instance;

    /**
     * Constructor.
     */
    private ActivityTypes() {
    }

    /**
     * Returns the singleton instance.
     * @return singleton instance of the model
     */
    public static ActivityTypes getInstance() {
        if (ActivityTypes.instance == null) {
            ActivityTypes.instance = new ActivityTypes();
        }
        return ActivityTypes.instance;
    }

    /**
     * Returns the human readable representation of the given activity identifier index.
     * @param activity an activity identifier index
     * @return the humand readable string of the activity type, otherwise "Unknown"
     */
    public String getReadableActivityType(final int activity) {
        switch (activity) {
            case 0:
                return "In vehicle";
            case 1:
                return "Biking";
            case 2:
                return "On foot";
            case 3:
                return "Still";
            case 4:
                return "Unknown";
            case 5:
                return "Tilting";
            case 7:
                return "Walking";
            case 8:
                return "Running";
            case 9:
                return "Aerobics";
            case 10:
                return "Badminton";
            case 11:
                return "Baseball";
            case 12:
                return "Basketball";
            case 13:
                return "Biathlon";
            case 14:
                return "Handbiking";
            case 15:
                return "Mountainbiking";
            case 16:
                return "Road biking";
            case 17:
                return "Spinning";
            case 18:
                return "Stationary biking";
            case 19:
                return "Utility biking";
            case 20:
                return "Boxing";
            case 21:
                return "Calisthenics";
            case 22:
                return "Circuit training";
            case 23:
                return "Cricket";
            case 24:
                return "Dancing";
            case 25:
                return "Elliptical";
            case 26:
                return "Fencing";
            case 27:
                return "American Football";
            case 28:
                return "Australian Football";
            case 29:
                return "Football (Soccer)";
            case 30:
                return "Frisbee";
            case 31:
                return "Gardening";
            case 32:
                return "Golf";
            case 33:
                return "Gymnastics";
            case 34:
                return "Handball";
            case 35:
                return "Hiking";
            case 36:
                return "Hockey";
            case 37:
                return "Horseback riding";
            case 38:
                return "Housework";
            case 39:
                return "Jumping rope";
            case 40:
                return "Kayaking";
            case 41:
                return "Kettlebell training";
            case 42:
                return "Kickboxing";
            case 43:
                return "Kitesurfing";
            case 44:
                return "Martial arts";
            case 45:
                return "Meditation";
            case 46:
                return "Mixed martial arts";
            case 47:
                return "P90X exercises";
            case 48:
                return "Paragliding";
            case 49:
                return "Pilates";
            case 50:
                return "Polo";
            case 51:
                return "Racquetball";
            case 52:
                return "Rock climbing";
            case 53:
                return "Rowing";
            case 54:
                return "Rowing machine";
            case 55:
                return "Rugby";
            case 56:
                return "Jogging";
            case 57:
                return "Running on sand";
            case 58:
                return "Running (treadmill)";
            case 59:
                return "Sailing";
            case 60:
                return "Scuba diving";
            case 61:
                return "Skateboarding";
            case 62:
                return "Skating";
            case 63:
                return "Cross skating";
            case 64:
                return "Inline skating (rollerblading)";
            case 65:
                return "Skiing";
            case 66:
                return "Back-country skiing";
            case 67:
                return "Cross-country skiing";
            case 68:
                return "Downhill skiing";
            case 69:
                return "Kite skiing";
            case 70:
                return "Roller skiing";
            case 71:
                return "Sledding";
            case 72:
                return "Sleeping";
            case 73:
                return "Snowboarding";
            case 74:
                return "Snowmobile";
            case 75:
                return "Snowshoeing";
            case 76:
                return "Squash";
            case 77:
                return "Stair climbing";
            case 78:
                return "Stair-climbing machine";
            case 79:
                return "Stand-up paddleboarding";
            case 80:
                return "Strength training";
            case 81:
                return "Surfing";
            case 82:
                return "Swimming";
            case 83:
                return "Swimming (swimming pool)";
            case 84:
                return "Swimming (open water)";
            case 85:
                return "Table tennis (ping pong)";
            case 86:
                return "Team sports";
            case 87:
                return "Tennis";
            case 88:
                return "Treadmill (walking or running)";
            case 89:
                return "Volleyball";
            case 90:
                return "Volleyball (beach)";
            case 91:
                return "Volleyball (indoor)";
            case 92:
                return "Wakeboarding";
            case 93:
                return "Walking (fitness)";
            case 94:
                return "Nording walking";
            case 95:
                return "Walking (treadmill)";
            case 96:
                return "Waterpolo";
            case 97:
                return "Weightlifting";
            case 98:
                return "Wheelchair";
            case 99:
                return "Windsurfing";
            case 100:
                return "Yoga";
            case 101:
                return "Zumba";
            case 102:
                return "Diving";
            case 103:
                return "Ergometer";
            case 104:
                return "Ice skating";
            case 105:
                return "Indoor skating";
            case 106:
                return "Curling";
            case 108:
                return "Other (unclassified fitness activity)";
            case 109:
                return "Light sleep";
            case 110:
                return "Deep sleep";
            case 111:
                return "REM sleep";
            case 112:
                return "Awake (during sleep cycle)";
            case 113:
                return "Crossfit";
            case 114:
                return "HIIT";
            case 115:
                return "Interval Training";
            case 116:
                return "Walking (stroller)";
            case 117:
                return "Elevator";
            case 118:
                return "Escalator";
            case 119:
                return "Archery";
            case 120:
                return "Softball";
            default:
                return "Unknown";
        }
    }
}

