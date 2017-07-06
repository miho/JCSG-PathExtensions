/**
 * Cylinder.java
 *
 * Copyright 2014-2014 Michael Hoffer <info@michaelhoffer.de>. All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Michael Hoffer
 * <info@michaelhoffer.de>.
 */
package eu.mihosoft.jcsg.ext.path;

import eu.mihosoft.jcsg.PropertyStorage;
import eu.mihosoft.jcsg.Vertex;
import eu.mihosoft.vvecmath.ModifiableVector3d;
import eu.mihosoft.vvecmath.Transform;
import eu.mihosoft.vvecmath.Vector3d;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A solid cylinder.
 *
 * The tessellation can be controlled via the {@link #numSlices} parameter.
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class Circle {

    private static final long serialVersionUID = 1L;

    private Vector3d start;
    private Vector3d dir;
    private double startRadius;
    private int numSlices;

    private final PropertyStorage properties = new PropertyStorage();

    /**
     * Constructor. Creates a new cylinder with center {@code [0,0,0]} and
     * ranging from {@code [0,-0.5,0]} to {@code [0,0.5,0]}, i.e.
     * {@code size = 1}.
     */
    public Circle() {
        this.start = Vector3d.xyz(0, -0.5, 0);
        this.dir = Vector3d.xyz(0, 0.5, 0);
        this.startRadius = 1;
        this.numSlices = 16;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code start} to {@code end}
     * with the specified {@code radius}. The resolution of the tessellation can
     * be controlled with {@code numSlices}.
     *
     * @param start cylinder start
     * @param end cylinder end
     * @param radius cylinder radius
     * @param numSlices number of slices (used for tessellation)
     */
    public Circle(Vector3d start, Vector3d dir, double radius, int numSlices) {
        this.start = start;
        this.dir = dir;
        this.startRadius = radius;
        this.numSlices = numSlices;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code [0,0,0]} to
     * {@code [0,0,height]} with the specified {@code radius} and
     * {@code height}. The resolution of the tessellation can be controlled with
     * {@code numSlices}.
     *
     * @param radius cylinder radius
     * @param height cylinder height
     * @param numSlices number of slices (used for tessellation)
     */
    public Circle(double radius, double height, int numSlices) {
        this.start = Vector3d.ZERO;
        this.dir = Vector3d.Z_ONE.times(height);
        this.startRadius = radius;
        this.numSlices = numSlices;
    }

    /**
     * Constructor. Creates a cylinder ranging from {@code [0,0,0]} to
     * {@code [0,0,height]} with the specified {@code radius} and
     * {@code height}. The resolution of the tessellation can be controlled with
     * {@code numSlices}.
     *
     * @param startRadius cylinder start radius
     * @param endRadius cylinder end radius
     * @param height cylinder height
     * @param numSlices number of slices (used for tessellation)
     */
    public Circle(double startRadius, double endRadius, double height, int numSlices) {
        this.start = Vector3d.ZERO;
        this.dir = Vector3d.Z_ONE.times(height);
        this.startRadius = startRadius;
        this.numSlices = numSlices;
    }

    public List<Vector3d> toPoints() {
        final Vector3d s = getStart();
        final Vector3d ray = getDir();
        final Vector3d axisZ = ray.normalized();
        boolean isY = (Math.abs(axisZ.y()) > 0.5);
        final Vector3d axisX = Vector3d.xyz(isY ? 1 : 0, !isY ? 1 : 0, 0).
                crossed(axisZ).normalized();
        final Vector3d axisY = axisX.crossed(axisZ).normalized();
        Vertex startV = new Vertex(s, axisZ.negated());
        List<Vector3d> points = new ArrayList<>();

        for (int i = 0; i < numSlices; i++) {
            double t0 = i / (double) numSlices, t1 = (i + 1) / (double) numSlices;

            points.add(cylPoint(axisX, axisY, axisZ, ray, s, startRadius, 0, t0, -1).pos);

        }

        return points;
    }

    private Vertex cylPoint(
            Vector3d axisX, Vector3d axisY, Vector3d axisZ, Vector3d ray, Vector3d s,
            double r, double stack, double slice, double normalBlend) {
        double angle = slice * Math.PI * 2;
        Vector3d out = axisX.times(Math.cos(angle)).plus(axisY.times(Math.sin(angle)));
        Vector3d pos = s.plus(ray.times(stack)).plus(out.times(r));
        Vector3d normal = out.times(1.0 - Math.abs(normalBlend)).plus(axisZ.times(normalBlend));
        return new Vertex(pos, normal);
    }

    /**
     * @return the start
     */
    public Vector3d getStart() {
        return start;
    }

    /**
     * @param start the start to set
     */
    public void setStart(Vector3d start) {
        this.start = start;
    }

    /**
     * @return the end
     */
    public Vector3d getDir() {
        return dir;
    }

    /**
     * @param end the end to set
     */
    public void setDir(Vector3d dir) {
        this.dir = dir;
    }

    /**
     * @return the radius
     */
    public double getStartRadius() {
        return startRadius;
    }

    /**
     * @param radius the radius to set
     */
    public void setStartRadius(double radius) {
        this.startRadius = radius;
    }

    /**
     * @return the number of slices
     */
    public int getNumSlices() {
        return numSlices;
    }

    /**
     * @param numSlices the number of slices to set
     */
    public void setNumSlices(int numSlices) {
        this.numSlices = numSlices;
    }
    
    public List<Vector3d> getPoints() {
        List<ModifiableVector3d> points = createCircleAboutAxis(dir, startRadius, numSlices);
        
        Transform position = Transform.unity().translate(start);
        
        for (ModifiableVector3d point : points) {
            position.transform(point);
        }
        
        return (List)points;
    }

    public List<ModifiableVector3d> createCircleAboutAxis(final Vector3d axis, double r, int ewSteps) {
        ModifiableVector3d[] circle = new ModifiableVector3d[ewSteps-1];
        double a = 0;
        double da = (Math.PI * 2) / (ewSteps - 1.0f);
        // First create the circle about the (0,1,0) axis
        for (int i = 0; i < ewSteps - 1; i++) {
            circle[i] = Vector3d.xz(r*Math.cos(a), r*Math.sin(a)).asModifiable();
            a -= da;
        }
        //circle[ewSteps - 1] = circle[0].asModifiable();
        Vector3d orgAxis = Vector3d.xyz(0, 1, 0);

        if (!orgAxis.equals(axis)) {
            Transform rot = Transform.unity().rot(Vector3d.y(1), axis);
            for (int i = 0; i < ewSteps-1; i++) {
                rot.transform(circle[i]);
            }
        }
        return Arrays.asList(circle).stream().distinct().collect(Collectors.toList());
    }

}
