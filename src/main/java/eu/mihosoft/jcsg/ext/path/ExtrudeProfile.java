/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.jcsg.ext.path;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Extrude;
import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.vvecmath.ModifiableVector3d;
import eu.mihosoft.vvecmath.Plane;
import eu.mihosoft.vvecmath.Transform;
import eu.mihosoft.vvecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Extrudes profiles along paths.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class ExtrudeProfile {

    private ExtrudeProfile() {
        throw new AssertionError("Dont instantiate me!");
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param path path
     * @return CSG object (extruded profile)
     */
    public static CSG alongPath(PathProfile profile, List<Vector3d> path) {
        List<Segment> segments = path.stream().
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, segments);
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param path path
     * @return CSG object (extruded profile)
     */
    public static CSG alongPath(PathProfile profile, Vector3d... path) {
        List<Segment> segments = Stream.of(path).
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, segments);
    }

    /**
     * Extrudes the specified segments.
     *
     * @param segments segments to extrude
     * @return CSG object
     */
    private static CSG extrudeSegments(PathProfile profile,
            List<Segment> segments) {

        computeSegmentNormals(segments);

        List<Vector3d> profilePoints = new ArrayList<>(profile.getPoints());

        // transform profile points to path direction
        
        Vector3d profileNormal
                = Polygon.fromPoints(profilePoints).plane.getNormal();

        if (!profileNormal.equals(segments.get(0).normal)) {
            Transform rot = Transform.unity().rot(profileNormal,
                    segments.get(0).normal);
            for (int i = 0; i < profilePoints.size(); i++) {
                profilePoints.set(i, 
                        profilePoints.get(i).transformed(rot));
            }
        }

        // translate profile to first path segment location
        Vector3d offset = segments.get(0).pos.minus(profile.getCenter());
        
        Transform translate = Transform.unity().
                translate(profile.getCenter().plus(offset));
        
        for (int i = 0; i < profilePoints.size(); i++) {
            profilePoints.set(i, 
                    profilePoints.get(i).transformed(translate));
        }

        List<Polygon> polygons = new ArrayList<>();

        for (int i = 0; i < segments.size(); i++) {
            Segment s = segments.get(i);

            // skip first segment 
            if (i < 1) {
                continue;
            }

            // Compute transformation along path without annoying twisting.
            //
            // Note: there are several solutions out there for preventing
            //       the twisting. To me, the most pragmatic one seems to be
            //       https://www.cs.indiana.edu/pub/techreports/TR425.pdf
            Vector3d curveTangent0 = segments.get(i - 1).getNormal();
            Vector3d curveTangent1 = s.getNormal();
            Vector3d curveBiNormal = curveTangent1.crossed(curveTangent0);

            // angle of curvature (our rotation angle)
            double angle = curveTangent0.angle(curveTangent1);

            // rotate circle points
            Transform rot;

            if (Double.compare(angle, 0) != 0) {
                // angle is reasonably large. we apply the rotation transform
                rot = Transform.unity().
                        rot(s.getPos(), curveBiNormal, -angle);
            } else {
                // we don't apply the rotation transform since the angle is
                // very small and rounding errors are larger than not applying
                // the rotation transform
                rot = Transform.unity();
            }

            // translate profile points to new curve point
            // rotate profile points about binormal with curve angle 
            List<Vector3d> profilePointsTransformed
                    = new ArrayList<>(profilePoints.size());

            // finally apply the transforms
            for (Vector3d p : profilePoints) {
                // move points points from previous path point to current one
                p = p.plus(segments.get(i).getPos().
                        minus(segments.get(i - 1).getPos()));
                // apply rotation transform
                p = p.transformed(rot);

                // add transformed point to list
                profilePointsTransformed.add(p);
            }

            // combine both profile profiles and close start and end to 
            // yield a valid CSG object
            CSG csg = CSG.fromPolygons(Extrude.combine(
                    Polygon.fromPoints(profilePoints),
                    Polygon.fromPoints(profilePointsTransformed),
                    i == 1, i == segments.size() - 1));

            polygons.addAll(csg.getPolygons());

            // use the current profile points as start points for the next
            // segment
            profilePoints = profilePointsTransformed;
        }

        // finally, create the CSG
        CSG pathCSG = CSG.fromPolygons(polygons);

        return pathCSG;
    }

    /**
     * Computes and sets the normals of the specified path segments. Segments
     * are assumed to form a non-closed path.
     *
     * @param segments path segments
     */
    private static void computeSegmentNormals(List<Segment> segments) {
        Segment prevSegment = null;
        for (Segment s : segments) {
            if (prevSegment != null) {
                Vector3d normal = s.getPos().
                        minus(prevSegment.getPos()).
                        normalized();
                prevSegment.setNormal(normal);

                // needs to be set for last element
                // since paths are not closed, we set normal to normal
                // of last segment
                s.setNormal(normal);
            }
            prevSegment = s;
        }
    }

    private static class Segment {

        private final Vector3d pos;
        private PathProfile profile;
        private Vector3d normal;

        public Segment(Vector3d pos, PathProfile profile) {
            this.pos = pos;
            this.profile = profile;
        }

        public Segment(Segment other) {
            this.pos = other.pos;
            this.profile = new PathProfile(other.profile);
            this.normal = other.normal;
        }

        public Vector3d getPos() {
            return pos;
        }

        public PathProfile getProfile() {
            return this.profile;
        }

        public List<Vector3d> getPoints() {
            return getProfile().getPoints().stream().map(p -> p.transformed(Transform.unity().
                    translate(getPos().minus(getProfile().getCenter())))).collect(Collectors.toList());
        }

        public Vector3d getNormal() {
            return normal;
        }

        public void setNormal(Vector3d normal) {
            this.normal = normal;
        }

    }
}
