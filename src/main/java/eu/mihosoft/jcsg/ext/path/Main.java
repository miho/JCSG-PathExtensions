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
import eu.mihosoft.vvecmath.Vector3d;
import eu.mihosoft.vvecmath.Vectors3d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /**
     * @param args the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        List<CSG> csgs = new ArrayList<>();

        List<Vector3d> path = new ArrayList<>();

        double d = 0.2;

        List<Vector3d> profilePath = Vectors3d.xy(
                -d, -d,
                d, -d,
                d, d,
                -d, d
        );

        double dt = 0.01; // resolution (smaller is better, 1.0 means one turn)

        double r = 5; //     radius of the helix
        double c = 3; //     pitch (height per turn)

        double h = 3; //     number of turns

        double t = 0.0;

        // add first segment at t = 0.0
        path.add(Vector3d.x(r));

        while (t < h) {

            t += dt;

            // check if t is out of bounds:
            // if yes then set t to final curve point
            if (t > h) {
                t = h;
            }

            double x = r * Math.cos(2 * Math.PI * t);
            double y = r * Math.sin(2 * Math.PI * t);
            double z = c * t;

            Vector3d s = Vector3d.xyz(x, y, z);
            path.add(s);
        }

        System.out.println("> extrude path with size: " + path.size());
        CSG pathCSG = ExtrudeProfile.alongPath(
                PathProfile.fromPoints(Vector3d.ZERO, profilePath), path);
        csgs.add(pathCSG);

        System.out.println(
                "#paths: " + csgs.size());

        CSG result = csgs.get(0);

//        // perform the optimization
//        result = MeshTools.optimize(
//                result, // csg object to optimize
//                1e-8, // tolerance
//                1e-6, // max tolerance
//                0.5, // min edge length
//                1.0, // max edge length
//                10, // max iterations for edge adjustment
//                30 // crease edge marker threshold angle
//        );
        Files.write(Paths.get("test.stl"), result.toStlString().getBytes());
    }

}
