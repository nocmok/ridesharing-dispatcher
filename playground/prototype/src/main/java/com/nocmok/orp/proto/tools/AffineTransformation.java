package com.nocmok.orp.proto.tools;

// Affine transformation for coordinate system
// (xSource, ySource, 1) *  (xScale,       0) = (xTarget, yTarget)
//                          (     0,  yScale)
//                          (xShift,  yShift)
public class AffineTransformation {

    private double xScale;
    private double yScale;
    private double xShift;
    private double yShift;


    public AffineTransformation(double xScale, double yScale, double xShift, double yShift) {
        this.xScale = xScale;
        this.yScale = yScale;
        this.xShift = xShift;
        this.yShift = yShift;
    }

    public double translateX(double x) {
        return x * xScale + xShift;
    }

    public double translateY(double y) {
        return y * yScale + yShift;
    }
}
