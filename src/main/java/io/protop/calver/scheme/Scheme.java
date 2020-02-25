package io.protop.calver.scheme;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Scheme {

    /**
     * Represents the pattern for a segment of a version.
     *
     * Default implementations are subclassed here, but are not necessarily exhaustive.
     */
    public interface Segment {

        String getPattern();

        /**
         * Validate the pattern of a given version string segment.
         * @param value raw version string segment.
         * @return true if the value matches a pattern indicated for the segment.
         */
        default boolean isMatchedBy(String value) {
            return Pattern.compile(getPattern()).matcher(value).matches();
        }
    }

    public enum Major implements Segment {

        // Four-digit year.
        YYYY("[0-9]{4}"),

        // One- or two-digit year.
        YY("[0-9]{1,2}"),

        // Zero-padded two-digit year.
        OY("[0-9]{2}"),

        // Any number.
        MAJOR("[0-9]+");

        private final String pattern;

        Major(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    public enum Minor implements Segment {

        // One- or two-digit month.
        MM("[0-9]{1,2}"),

        // Zero-padded month.
        OM("[0-9]{2}"),

        // Any number.
        MINOR("[0-9]+");

        private final String pattern;

        Minor(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public String getPattern() {
            return pattern;
        }
    }

    public enum Micro implements Segment {

        // One- or two-digit day.
        DD("[0-9]{1,2}"),

        // Zero-padded day.
        OD("[0-9]{1,2}"),

        // Any number.
        MICRO("[0-9]+");

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

    public Scheme(Segment... segments) {
        this.segments = Arrays.asList(segments);
    }

    public List<Segment> getSegments() {
        return this.segments;
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
