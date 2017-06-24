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

import eu.mihosoft.vvecmath.Vector3d;
import java.util.ArrayList;
import java.util.List;

/**
 * Tools for linear paths.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class LinearPathUtil {

    private LinearPathUtil() {
        throw new AssertionError("Don't instantiate me!");
    }

    /**
     * Extends the specified linear closed path by the given amount.
     *
     * @param path path to extend
     * @param amount amount
     * @return extended linear path (list of points)
     */
    public static List<Vector3d> extend(List<Vector3d> path, double amount) {
        List<Vector3d> result = new ArrayList<>(path.size());

        // 1. compute edge normals
        List<Vector3d> edgeNormals = new ArrayList<>(path.size());

        for (int i = 1; i < path.size(); i++) {

            Vector3d segment = path.get(i).minus(path.get(i - 1));
            edgeNormals.add(Vector3d.xy(-segment.y(), segment.x()).normalized());
        }

        Vector3d segment = path.get(0).minus(path.get(path.size() - 1));
        edgeNormals.add(Vector3d.xy(-segment.y(), segment.x()).normalized());

        // 2. compute vertex normals (average of adjacent edge normals)
        List<Vector3d> vertexNormals = new ArrayList<>(path.size());

        Vector3d n0 = edgeNormals.get(edgeNormals.size() - 1).
                lerp(edgeNormals.get(0), 0.5).
                normalized();
        vertexNormals.add(n0);

        for (int i = 1; i < edgeNormals.size(); i++) {
            Vector3d n = edgeNormals.get(i).lerp(edgeNormals.get(i - 1), 0.5).
                    normalized();
            vertexNormals.add(n);
        }

        // 3. extend path along vertex normals
        for (int i = 0; i < path.size(); i++) {
            result.add(path.get(i).plus(vertexNormals.get(i).
                    times(amount * 0.5)));
        }

        return result;
    }
}
