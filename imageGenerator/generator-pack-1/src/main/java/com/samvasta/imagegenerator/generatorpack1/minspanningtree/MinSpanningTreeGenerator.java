package com.samvasta.imagegenerator.generatorpack1.minspanningtree;

import com.samvasta.imageGenerator.common.graphics.colors.ColorUtil;
import com.samvasta.imageGenerator.common.interfaces.SimpleGenerator;
import org.apache.commons.math3.random.MersenneTwister;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;

public class MinSpanningTreeGenerator extends SimpleGenerator
{
    static Image TEXTURE = null;
    static Image[] SPLATS = null;

    double v;

    public boolean goodDist(Polygon polys[], int num){

        for(int i = 0; i < num; i++){
            if(polys[i].getBounds().intersects(polys[num].getBounds())) return false;
//			Point p1 = getCenter(polys[i]), p2 = getCenter(polys[num]);
//			for(int j=0;j<polys[i].npoints;j++){
//				Point ip1 = new Point(polys[i].xpoints[j], polys[i].ypoints[j]);
//				for(int k =0; k < polys[num].npoints; k++){
//					Point ip2 = new Point(polys[num].xpoints[k], polys[num].ypoints[k]);
//					if(Point.distance(ip1.x, ip1.y, ip2.x, ip2.y) < MIN_DIST) return false;
//				}
//			}
        }
        return true;
    }


    @Override
    public void generateImage(Map<String, Object> settings, Graphics2D g, Dimension imageSize, MersenneTwister random) {
        double variation = random.nextDouble();
//        if(TEXTURE == null){
//            try {
//                ClassLoader cl = this.getClass().getClassLoader();
//                TEXTURE = ImageIO.read(cl.getResourceAsStream("vignetteTex.png")).getScaledInstance(imageSize.width, imageSize.height, Image.SCALE_SMOOTH);
////				System.out.println("Read correctly!");
//            } catch (Exception e) {
//                try {
//                    TEXTURE = ImageIO.read(new File("vignetteTex.png")).getScaledInstance(imageSize.width, imageSize.height, Image.SCALE_SMOOTH);
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                e.printStackTrace();
//            }
//        }

        int maxSize = (int)imageSize.getWidth()/15;
        int minSize = (int)imageSize.getWidth()/80;
        int strokeWeight = (int)(imageSize.getWidth()/((60 * Math.abs(random.nextFloat()-variation))+20));

        v = variation;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color bg = ColorUtil.getHSB(random.nextFloat(), (float)Math.max(variation*(1-variation),  0.35f+(random.nextFloat()-variation)), (float)Math.min(0.5f, Math.max(variation, 0.2f)));

        Polygon[] polys = new Polygon[(int)(((int)(variation*100) % 6 <= 1 ? 15 : 8) * variation*variation) + 3];
        for(int i =0 ;i < polys.length; i++){
            do{
                polys[i] = getRegularPolygon(random.nextInt((int)(10 * v)+1) + 3, random.nextInt((int)(v * (maxSize-minSize)+1)) + minSize);
                polys[i].translate(random.nextInt((int)imageSize.getWidth()*3/5) + (int)imageSize.getWidth()/5, random.nextInt((int)imageSize.getHeight()*3/5) + (int)imageSize.getHeight()/5);

            }while(!goodDist(polys, i));
        }

        Connection[] connections = getMST(polys);

        g.setColor(bg);
        g.fillRect(0, 0, (int)imageSize.getWidth(), (int)imageSize.getHeight());

//        if(TEXTURE != null){
//            g.drawImage(TEXTURE, 0, 0, null);
//        }

        Color lines = ColorUtil.getNextInSequence(bg, v);
        lines = ColorUtil.shift(lines, 0, -0.4f, 0.5f);
        g.setColor(new Color(lines.getRed(), lines.getGreen(), lines.getBlue(), 128));
        g.setStroke(new BasicStroke(strokeWeight/4));
        for(Connection c: connections){
            Point center1 = getCenter(c.p1);
            Point center2 = getCenter(c.p2);
            g.draw(new Line2D.Float(center1.x, center1.y, center2.x, center2.y));
        }

        Point centerOfPolys = new Point(0,0);
        Color polyCol = ColorUtil.getNextInSequence(ColorUtil.shift(bg, 0, 0.2f, (float)variation/3f), variation);
        for(Polygon p: polys){
            centerOfPolys.x += getCenter(p).x;
            centerOfPolys.y += getCenter(p).y;
        }
        centerOfPolys.x /= polys.length;
        centerOfPolys.y /= polys.length;
        int boundsx = 0, boundsy = 0;
        for(Polygon p: polys){
            Point center = getCenter(p);
            if(Math.abs(center.y - centerOfPolys.y) > boundsy)
                boundsy = Math.abs(center.y - centerOfPolys.y);
            if(Math.abs(center.x - centerOfPolys.x) > boundsx)
                boundsx = Math.abs(center.x - centerOfPolys.x);
        }


        Color next = ColorUtil.getNextInSequence(polyCol, variation);
        g.setColor(new Color(next.getRed(), next.getGreen(), next.getBlue(), 64));
        g.setStroke(new BasicStroke(strokeWeight));
        g.draw(new Ellipse2D.Float(centerOfPolys.x-Math.max(boundsx, boundsy), centerOfPolys.y-Math.max(boundsx, boundsy), Math.max(boundsx, boundsy)*2, Math.max(boundsx, boundsy)*2));
        g.setColor(g.getColor().darker().darker());
        g.fill(new Ellipse2D.Float(centerOfPolys.x-Math.max(boundsx, boundsy)+10, centerOfPolys.y-Math.max(boundsx, boundsy)+10, Math.max(boundsx, boundsy)*2-20, Math.max(boundsx, boundsy)*2-20));

        LinkedList<Polygon> polyList = new LinkedList<Polygon>();
        for(Connection c: connections){
            Polygon p;
            if(!polyList.contains((p = c.p1))){
                polyList.add(p);
                g.setColor(polyCol);
                g.fillPolygon(rotatePolygon(p, getCenter(p), 180 + Math.toDegrees(Math.atan2(getCenter(c.p1).y - getCenter(c.p2).y, getCenter(c.p1).x-getCenter(c.p2).x))));
                polyCol = ColorUtil.getNextInSequence(polyCol, variation);
            }
            if(!polyList.contains((p = c.p2))){
                polyList.add(p);
                g.setColor(polyCol);
                g.fillPolygon(rotatePolygon(p, getCenter(p), 180 + Math.toDegrees(Math.atan2(getCenter(c.p1).y - getCenter(c.p2).y, getCenter(c.p1).x-getCenter(c.p2).x))));
                polyCol = ColorUtil.getNextInSequence(polyCol, variation);
            }
            if(polyList.contains(c.p1) && polyList.contains(c.p1) && random.nextDouble() < variation*variation){
                if(random.nextDouble() < variation){
                    p = c.p1;
                }
                else{
                    p = c.p2;
                }
                next = ColorUtil.getNextInSequence(polyCol, variation);
                g.setColor(new Color(next.getRed(), next.getGreen(), next.getBlue(), 192));
                g.setStroke(new BasicStroke(strokeWeight/5));
                boundsx = 0;
                boundsy = 0;
                Point center = getCenter(p);
                for(int i =0 ; i < p.npoints; i++){
                    if(Math.abs(p.ypoints[i] - center.y) > boundsy)
                        boundsy = Math.abs(p.ypoints[i] - center.y);
                    if(Math.abs(p.xpoints[i] - center.x) > boundsx)
                        boundsx = Math.abs(p.xpoints[i] - center.x);
                }
                boundsx *= (1+(1-variation));
                boundsy *= (1+(1-variation));
                if(random.nextDouble() * variation < 0.7)
                    g.draw(new Ellipse2D.Float(center.x-Math.max(boundsx, boundsy), center.y-Math.max(boundsx, boundsy), Math.max(boundsx, boundsy)*2, Math.max(boundsx, boundsy)*2));
                else
                    g.draw(new Rectangle2D.Float(center.x-Math.max(boundsx, boundsy), center.y-Math.max(boundsx, boundsy), Math.max(boundsx, boundsy)*2, Math.max(boundsx, boundsy)*2));
            }
        }
    }

    public Connection[] getMST(Polygon[] polygons){
        ArrayList<Connection> connections = new ArrayList<Connection>();
        ArrayList<Connection> toReturn = new ArrayList<Connection>();
        for(Polygon p1: polygons){
            for(Polygon p2: polygons){
                if(p1.equals(p2)) continue;
                Connection c = new Connection(p1, p2);
                if(!connections.contains(c))
                    connections.add(c);
            }
        }

        ArrayList<Polygon> inTree = new ArrayList<Polygon>();
        LinkedList<Polygon> notInTree = new LinkedList<Polygon>();
        for(Polygon p : polygons)
            notInTree.add(p);

        inTree.add(notInTree.removeFirst());
        while(!notInTree.isEmpty()){
            Connection bestConnection = null;
            double minWeight = Double.MAX_VALUE;
            for(Connection c: connections){
                if(c.connectsTo(inTree) && !(inTree.contains(c.p1) && inTree.contains(c.p2))){
                    if(c.weight < minWeight){// || rand.nextDouble() < v){
                        minWeight = c.weight;
                        bestConnection = c;
                    }
                }
            }
            if(bestConnection != null && connections.remove(bestConnection)){
                Polygon outside = null;
                for(Polygon p: inTree){
                    if(bestConnection.p1.equals(p)){
                        outside = bestConnection.p2;
                        break;
                    }
                    if(bestConnection.p2.equals(p)){
                        outside = bestConnection.p1;
                        break;
                    }
                }
                if(outside != null && notInTree.remove(outside)){
                    inTree.add(outside);
                    toReturn.add(bestConnection);
                }
            }
        }

        return toReturn.toArray(new Connection[toReturn.size()]);
    }

    public Polygon getRegularPolygon(int n, double radius){
        double angleStep = 2d*Math.PI/(double)n;
        int x[] = new int[n];
        int y[] = new int[n];
        double angle = 0;
        for(int i = 0; i < n; i++){
            x[i] = (int)Math.round(radius * Math.cos(angle));
            y[i] = (int)Math.round(radius * Math.sin(angle));
            angle+=angleStep;
        }

        return new Polygon(x, y, n);
    }

    public Polygon rotatePolygon(Polygon p, Point center, double angle){
        Point[] points = new Point[p.npoints], newPoints = new Point[points.length];
        for(int i = 0; i < points.length; i++)
            points[i] = new Point(p.xpoints[i], p.ypoints[i]);

        for(Point pt: points){
            pt = rotatePoint(pt, center, angle);
        }

        int[] xpoints = new int[points.length], ypoints = new int[points.length];
        for(int i = 0; i < points.length; i++){
            xpoints[i] = points[i].x;
            ypoints[i] = points[i].y;
        }
        return new Polygon(xpoints, ypoints, newPoints.length);
    }

    public Point rotatePoint(Point pt, Point center, double angleDeg)
    {
        double angleRad = (angleDeg/180)*Math.PI;
        double cosAngle = Math.cos(angleRad );
        double sinAngle = Math.sin(angleRad );
        double dx = (pt.x-center.x);
        double dy = (pt.y-center.y);

        pt.x = center.x + (int) (dx*cosAngle-dy*sinAngle);
        pt.y = center.y + (int) (dx*sinAngle+dy*cosAngle);
        return pt;
    }

    public Point getCenter(Polygon p){
        double x=0, y=0;
        for(int xpoint: p.xpoints){
            x+=xpoint;
        }
        for(int ypoint: p.ypoints){
            y+=ypoint;
        }
        x/=(double)p.npoints;
        y/=(double)p.npoints;
        return new Point((int)Math.round(x), (int)Math.round(y));
    }

    class Connection{
        Polygon p1,p2;
        double weight;
        public Connection(Polygon p1, Polygon p2){
            this.p1 = p1;
            this.p2 = p2;
            weight = Point.distance(getCenter(p1).x, getCenter(p1).y, getCenter(p2).x, getCenter(p2).y);
        }
        @Override
        public boolean equals(Object obj){
            if(obj instanceof Connection){
                Connection that = (Connection)obj;
                return (this.p1.equals(that.p1) && this.p2.equals(that.p2)) || (this.p1.equals(that.p2) && this.p2.equals(that.p1));
            }
            else return false;
        }
        public boolean connectsTo(List<Polygon> polys){
            for(Polygon p: polys){
                if(p1.equals(p) || p2.equals(p)) return true;
            }
            return false;
        }
    }
}
