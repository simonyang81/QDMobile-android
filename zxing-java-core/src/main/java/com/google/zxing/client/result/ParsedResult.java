package com.google.zxing.client.result;

public abstract class ParsedResult {

    private final ParsedResultType type;

    protected ParsedResult(ParsedResultType type) {
        this.type = type;
    }

    public final ParsedResultType getType() {
        return type;
    }

    public abstract String getDisplayResult();

    @Override
    public final String toString() {
        return getDisplayResult();
    }

    public static void maybeAppend(String value, StringBuilder result) {
        if (value != null && !value.isEmpty()) {
            // Don't add a newline before the first value
            if (result.length() > 0) {
                result.append('\n');
            }
            result.append(value);
        }
    }

    public static void maybeAppend(String[] values, StringBuilder result) {
        if (values != null) {
            for (String value : values) {
                maybeAppend(value, result);
            }
        }
    }

}
