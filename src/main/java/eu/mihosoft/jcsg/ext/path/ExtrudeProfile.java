/*
 * Copyright 2017 Michael Hoffer <info@michaelhoffer.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * If you use this software for scientific research then please cite the following publication(s):
 *
 * M. Hoffer, C. Poliwoda, & G. Wittum. (2013). Visual reflection library:
 * a framework for declarative GUI programming on the Java platform.
 * Computing and Visualization in Science, 2013, 16(4),
 * 181–192. http://doi.org/10.1007/s00791-014-0230-y
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.jcsg.ext.path;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Extrude;
import eu.mihosoft.jcsg.Polygon;
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
        throw new AssertionError("Don't instantiate me!");
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param path    path
     * @return CSG object (extruded profile)
     */
    public static CSG alongPath(PathProfile profile, List<Vector3d> path) {
        List<Segment> segments = path.stream().
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, true, true, null, segments);
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param path    path
     * @return CSG object (extruded profile)
     */
    public static CSG alongPath(PathProfile profile, Vector3d... path) {
        List<Segment> segments = Stream.of(path).
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, true, true, null, segments);
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param bottom  determines whether to close bottom segment
     * @param top     determines whether to close top segment
     * @param path    path
     * @return list of polygons (extruded profile)
     */
    public static List<Polygon> alongPath(PathProfile profile, boolean bottom, boolean top, List<Vector3d> path) {
        List<Segment> segments = path.stream().
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, bottom, top, null, segments).getPolygons();
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param bottom  determines whether to close bottom segment
     * @param top     determines whether to close top segment
     * @param path    path
     * @return list of polygons (extruded profile)
     */
    public static List<Polygon> alongPath(PathProfile profile, boolean bottom, boolean top, Vector3d... path) {
        List<Segment> segments = Stream.of(path).
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, bottom, top, null, segments).getPolygons();
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param orientationPlane plane for fixing profile orientation to (optional, may be null)
     * @param path    path
     * @return CSG object (extruded profile)
     */
    public static CSG alongPath(PathProfile profile, Plane orientationPlane, List<Vector3d> path) {
        List<Segment> segments = path.stream().
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, true, true, orientationPlane, segments);
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param orientationPlane plane for fixing profile orientation to (optional, may be null)
     * @param path    path
     * @return CSG object (extruded profile)
     */
    public static CSG alongPath(PathProfile profile, Plane orientationPlane, Vector3d... path) {
        List<Segment> segments = Stream.of(path).
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, true, true, orientationPlane, segments);
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param bottom  determines whether to close bottom segment
     * @param top     determines whether to close top segment
     * @param orientationPlane plane for fixing profile orientation to (optional, may be null)
     * @param path    path
     * @return list of polygons (extruded profile)
     */
    public static List<Polygon> alongPath(PathProfile profile, boolean bottom, boolean top,
                                          Plane orientationPlane, List<Vector3d> path) {
        List<Segment> segments = path.stream().
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, bottom, top, orientationPlane, segments).getPolygons();
    }

    /**
     * Extrudes the specified profile along the given path.
     *
     * @param profile profile to extrude (profile expected in XY plane)
     * @param bottom  determines whether to close bottom segment
     * @param top     determines whether to close top segment
     * @param orientationPlane plane for fixing profile orientation to (optional, may be null)
     * @param path    path
     * @return list of polygons (extruded profile)
     */
    public static List<Polygon> alongPath(PathProfile profile, boolean bottom, boolean top,
                                          Plane orientationPlane, Vector3d... path) {
        List<Segment> segments = Stream.of(path).
                map(p -> new Segment(p, profile)).
                collect(Collectors.toList());

        return extrudeSegments(profile, bottom, top, orientationPlane, segments).getPolygons();
    }

    /**
     * Extrudes the specified segments.
     * @param bottom  determines whether to close bottom segment
     * @param top     determines whether to close top segment
     * @param segments segments to extrude
     * @param orientationPlane plane for fixing profile orientation to (optional, may be null)
     * @return CSG object
     */
    private static CSG extrudeSegments(PathProfile profile,
                                       boolean bottom, boolean top,
                                       Plane orientationPlane,
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

        double angleToPlaneNormal = 0;

        if(orientationPlane !=null) {
            // compute angle between edge (v0,v1) of the profile and
            // the normal of the specified orientation-plane
            angleToPlaneNormal = profilePoints.get(0).minus(profilePoints.get(1)).
                    angle(orientationPlane.getNormal());
        }

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
            //
            // Problematic for screw threads:
            //
            // - one problem with this approach is that the orientation of the
            //   profile along the curve (rotation axis in direction of the curve tangent)
            //   changes
            //
            // - we add support for fixing profile orientation to specified plane
            //
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

            if(orientationPlane!=null) {

                // compute angle between current profile edge (v0, v1) and
                // the normal of the orientation-plane
                double angleToPlaneNormalNew = profilePointsTransformed.get(0).
                        minus(profilePointsTransformed.get(1))
                        .angle(orientationPlane.getNormal());

                // for undoing the rotation with respect to the reference angle
                // 'angleToPlaneNormal' we subtract the current angle from
                // the reference angle
                double angleToRot = angleToPlaneNormal - angleToPlaneNormalNew;

                // define the rotation about curve tangent
                Transform corrRot = Transform.unity().
                        rot(s.getPos(), s.getNormal(), angleToRot);

                List<Vector3d> profilePointsTransformedCorr
                        = new ArrayList<>(profilePointsTransformed.size());

                // finally apply the correction transforms
                for (Vector3d p : profilePointsTransformed) {
                    // apply rotation transform
                    p = p.transformed(corrRot);

                    // add transformed point to list
                    profilePointsTransformedCorr.add(p);
                }

                profilePointsTransformed = profilePointsTransformedCorr;
            }

            // combine both profile profiles and close start and end to 
            // yield a valid CSG object
            CSG csg = CSG.fromPolygons(Extrude.combine(
                    Polygon.fromPoints(profilePoints),
                    Polygon.fromPoints(profilePointsTransformed),
                    i == 1 && bottom, i == segments.size() - 1 && top));

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
