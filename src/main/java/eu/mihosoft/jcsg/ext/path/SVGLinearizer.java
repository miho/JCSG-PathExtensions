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
 * SVG path linearizer. SVG paths of the form
 * {@code "m 0.18275487,1047.5449 4.63449033,0 0,4.6345 -4.63449033,0 z"} are
 * linearized, i.e., converted to a list of points.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@SuppressWarnings( "deprecation" )
public final class SVGLinearizer {

    private SVGLinearizer() {
        throw new AssertionError("Don't instantiate me!");
    }

    /**
     * Linearizes the the specified SVG path.
     *
     * @param svgPath svg path to linearize, e.g.,
     * {@code "m 0.18275487,1047.5449 4.63449033,0 0,4.6345 -4.63449033,0 z"}
     * @param step step size to use for path sampling ({@code 0.0 < step < 1.0})
     *
     * @return linearized svg path points
     */
    @SuppressWarnings( "deprecation" )
    public static List<Vector3d> linearizePath(String svgPath, float step) {

        if (step <= 0) {
            throw new IllegalArgumentException(
                    "Illegal step specified: step must be > 0!");
        }

        if (step >= 1) {
            throw new IllegalArgumentException(
                    "Illegal step specified: step must be < 1!");
        }

        eu.mihosoft.jcsg.ext.path.internal.InternalBezierPath path
                = new eu.mihosoft.jcsg.ext.path.internal.InternalBezierPath();
        path.parsePathString(svgPath);

        List<Vector3d> result = new ArrayList<>((int) (1.0 / step));

        float t = 0;
        while (t < 1.0) {
            result.add(path.eval(t));
            t += step;
        }

        return result;
    }
}
