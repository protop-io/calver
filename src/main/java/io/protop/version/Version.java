package io.protop.version;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Version object.
 *
 * @param <T> {@link Scheme} or subclass.
 */
public final class Version<T extends Scheme> implements Comparable<Version<T>> {

    private final T scheme;
    private final List<String> segments;

    // no op
    private Version() {this(null,null);}

    /**
     * Construct an instance with valid version strings. This is private so as to enforce
     *
     * @param scheme a valid scheme.
     * @param segments string segments.
     */
    private Version(T scheme, List<String> segments) {
        this.scheme = scheme;
        this.segments = segments;
    }

    /**
     * Get the scheme for this instance.
     *
     * @return the scheme.
     */
    public Scheme getScheme() {
        return scheme;
    }

    /**
     * Retrieve the version segments.
     *
     * @return a copy of the internal list of segments.
     */
    public List<String> getSegments() {
        return new ArrayList<>(segments);
    }

    /**
     * Compares versions via segments (in order), returning the result of the first segment with a non-zero result, or
     * else zero.
     *
     * The comparison does not include modifiers (any segments <i>not</i> not indicated in the {@link Scheme}.
     */
    @Override
    public int compareTo(Version<T> other) {
        List<String> otherSegments = other.getSegments();
        for (int i = 0; i < scheme.getSegments().size(); i++) {
            int comparison = segments.get(i).compareTo(otherSegments.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    /**
     * Generates a string composed of the segments in order, joined by a <code>.</code>.
     */
    @JsonValue
    @Override
    public String toString() {
        return String.join(".", segments);
    }

    /**
     * Convenience method to parse a new instance; simply wraps <code>Parser.parse(String)</code>.
     *
     * @param scheme the intended scheme.
     * @param value raw version string.
     * @param <S> {@link Scheme} or subclass.
     * @return the parsed {@link Version} instance.
     * @throws InvalidVersionString if string is not valid.
     */
    public static <S extends Scheme> Version valueOf(S scheme, String value)
            throws InvalidVersionString {
        return new Parser<>(scheme).parse(value);
    }

    /**
     * The (currently only) mechanism for creating an instance of {@link Version}.
     *
     * @param <S> {@link Scheme} or subclass.
     */
    public static class Parser<S extends Scheme> {

        private final S scheme;

        public Parser(S scheme) {
            this.scheme = scheme;
        }

        /**
         * Validate the given version string, and, from it, construct a new {@link Version} instance.
         *
         * @param value raw version string.
         * @return a valid instance of {@link Version}
         * @throws InvalidVersionString if it is invalid/un-parsable in any way.
         */
        public Version<S> parse(final String value) throws InvalidVersionString {
            if (StringUtils.isNullOrEmpty(value)) {
                throw new InvalidVersionString("Version string is blank.");
            }

            List<Scheme.Segment> schemeSegments = scheme.getSegments();
            List<String> segments = Arrays.asList(value.trim().split("[.]"));

            int numSchemeSegments = schemeSegments.size();
            int numGivenSegments = segments.size();

            // Because modifiers are optional (in this library), allow the number of segments to vary accordingly.
            if (numGivenSegments < numSchemeSegments || numGivenSegments > numSchemeSegments + 2) {
                throw new InvalidVersionString(
                        "Value provided does not match the number of segments by the indicated schema.");
            }

            for (int i = 0; i < numSchemeSegments; i++) {
                Scheme.Segment schemeForSegment = schemeSegments.get(i);
                String given = segments.get(i);

                if (!schemeForSegment.isMatchedBy(given)) {
                    throw new InvalidVersionString(String.format(
                            "Segment provided for %s at position %s is not valid: %s.",
                            schemeForSegment, i, given));
                }
            }

            return new Version<>(scheme, segments);
        }
    }
}
