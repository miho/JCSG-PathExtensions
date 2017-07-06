/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.jcsg.ext.path;

import eu.mihosoft.jcsg.Extrude;
import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.vvecmath.Vector3d;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Path profile. A profile consists of a center and a list of points which
 * define the outline of the profile.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class PathProfile {

    private final List<Vector3d> points = new ArrayList<>();
    private final Vector3d center;

    /**
     * Constructor. Creates a new profile.
     *
     * @param center center of the profile
     * @param points profile points (outline in XY plane)
     */
    private PathProfile(Vector3d center, List<Vector3d> points) {
        this.center = center;
        this.points.addAll(points);

        if (!Extrude.isCCW(Polygon.fromPoints(this.points))) {
            // we need to revert if the path is not defined counter-clockwise
            Collections.reverse(points);
        }
    }

    /**
     * Constructor. Creates a new profile from the specified prototype (performs
     * a deep copy).
     *
     * @param other profile prototype
     */
    public PathProfile(PathProfile other) {
        this.center = other.center;
        this.points.addAll(other.points.stream().map(point -> point.clone()).collect(Collectors.toList()));
    }

    public List<Vector3d> getPoints() {
        return points;
    }

    public Vector3d getCenter() {
        return center;
    }

    /**
     * Creates a new profile from the specified points.
     *
     * @param center profie center
     * @param points profile points (outline in XY plane)
     * @return new profile
     */
    public static PathProfile fromPoints(Vector3d center, List<Vector3d> points) {
        return new PathProfile(center, points);
    }
}
