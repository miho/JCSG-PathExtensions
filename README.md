# JCSG-PathExtensions
[ ![Download](https://api.bintray.com/packages/miho/JCSG/JCSG-PathExtensions/images/download.svg) ](https://bintray.com/miho/JCSG/JCSG-PathExtensions/_latestVersion)

A [JCSG](https://github.com/miho/JCSG) extension library for working with simple paths (linearize SVG paths, extrude &amp; extend)

![](https://raw.githubusercontent.com/miho/JCSG-PathExtensions/master/resources/img/sample-01.jpg)

## Sample Code 1
```java
public class ExtrudeSVGPath {

    public static void main(String[] args) throws IOException {

        // a simple closed SVG path
        String svgPath = "m 168.22705,341.58319 c -8.30208,0 -14.9857,6.68362 "
                + "-14.9857,14.9857 l 0,76.53677 c 0,8.30208 6.68362,14.9857 "
                + "14.9857,14.9857 l 76.53677,0 c 8.30208,0 14.9857,-6.68362 "
                + "14.9857,-14.9857 l 0,-76.53677 c 0,-8.30208 "
                + "-6.68362,-14.9857 -14.9857,-14.9857 l -76.53677,0 z";

        // extrude the path
        CSG extrudedPath
                = SVGPath.toCSG(
                        svgPath, // svg path
                        10.0, //    extrusion height
                        0.05, //    step size
                        0.0); //    extension

        // save path to disk
        Files.write(Paths.get("full.stl"),
                extrudedPath.toStlString().getBytes());
    }
}
```

## Sample Code 2
```java
public class ExtrudeSVGPath {

    public static void main(String[] args) throws IOException {

        // a simple closed SVG path
        String svgPath = "m 168.22705,341.58319 c -8.30208,0 -14.9857,6.68362 "
                + "-14.9857,14.9857 l 0,76.53677 c 0,8.30208 6.68362,14.9857 "
                + "14.9857,14.9857 l 76.53677,0 c 8.30208,0 14.9857,-6.68362 "
                + "14.9857,-14.9857 l 0,-76.53677 c 0,-8.30208 "
                + "-6.68362,-14.9857 -14.9857,-14.9857 l -76.53677,0 z";

        // create a hull with thickness = 1.0
        double thickness = 10;

        CSG outer = SVGPath.toCSG(
                svgPath, //    svg path
                10.0, //       extrusion height
                0.01, //       step size
                thickness); // extension

        CSG inner = SVGPath.toCSG(
                svgPath, //    svg path
                10.0, //       extrusion height
                0.01, //       step size
                0.0); //       extension

        CSG hull = outer.difference(inner);

        // save hull path to disk
        Files.write(Paths.get("hull.stl"),
                hull.toStlString().getBytes());
    }
}
```

## How to Build JCSG-PathExtensions

### Requirements

- Java >= 1.8
- Internet connection (dependencies are downloaded automatically)
- IDE: [Gradle](http://www.gradle.org/) Plugin (not necessary for command line usage)

### IDE

Open the `JCSG-PathExtensions` [Gradle](http://www.gradle.org/) project in your favourite IDE (tested with NetBeans 8.2) and build it
by calling the `assemble` task.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/JCSG-PathExtensions`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    bash gradlew assemble
    
#### Windows (CMD)

    gradlew assemble

