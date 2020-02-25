package io.protop.calver;

import io.protop.calver.scheme.Scheme;
import io.protop.calver.utils.StringUtils;
import java.util.Arrays;
import java.util.List;

/**
 * A calendar version library based on the <a href="https://calver.org">specs</a>.
 *
 * A generic is provided for a {@Link Scheme} type for convenience, but in most cases probably not necessary.
 */
public class CalVer<T extends Scheme> implements Comparable<CalVer<T>> {

    private final T scheme;
    private final List<String> segments;

    /**
     * Construct an instance with valid version strings.
     * @param scheme
     * @param segments
     */
    private CalVer(T scheme, List<String> segments) {
        this.scheme = scheme;
        this.segments = segments;
    }

    public Scheme getScheme() {
        return scheme;
    }

    public List<String> getSegments() {
        return segments;
    }

    @Override
    public int compareTo(CalVer<T> other) {
        List<String> otherSegments = other.getSegments();
        for (int i = 0; i < scheme.getSegments().size(); i++) {
            int comparison = segments.get(i).compareTo(otherSegments.get(i));
            if (comparison != 0) {
                return comparison;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.join(".", segments);
    }

    public static <S extends Scheme> CalVer valueOf(S scheme, String value)
            throws InvalidVersionString {
        return new Parser<>(scheme).parse(value);
    }

    public static class Parser<S extends Scheme> {

        private final S scheme;

        public Parser(S scheme) {
            this.scheme = scheme;
        }

        /**
         * Validate the given version string, and, from it, construct a new {@link CalVer<S>} instance.
         * @param value raw version string.
         * @return a valid instance of {@link CalVer<S>}
         * @throws InvalidVersionString if it is invalid/un-parsable in any way.
         */
        public CalVer<S> parse(final String value) throws InvalidVersionString {
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

            return new CalVer<>(scheme, segments);
        }
    }
}
