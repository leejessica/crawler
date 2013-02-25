`/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MapFrame.java
 *
 * Created on Aug 16, 2010, 9:45:42 PM
 */
package mo.umac.geo.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author yyy
 */
public class MapFrame extends javax.swing.JFrame implements Runnable {
//
//    protected static final Color[] colors = {Color.red, Color.orange, Color.yellow, Color.green, Color.blue,
//        Color.magenta, Color.lightGray};

//    protected static final Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GRAY,
//        Color.GREEN, Color.LIGHT_GRAY, Color.MAGENTA, Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW, new Color(0, 0, 64), new Color(0, 64, 0), new Color(64, 0, 0),
//        new Color(0, 0, 128), new Color(0, 128, 0), new Color(128, 0, 0), new Color(64, 64, 128), new Color(64, 128, 64), new Color(128, 64, 64), new Color(128, 128, 192), new Color(128, 192, 128), new Color(192, 128, 128)};
    protected static Color[] colors;
    public final static int colorSize = 100;
    private int frameWidth;
    private int frameHeight;
    private int imageWidth;
    private int imageHeight;
    public final static double latitudeMin = 22.175960091218524;
    public final static double latitudeMax = 22.56773178276118;
    public final static double longitudeMin = 113.79913330078125;
    public final static double longitudeMax = 114.44252014160156;
    private double latitudeRange;
    private double longitudeRange;
    private int heightRange;
    private int widthRange;
    private List<Integer> xValues = new ArrayList<Integer>();
    private List<Integer> yValues = new ArrayList<Integer>();
    private List<Integer> cValues = new ArrayList<Integer>();
    private final static Color dotColor = Color.RED;
    private final static int dotSize = 2;

    /** Creates new form MapFrame */
    public MapFrame() {
        initComponents();




        this.initialLizeResources();

//        this.markMap(100, 100);


//        this.setBackground(this.getContentPane().getBackground());

    }

    public MapFrame(List<String> values) {
        super();

//        clear all buffers
        xValues.clear();
        yValues.clear();

        System.out.println("Totally " + values.size() / 2 + " points ");

        double lati, longi;
        for (int i = 0, size = values.size(); i < size; i += 2) {
            lati = Double.parseDouble(values.get(i));
            longi = Double.parseDouble(values.get(i + 1));


            int xValue = (int) ((lati - latitudeMin) * this.imageHeight / this.latitudeRange);
            int yValue = (int) ((longi - longitudeMin) * this.imageWidth / this.longitudeRange);

            yValue = yValue - this.heightRange;
            xValue = xValue - this.widthRange;

            xValues.add(xValue);
            yValues.add(yValue);
//            System.out.println("Transformed height and width : " + xValue + " - " + yValue);
        }
//        initComponents();


//        this.initialLizeResources();

//        this.markMap(100, 100);


//        this.setBackground(this.getContentPane().getBackground());

    }

    private void initialLizeResources() {
        this.setResizable(false);
//        this.setSize(imageWidth, 615);
//        this.iconPanel.setSize(imageWidth, 615);
//        this.iconLabel.setSize(imageWidth, 615);
        this.frameWidth = this.getWidth();
        this.frameHeight = this.getHeight();
        this.imageWidth = this.iconLabel.getWidth();
        this.imageHeight = this.iconLabel.getHeight();
//        this.iconLabel.setOpaque(true);
//        this.iconPanel.setOpaque(true);


        this.latitudeRange = latitudeMax - latitudeMin;
        this.longitudeRange = longitudeMax - longitudeMin;

        this.heightRange = 30;
        this.widthRange = 4;
//        this.heightRange = this.frameHeight - this.imageHeight;
//        this.widthRange = this.frameWidth - this.imageWidth;

//        xValues.add(100);
//        yValues.add(100);
//
//        xValues.add(108);
//        yValues.add(108);

        System.out.println("Frame : " + this.getWidth() + " - " + this.getHeight());
        System.out.println("Panel : " + this.iconPanel.getWidth() + " - " + this.iconPanel.getHeight());
        System.out.println("Label : " + this.iconLabel.getWidth() + " - " + this.iconLabel.getHeight());
        System.out.println(latitudeRange);
        System.out.println(longitudeRange);


        colors = new Color[colorSize];

        Random random = new Random(0);

        //color format 0xFFFFFF
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        }

//        this.transformGeoToImage(22.39, 114.11);
    }

    public void transformGeoToImage(double latitude, double longitude, int color) {
        this.transformGeoToImage(latitude, longitude);
        cValues.add(color);
    }

    //transform and insert into the x and y buffer
    public void transformGeoToImage(double latitude, double longitude) {
//        System.out.println(latitude + " - " + longitude);
        int yValue = (int) ((latitude - latitudeMin) * this.imageHeight / this.latitudeRange);
        int xValue = (int) ((longitude - longitudeMin) * this.imageWidth / this.longitudeRange);
//        System.out.println("Transformed height and width : " + iHeight + " - " + iWidth);
        yValue = yValue - this.heightRange;
        xValue = xValue - this.widthRange;

        yValue = this.imageHeight - yValue;
        xValues.add(xValue);
        yValues.add(yValue);
//        System.out.println(latitude + " - " + longitude + " To " + xValue + " - " + yValue);

//        this.repaint();
    }

    public void renewPointBuffer(double[] latis, double[] longis) {
        //clear all buffers
        xValues.clear();
        yValues.clear();

        for (int i = 0, size = Math.min(latis.length, longis.length); i < size; i++) {
            this.transformGeoToImage(latis[i], longis[i]);
        }
    }

    public void renewPointBuffer(List<String> values) {
        //clear all buffers
        xValues.clear();
        yValues.clear();

        System.out.println("Totally " + values.size() / 2 + " points ");

        double lati, longi;
        for (int i = 0, size = values.size(); i < size; i += 2) {
            lati = Double.parseDouble(values.get(i));
            longi = Double.parseDouble(values.get(i + 1));
            this.transformGeoToImage(lati, longi);
        }

//        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

//        Graphics2D g2 = (Graphics2D) g;
        int xValue, yValue;

        int colorIndex;
//        g.setColor(dotColor);
        for (int i = 0, size = Math.min(xValues.size(), yValues.size()); i < size; i++) {
            colorIndex = cValues.get(i);

            if (colorIndex >= MapFrame.colors.length || colorIndex < 0) {
                colorIndex = MapFrame.colors.length - 1;
            }

            g.setColor(MapFrame.colors[colorIndex]);

            xValue = xValues.get(i);
            yValue = yValues.get(i);
            g.fillRect(xValue, yValue, dotSize, dotSize);
//            System.out.println("Draw ");
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        iconPanel = new javax.swing.JPanel();
        iconLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        iconLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/flickr/util/gui/images/HKMap.PNG"))); // NOI18N

        javax.swing.GroupLayout iconPanelLayout = new javax.swing.GroupLayout(iconPanel);
        iconPanel.setLayout(iconPanelLayout);
        iconPanelLayout.setHorizontalGroup(
            iconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 940, Short.MAX_VALUE)
        );
        iconPanelLayout.setVerticalGroup(
            iconPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 615, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(iconPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {


        MapFrame mapFrame = new MapFrame();

        mapFrame.setVisible(true);


        //        java.awt.EventQueue.invokeLater(new Runnable() {
//
//            public void run() {
//                new MapFrame().setVisible(true);
//            }
//        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel iconLabel;
    private javax.swing.JPanel iconPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        try {
            //        throw new UnsupportedOperationException("Not supported yet.");
            Thread.sleep(1000);
            this.repaint();
        } catch (InterruptedException ex) {
            Logger.getLogger(MapFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
