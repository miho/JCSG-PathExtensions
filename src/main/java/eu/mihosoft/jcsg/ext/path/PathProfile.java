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
