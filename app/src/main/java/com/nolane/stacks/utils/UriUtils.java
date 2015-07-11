package com.nolane.stacks.utils;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * This class contains helper function that are connected with databases
 * or providers.
 */
public class UriUtils {
    /**
     * Checks correctness of data specified for activity. If checking fails throws
     * {@link IllegalArgumentException}.
     * @param activity Activity to check against.
     * @param requiredType Required type of {@code activity.getIntent().getData()}.
     * @throws IllegalArgumentException
     */
    public static void checkDataTypeOrThrow(@NonNull Activity activity, @NonNull String requiredType) throws IllegalArgumentException {
        Uri data = activity.getIntent().getData();
        if (null == data) {
            throw new IllegalArgumentException("You must specify data to start this activity.");
        }
        String dataType = activity.getContentResolver().getType(data);
        if (!requiredType.equals(dataType)) {
            throw new IllegalArgumentException("Specified data has unknown type. Must be \"" + requiredType + "\", but \"" + dataType + "\" provided.");
        }
    }

    /**
     * Checks that activity data uri specifies parameter {@code key}. If checking fails throws
     * {@link IllegalArgumentException}.
     * @param activity Activity to check against.
     * @param key In other words parameter name.
     */
    public static void checkSpecifiesParameterOrThrow(@NonNull Activity activity, @NonNull String key) throws IllegalArgumentException {
        Uri uri = activity.getIntent().getData();
        if (null == uri.getQueryParameter(key)) {
            throw new IllegalArgumentException("Data for activity must specify parameter: " + key);
        }
    }

    /**
     * Inserts parameter to activity data uri. If such parameter already exists updates its value.
     * Otherwise appends it. So we can store loaded data in there and don't requery this information
     * time after time in new activities and fragments.
     * @param activity Activity to change data uri in.
     * @param key In other words parameter name.
     * @param value The object which string representation of will be placed as parameter value.
     */
    public static void insertParameter(@NonNull Activity activity, @NonNull String key, @NonNull Object value) {
        Uri data = activity.getIntent().getData();
        if (null == data) {
            throw new IllegalArgumentException("Activity intent don't have data.");
        }
        String stringValue = value.toString();
        String parameter = data.getQueryParameter(key);
        if (null == parameter) {
            Uri newData = data.buildUpon().appendQueryParameter(key, value.toString()).build();
            activity.getIntent().setData(newData);
            return;
        }
        if (parameter.equals(stringValue)) {
            return;
        }
        Uri newData = Uri.parse(data.getScheme() + data.getAuthority() + data.getPath());
        Uri.Builder builder = newData.buildUpon();
        for (String parameterName : data.getQueryParameterNames()) {
            builder.appendQueryParameter(parameterName, data.getQueryParameter(parameterName));
        }
        activity.getIntent().setData(newData);
    }
}
