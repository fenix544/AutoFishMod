package me.fenix.module.settings;

public class Setting<T> {

    private final Class<T> type;
    private final String name;
    private final boolean save;
    private final float min;
    private final float max;
    private final String formatMessage;
    private T value;

    public Setting(Class<T> type, T value, String name) {
        this(type, value, name, false, 0f, 0f);
    }

    /**
     * @param formatMessage message to display, <p>%value% displaying value of setting,</p> <p>%name% displaying name of setting</p>
     */
    public Setting(Class<T> type, T value, String name, boolean save, String formatMessage) {
        this(type, value, name, save, 1f, 1000f, formatMessage);
    }

    public Setting(Class<T> type, T value, String name, boolean save, float min, float max) {
        this(type, value, name, save, min, max, null);
    }

    /**
     * @param formatMessage message to display, <p>%value% displaying value of setting,</p> <p>%name% displaying name of setting</p>
     */
    public Setting(Class<T> type, T value, String name, boolean save, float min, float max, String formatMessage) {
        this.type = type;
        this.value = value;
        this.name = name;
        this.save = save;
        this.min = min;
        this.max = max;
        this.formatMessage = formatMessage;
    }

    public Class<T> getType() {
        return type;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public boolean isSave() {
        return save;
    }

    public float getMin() {
        return min;
    }

    public float getMax() {
        return max;
    }

    public String getFormatMessage() {
        return formatMessage;
    }
}
