package eu.mihosoft.jcsg.ext.path;

import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.Cube;
import eu.mihosoft.jcsg.Extrude;
import eu.mihosoft.jcsg.Polygon;

import eu.mihosoft.vvecmath.Transform;
import eu.mihosoft.vvecmath.Vector3d;
import eu.mihosoft.vvecmath.Vectors3d;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {


    private static CSG circle(Vector3d normal) {
        Circle c1 = new Circle(Vector3d.ZERO, normal, 10, 64);
        CSG csg = new Cube(Vector3d.ZERO, Vector3d.UNITY.times(0.5)).toCSG();
        for (Vector3d v : c1.getPoints()) {
            CSG vCSG = new Cube(v, Vector3d.UNITY.times(0.5)).toCSG();
            csg = csg.union(vCSG);
        }
        return csg;
    }

    /**
     * @param args the command line arguments
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        List<CSG> csgs = new ArrayList<>();

        List<Segment> path = new ArrayList<>();

        double d = 0.2;

        List<Vector3d> profilePath = Vectors3d.xz(
                -d,-d,
                d,-d,
                d,d,
                -d,d
                );

        Profile profile = new Profile(Vector3d.ZERO, profilePath);


        double dt = 0.01; // resolution (smaller is better, 1.0 means one turn)

        double r = 5;    // radius of the helix
        double c = 3; // pitch (height per turn)

        double h = 3;   // number of turns

        double t = 0.0;

        // add first segment at t = 0.0
        Segment firstSegment = new Segment(Vector3d.x(r), profile);
        path.add(firstSegment);
        Segment prevSegment = firstSegment;

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

            Segment s = new Segment(Vector3d.xyz(x, y, z), profile);
            path.add(s);

            Vector3d normal = null;

            normal = s.getPos().minus(prevSegment.getPos()).normalized();
            prevSegment.setNormal(normal);

            prevSegment = s;
            prevSegment.setNormal(normal);

        }

        System.out.println("> extrude path with size: " + path.size());
        CSG pathCSG = extrudePath(path);
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

    private static CSG extrudePath(List<Segment> segments) {

        System.out.println("num segments: " + segments.size());

//        Spline3D spline = new Spline3D();
//        Spline3D rSpline = new Spline3D();
//        for (int i = 0;
//             i < segments.size();
//             i++) {
//            Segment s = segments.get(i);
//            Vector3d pos = s.getPos();
//            spline.addPoint(pos);
//            rSpline.addPoint(Vector3d.x(s.getR()));
//        }
//
//        spline.calcSpline();
//        rSpline.calcSpline();
//        List<Segment> newSegments = new ArrayList<>();
//        double dt = 1.0f / (segments.size() * 0.7);
//        double dt2 = spline.getPoint(0).minus(spline.getPoint(0 + 0.001)).magnitude();
//        double t = 0;
//        double tMax = 1.0;
//        double dtMin = 1e-2;
//        int index = 0;
//        while (t < tMax) {
//
//            double t2 = t + dt2 * 0.1;
//
//            if (t2 > tMax - 1e-6) {
//                t2 = tMax - 1e-6;
//            }
//
//            dt2 = dt;
//
////            if (t == 0) {
////                dt2 = dt2 * 0.5;
////            }
//            if (dt2 < dtMin) {
//                dt2 = dtMin;
//            }
//
//            Vector3d pos1 = spline.getPoint(t);
//            Vector3d pos2 = spline.getPoint(t2);
//
//            int pIndex;
//
//            if (index == 0) {
//                pIndex = -1;
//            } else {
//                pIndex = index - 1 + 1;
//            }
//
//            double r = rSpline.getPoint(t).x();
//
//            Segment s = new Segment(index, 0,
//                    Vector3d.xyz(pos1.x(), pos1.y(), pos1.z()), r, pIndex);
//
//            Vector3d normal = pos2.minus(pos1).normalized();
//            s.setNormal(normal);
//            newSegments.add(s);
//
//            t += dt2;
//
//            index++;
//        }
//
//        segments = newSegments;
        int res = 16;

//        Circle circle = new Circle(
//                segments.get(0).getPos(),
//                segments.get(0).getNormal(),
//                segments.get(0).getR(),
//                res);

        List<Vector3d> circlePoints = segments.get(0).getPoints();
        List<Vector3d> prevCirclePoints = circlePoints;

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

            // translate circle points to new curve point
            // rotate circle points about binormal with curve angle 
            List<Vector3d> circlePointsTransformed
                    = new ArrayList<>(prevCirclePoints.size());

            // finally apply the transforms
            for (Vector3d p : prevCirclePoints) {
                // move points points from previous path point to current one
                p = p.plus(segments.get(i).getPos().
                        minus(segments.get(i - 1).getPos()));
                // apply rotation transform
                p = p.transformed(rot);

                // add transformed point to list
                circlePointsTransformed.add(p);
            }

            // combine both circle profiles and close start and end to 
            // yield a valid CSG object
            CSG csg = CSG.fromPolygons(Extrude.combine(
                    Polygon.fromPoints(prevCirclePoints),
                    Polygon.fromPoints(circlePointsTransformed),
                    i == 1, i == segments.size() - 1));

            polygons.addAll(csg.getPolygons());

            // use the current circle points as start points for the next
            // segment
            prevCirclePoints = circlePointsTransformed;
        }

        // finally, create the CSG
        CSG pathCSG = CSG.fromPolygons(polygons);

        return pathCSG;
    }
}

class Segment {

    private final Vector3d pos;
    private Profile profile;
    private Vector3d normal;

    public Segment(Vector3d pos, Profile profile) {
        this.pos = pos;
        this.profile = profile;
    }

    public Segment(Segment other) {
        this.pos = other.pos;
        this.profile = new Profile(other.profile);
        this.normal = other.normal;
    }

    public Vector3d getPos() {
        return pos;
    }

    public Profile getProfile() {
        return this.profile;
    }

    public List<Vector3d> getPoints() {
        return  getProfile().getPoints().stream().map(p->p.transformed(Transform.unity().
                translate(getPos().minus(getProfile().getCenter())))).collect(Collectors.toList());
    }

    public Vector3d getNormal() {
        return normal;
    }

    public void setNormal(Vector3d normal) {
        this.normal = normal;
    }


}

class Profile {
    private final List<Vector3d> points = new ArrayList<>();
    private final Vector3d center;

    public Profile(Vector3d center, List<Vector3d> points) {
        this.center = center;
        this.points.addAll(points);
    }

    public Profile(Profile other) {
        this.center = other.center;
        this.points.addAll(other.points.stream().map(point-> point.clone()).collect(Collectors.toList()));
    }

    public List<Vector3d> getPoints() {
        return points;
    }

    public Vector3d getCenter() {
        return center;
    }
}
