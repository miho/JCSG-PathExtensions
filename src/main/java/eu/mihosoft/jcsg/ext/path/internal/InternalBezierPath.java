/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package eu.mihosoft.jcsg.ext.path.internal;

import eu.mihosoft.vvecmath.Vector3d;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class InternalBezierPath {

    private static final Matcher MATCH_POINT_PATTERN = 
            Pattern.compile("\\s*(\\d+)[^\\d]+(\\d+)\\s*").matcher("");

    private BezierListProducer path;

    /**
     * Creates a new instance of Animate
     */
    public InternalBezierPath() {
    }

    public void parsePathString(String d) {

        this.path = new BezierListProducer();

        parsePathList(d);
    }

    protected void parsePathList(String list) {
        final Matcher matchPathCmd = 
                Pattern.compile("([MmLlHhVvAaQqTtCcSsZz])|([-+]?((\\d*\\.\\d+)|(\\d+))([eE][-+]?\\d+)?)").matcher(list);

        //Tokenize
        LinkedList<String> tokens = new LinkedList<>();
        while (matchPathCmd.find()) {
            tokens.addLast(matchPathCmd.group());
        }

        char curCmd = 'Z';
        while (!tokens.isEmpty()) {
            String curToken = tokens.removeFirst();
            char initChar = curToken.charAt(0);
            if ((initChar >= 'A' && initChar <= 'Z') || (initChar >= 'a' && initChar <= 'z')) {
                curCmd = initChar;
            } else {
                tokens.addFirst(curToken);
            }

            switch (curCmd) {
                case 'M':
                    path.movetoAbs(nextFloat(tokens), nextFloat(tokens));
                    curCmd = 'L';
                    break;
                case 'm':
                    path.movetoRel(nextFloat(tokens), nextFloat(tokens));
                    curCmd = 'l';
                    break;
                case 'L':
                    path.linetoAbs(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'l':
                    path.linetoRel(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'H':
                    path.linetoHorizontalAbs(nextFloat(tokens));
                    break;
                case 'h':
                    path.linetoHorizontalRel(nextFloat(tokens));
                    break;
                case 'V':
                    path.linetoVerticalAbs(nextFloat(tokens));
                    break;
                case 'v':
                    path.linetoVerticalAbs(nextFloat(tokens));
                    break;
                case 'A':
                case 'a':
                    break;
                case 'Q':
                    path.curvetoQuadraticAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'q':
                    path.curvetoQuadraticAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'T':
                    path.curvetoQuadraticSmoothAbs(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 't':
                    path.curvetoQuadraticSmoothRel(nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'C':
                    path.curvetoCubicAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'c':
                    path.curvetoCubicRel(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'S':
                    path.curvetoCubicSmoothAbs(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 's':
                    path.curvetoCubicSmoothRel(nextFloat(tokens), nextFloat(tokens),
                            nextFloat(tokens), nextFloat(tokens));
                    break;
                case 'Z':
                case 'z':
                    path.closePath();
                    break;
                default:
                    throw new RuntimeException("Invalid path element");
            }
        }
    }

    static protected float nextFloat(LinkedList<String> l) {
        String s = l.removeFirst();
        return Float.parseFloat(s);
    }

    /**
     * Evaluates this animation element for the passed interpolation time.
     * Interp must be on [0..1].
     *
     * @param interp
     * @return
     */
    public Vector3d eval(float interp) {
        SVGVector point = new SVGVector();

        double curLength = path.curveLength * interp;
        for (Iterator<Bezier> it = path.bezierSegs.iterator(); it.hasNext();) {
            Bezier bez = it.next();

            double bezLength = bez.getLength();
            if (curLength < bezLength) {
                double param = curLength / bezLength;
                bez.eval(param, point);
                break;
            }

            curLength -= bezLength;
        }

        return Vector3d.xy(point.x, point.y);
    }

}
