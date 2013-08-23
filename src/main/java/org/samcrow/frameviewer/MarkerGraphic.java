package org.samcrow.frameviewer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Enumerates different things that can be drawn to represent markers
 * <p/>
 * @author Sam Crow
 */
public enum MarkerGraphic {

    Circle (new Paintable() {
        @Override
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
            gc.setStroke(color);
            gc.setLineWidth(LINE_WIDTH);
            gc.strokeOval(centerX - RADIUS, centerY - RADIUS, RADIUS * 2, RADIUS * 2);
        }
    }),
    
    FilledDiagonalSquare (new Paintable() {
        @Override
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
            gc.translate(centerX, centerY);
            gc.rotate(45);
            
            gc.setStroke(color);
            gc.setFill(color);
            gc.setLineWidth(LINE_WIDTH);
            
            final double x = -RADIUS;
            final double y = -RADIUS;
            final double width = RADIUS * 2;
            final double height = RADIUS * 2;
            
            gc.fillRect(x, y, width, height);
            gc.strokeRect(x, y, width, height);
            
            gc.rotate(-45);
            gc.translate(-centerX, -centerY);
        }
    }),
    PlusSign (new Paintable() {

        @Override
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
            gc.setStroke(color);
            gc.setLineWidth(LINE_WIDTH);
            //Vertical line
            gc.strokeLine(centerX, centerY - RADIUS, centerX, centerY + RADIUS);
            //Horizontal line
            gc.strokeLine(centerX - RADIUS, centerY, centerX + RADIUS, centerY);
            
        }
    }),
    X (new Paintable() {

        @Override
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
            gc.setStroke(color);
            gc.setLineWidth(LINE_WIDTH);
            //Diagonal line descending to the right
            gc.strokeLine(centerX - RADIUS, centerY - RADIUS, centerX + RADIUS, centerY + RADIUS);
            //Diagonal line ascending to the right
            gc.strokeLine(centerX - RADIUS, centerY + RADIUS, centerX + RADIUS, centerY - RADIUS);
        }
    }),
    DiagonalSquare (new Paintable() {

        @Override
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
            gc.translate(centerX, centerY);
            gc.rotate(45);
            
            gc.setStroke(color);
            gc.setLineWidth(LINE_WIDTH);
            
            final double x = -RADIUS;
            final double y = -RADIUS;
            final double width = RADIUS * 2;
            final double height = RADIUS * 2;
            
            gc.strokeRect(x, y, width, height);
            
            gc.rotate(-45);
            gc.translate(-centerX, -centerY);
        }
    }),
    Triangle (new Paintable() {

        @Override
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
            gc.setStroke(color);
            gc.setLineWidth(LINE_WIDTH);
            
            final double bottomY = centerY + RADIUS;
            final double topY = centerY - RADIUS;
            final double leftX = centerX - RADIUS;
            final double rightX = centerX + RADIUS;
            
            gc.beginPath();
            gc.moveTo(leftX, bottomY);
            gc.lineTo(rightX, bottomY);
            gc.lineTo(centerX, topY);
            gc.closePath();
            gc.stroke();
        }
    }),
    
    Square (new Paintable() {
        @Override
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
            gc.setLineWidth(LINE_WIDTH);
            gc.setStroke(color);
            
            gc.strokeRect(centerX - RADIUS, centerY - RADIUS, RADIUS * 2, RADIUS * 2);
        }
    })
    
    ;

    /**
     * The paintable used to draw this graphic
     */
    private final Paintable paintable;

    private MarkerGraphic(Paintable paintable) {
        this.paintable = paintable;
    }

    /**
     * Paints this graphic, centered on the specified location
     * <p/>
     * @param gc The context in which to paint
     * @param color The color in which to paint this graphic
     * @param centerX The X location of the center of this graphic
     * @param centerY The Y location of the center of this graphic
     */
    public void paint(GraphicsContext gc, Color color, double centerX, double centerY) {
        paintable.paint(gc, color, centerX, centerY);
    }

    /**
     * An interface for something that can be painted onto a Canvas
     */
    private interface Paintable {

        /**
         * Paints this thing, centered on the specified location
         * <p/>
         * @param gc The context in which to paint
         * @param color The color in which to paint this thing
         * @param centerX The X location of the center of this thing
         * @param centerY The Y location of the center of this thing
         */
        public void paint(GraphicsContext gc, Color color, double centerX, double centerY);

    }

    /**
     * A hint to enumerated values of the approximate radius that they should draw in
     */
    private static final double RADIUS = 3;
    /**
     * The line width that graphics should use when painting
     */
    private static final double LINE_WIDTH = 2;
}
