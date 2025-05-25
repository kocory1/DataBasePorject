package admin.model;

/**
 * 테이블 컬럼 정보를 담는 DTO
 */
public class ColumnInfo {
    private String name;
    private String type;
    private boolean nullable;
    private String key;
    private String defaultValue;
    private String extra;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public boolean isNullable() { return nullable; }
    public void setNullable(boolean nullable) { this.nullable = nullable; }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }

    public boolean isPrimaryKey() { return "PRI".equals(key); }
    public boolean isUniqueKey() { return "UNI".equals(key); }
}