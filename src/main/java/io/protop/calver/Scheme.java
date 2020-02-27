package io.protop.calver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Represent a calendar version scheme. A scheme comprises any number of segments which contain regular expressions that
 * should be matched by corresponding segments of a version string.
 *
 * A scheme is never parsed from a version string; it must be constructed explicitly.
 */
public final class Scheme {

    /**
     * Represents the pattern for a segment of a version.
     *
     * Default implementations are subclassed here, but are not necessarily exhaustive.
     */
    public interface Segment {

        String getPattern();

        /**
         * Validate the pattern of a given version string segment.
         *
         * @param value raw version string segment.
         * @return true if the value matches a pattern indicated for the segment.
         */
        default boolean isMatchedBy(String value) {
            return Pattern.compile(getPattern()).matcher(value).matches();
        }
    }

    /**
     * Generic implementation of a numeric {@link Segment} composed of one or more digits.
     */
    public static final Segment ANY_NUM = () -> "[0-9]+";

    /**
     * Enum implementation of {@link Segment} with variants for common major/year patterns.
     */
    public enum Major implements Segment {

        // Four-digit year.
        YYYY("[0-9]{4}"),

        // One- or two-digit year.
        YY("[0-9]{1,2}"),

        // Zero-padded two-digit year.
        OY("[0-9]{2}");

        private final String pattern;

        Major(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    /**
     * Enum implementation of {@link Segment} with variants for common minor/month patterns.
     */
    public enum Minor implements Segment {

        // One- or two-digit month.
        MM("[0-9]{1,2}"),

        // Zero-padded month.
        OM("[0-9]{2}");

        private final String pattern;

        Minor(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    /**
     * Enum implementation of {@link Segment} with variants for common micro/day patterns.
     */
    public enum Micro implements Segment {

        // One- or two-digit day.
        DD("[0-9]{1,2}"),

        // Zero-padded day.
        OD("[0-9]{1,2}");

        private final String pattern;

        Micro(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    private final List<Segment> segments;

    /**
     * Constructs an instance with the provided segments.
     *
     * @param segments ordered segments.
     */
    public Scheme(Segment... segments) {
        this.segments = Arrays.asList(segments);
    }

    /**
     * Retrieve the scheme segments.
     *
     * @return a copy of the internal list of segments.
     */
    public List<Segment> getSegments() {
        return new ArrayList<>(segments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scheme scheme = (Scheme) o;
        return segments.equals(scheme.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }
}
