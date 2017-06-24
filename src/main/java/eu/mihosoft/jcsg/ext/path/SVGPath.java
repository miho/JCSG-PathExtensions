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
package eu.mihosoft.jcsg.ext.path;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Extrude;
import eu.mihosoft.jcsg.Polygon;
import eu.mihosoft.vvecmath.Vector3d;
import java.util.List;

/**
 * Converts simple SVG paths to CSG objects. Sample path:
 * {@code "m 0.18275487,1047.5449 4.63449033,0 0,4.6345 -4.63449033,0 z"}
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class SVGPath {

    private SVGPath() {
        throw new AssertionError("Don't instantiate me!");
    }

    /**
     * Converts closed SVG paths without holes to polygons.
     *
     * @param path path to convert (e.g.
     * {@code "m 0.18275487,1047.5449 4.63449033,0 0,4.6345 -4.63449033,0 z"})
     * @param stepSize step size for path linearization (small is better)
     * @param extension extends the path along vertex normals (XY plane)
     * @return list of convex polygons
     */
    public static final List<Polygon> toPolygons(
            String path, double stepSize, double extension) {
        return Polygon.fromConcavePoints(LinearPathUtil.extend(
                SVGLinearizer.linearizePath(path, (float) stepSize),
                extension));
    }

    /**
     * Converts closed SVG paths without holes to CSG objects (extrudes path).
     *
     * @param path path to convert (e.g.
     * {@code "m 0.18275487,1047.5449 4.63449033,0 0,4.6345 -4.63449033,0 z"})
     * @param height extrusion hight.
     * @param stepSize step size for path linearization (small is better)
     * @param extension extends the path along vertex normals (XY plane)
     * @return CSG object
     */
    public static CSG toCSG(String path, double height,
            double stepSize, double extension) {
        return Extrude.points(Vector3d.z(height),
                LinearPathUtil.extend(
                        SVGLinearizer.linearizePath(path, (float) stepSize),
                        extension));
    }
}
