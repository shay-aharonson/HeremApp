package com.heremapp.utility.rx;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A wrapper around nullable objects to be used in RxJava 2.x.
 * This can be used as a safe wrapper for streams that could benefit a null value.
 */
public final class Optional<T> {

    @Nullable
    private final T value;

    public Optional(@Nullable T value) {
        this.value = value;
    }

    /**
     * @return The value if present. Throws a [NoSuchElementException] if null
     */
    @NonNull
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("Optional is null");
        }
        return value;
    }

    /**
     * @return {@code true} if the underlying value is null
     */
    public boolean isNull() {
        return value == null;
    }

    /**
     * @return The value even if it is null. Use with caution.
     */
    @Nullable
    public T getValueOrNull() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Optional)) {
            return false;
        }

        Optional<?> other = (Optional<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}