package de.jonasrottmann.realmbrowser;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import io.realm.DynamicRealmObject;
import io.realm.RealmObject;
import io.realm.RealmObjectSchema;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;

class Utils {
    @NonNull
    static String createParametrizedName(@NonNull Field field) {
        //noinspection ConstantConditions
        if (field == null) {
            throw new IllegalArgumentException("The passed in Field cannot be null.");
        }

        ParameterizedType pType = (ParameterizedType) field.getGenericType();
        String rawType = pType.getRawType().toString();
        int rawTypeIndex = rawType.lastIndexOf(".");
        if (rawTypeIndex > 0) {
            rawType = rawType.substring(rawTypeIndex + 1);
        }

        String argument = pType.getActualTypeArguments()[0].toString();
        int argumentIndex = argument.lastIndexOf(".");
        if (argumentIndex > 0) {
            argument = argument.substring(argumentIndex + 1);
        }

        return rawType + "<" + argument + ">";
    }

    @Nullable
    static String createBlobValueString(byte[] blobValue) {
        if (blobValue == null) return null;
        StringBuilder builder = new StringBuilder("byte[] = ");
        builder.append("{");
        for (int i = 0; i < blobValue.length; i++) {
            builder.append(String.valueOf(blobValue[i]));
            if (i < blobValue.length - 1) {
                builder.append(", ");
            }
        }
        builder.append("}");
        return builder.toString();
    }

    @NonNull
    static CharSequence getFieldValueString(@NonNull DynamicRealmObject realmObject, @NonNull Field field) {
        String valueString = null;

        if (isParametrizedField(field)) {
            valueString = createParametrizedName(field);
        } else if (isBlob(field)) {
            valueString = createBlobValueString(realmObject.getBlob(field.getName()));
        } else {
            // Strings, Numbers, Objects
            Object fieldValue = realmObject.get(field.getName());
            if (fieldValue != null) {
                valueString = String.valueOf(fieldValue);
            }
        }

        if (valueString == null) {
            // Display null in italics to be able to distinguish between null and a string that actually says "null"
            valueString = "null";
            SpannableString nullString = new SpannableString(valueString);
            nullString.setSpan(new StyleSpan(Typeface.ITALIC), 0, valueString.length(), 0);
            return nullString;
        } else {
            return valueString;
        }
    }

    @Nullable
    static String getPrimaryKeyFieldName(@NonNull RealmObjectSchema schema) {
        for (String s : schema.getFieldNames()) {
            if (schema.isPrimaryKey(s)) {
                return s;
            }
        }
        return null;
    }


    static boolean isNumberField(@NonNull Field field) {
        return isLong(field) || isInteger(field) || isShort(field) || isByte(field) || isDouble(field) || isFloat(field);
    }

    static boolean isLong(@NonNull Field field) {
        return field.getType().getName().equals(Long.class.getName()) || field.getType().getName().equals("long");
    }

    static boolean isInteger(@NonNull Field field) {
        return field.getType().getName().equals(Integer.class.getName()) || field.getType().getName().equals("int");
    }

    static boolean isShort(@NonNull Field field) {
        return field.getType().getName().equals(Short.class.getName()) || field.getType().getName().equals("short");
    }

    static boolean isByte(@NonNull Field field) {
        return field.getType().getName().equals(Byte.class.getName()) || field.getType().getName().equals("byte");
    }

    static boolean isDouble(@NonNull Field field) {
        return field.getType().getName().equals(Double.class.getName()) || field.getType().getName().equals("double");
    }

    static boolean isFloat(@NonNull Field field) {
        return field.getType().getName().equals(Float.class.getName()) || field.getType().getName().equals("float");
    }

    static boolean isBoolean(@NonNull Field field) {
        return field.getType().getName().equals(Boolean.class.getName()) || field.getType().getName().equals("boolean");
    }

    static boolean isString(@NonNull Field field) {
        return field.getType().getName().equals(String.class.getName());
    }

    static boolean isParametrizedField(@NonNull Field field) {
        return field.getGenericType() instanceof ParameterizedType;
    }

    static boolean isBlob(@NonNull Field field) {
        return field.getType().getName().equals(byte[].class.getName());
    }

    static boolean isDate(@NonNull Field field) {
        return field.getType().getName().equals(Date.class.getName());
    }

    static boolean isRealmObjectField(@NonNull Field field) {
        return RealmObject.class.isAssignableFrom(field.getType());
    }
}