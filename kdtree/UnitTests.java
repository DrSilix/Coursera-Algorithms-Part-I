/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UnitTests {

    @Test
    void checkSizeSET() {
        PointSET set = new PointSET();
        set.insert(new Point2D(0.5, 0.5));
        assert set.size() == 1;
        set.insert(new Point2D(0.2, 0.5));
        set.insert(new Point2D(0.1, 0.1));
        set.insert(new Point2D(0.4, 0.5));
        assert set.size() == 4;
        set.insert(new Point2D(0.6, 0.8));
        set.insert(new Point2D(0.1, 0.1));
        set.insert(new Point2D(0.5, 0.2));
        assert set.size() == 6;
        set.insert(new Point2D(0.0, 0.0));
        set.insert(new Point2D(1.0, 1.0));
        set = new PointSET();
        assert set.size() == 0;
    }

    @Test
    void checkEmptySET() {
        PointSET set = new PointSET();
        assert set.size() == 0;
        assert !set.contains(new Point2D(0.5, 0.5));
        assertDoesNotThrow(() -> {set.draw();});
        assert set.isEmpty();
        assert set.nearest(new Point2D(0.5, 0.5)) == null;
        assert !set.range(new RectHV(0.0, 0.0, 1.0, 1.0)).iterator().hasNext();
    }

    @Test
    void checkFilledSET() {
        PointSET set = new PointSET();
        set.insert(new Point2D(0.5, 0.5));
        set.insert(new Point2D(0.2, 0.5));
        set.insert(new Point2D(0.1, 0.1));
        set.insert(new Point2D(0.4, 0.5));
        assert set.size() == 4;
        assert set.contains(new Point2D(0.4, 0.5));
        assert !set.contains(new Point2D(0.0, 0.0));
        assertDoesNotThrow(() -> {set.draw();});
        assert !set.isEmpty();
        assert set.nearest(new Point2D(0.1, 0.8)).compareTo(new Point2D(0.2, 0.5)) == 0;
        assert set.range(new RectHV(0.0, 0.0, 1.0, 1.0)).iterator().hasNext();
    }

    @Test
    void exceptionChecksSET(){
        PointSET set = new PointSET();
        assertThrows(IllegalArgumentException.class, () -> {set.insert(null);});
        assertThrows(IllegalArgumentException.class, () -> {set.contains(null);});
        assertThrows(IllegalArgumentException.class, () -> {set.range(null);});
        assertThrows(IllegalArgumentException.class, () -> {set.nearest(null);});
    }

    @Test
    void checkSizeKdTree() {
        KdTree kdtree = new KdTree();
        kdtree.insert(new Point2D(0.5, 0.5));
        assert kdtree.size() == 1;
        kdtree.insert(new Point2D(0.2, 0.5));
        kdtree.insert(new Point2D(0.1, 0.1));
        kdtree.insert(new Point2D(0.4, 0.5));
        assert kdtree.size() == 4;
        kdtree.insert(new Point2D(0.6, 0.8));
        kdtree.insert(new Point2D(0.1, 0.1));
        kdtree.insert(new Point2D(0.5, 0.2));
        assert kdtree.size() == 6;
        kdtree.insert(new Point2D(0.0, 0.0));
        kdtree.insert(new Point2D(1.0, 1.0));
        kdtree = new KdTree();
        assert kdtree.size() == 0;
    }

    @Test
    void nearestCallSET(){
        PointSET set = new PointSET();
        In in = new In("circle10.txt");
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            set.insert(p);
        }
        assert set.nearest(new Point2D(0.81, 0.30)).compareTo(new Point2D(0.975528, 0.345492)) == 0;
    }

    @Test
    void checkEmptyKdTree() {
        KdTree kdtree = new KdTree();
        assert kdtree.size() == 0;
        assert !kdtree.contains(new Point2D(0.5, 0.5));
        kdtree.draw();
        assert kdtree.isEmpty();
        assert kdtree.nearest(new Point2D(0.5, 0.5)) == null;
        assert !kdtree.range(new RectHV(0.0, 0.0, 1.0, 1.0)).iterator().hasNext();
    }

    @Test
    void checkFilledKdTree() {
        KdTree kdtree = new KdTree();
        kdtree.insert(new Point2D(0.5, 0.5));
        kdtree.insert(new Point2D(0.2, 0.5));
        kdtree.insert(new Point2D(0.1, 0.1));
        kdtree.insert(new Point2D(0.4, 0.5));
        assert kdtree.size() == 4;
        assert kdtree.contains(new Point2D(0.4, 0.5));
        assert !kdtree.contains(new Point2D(0.0, 0.0));
        assertDoesNotThrow(() -> {kdtree.draw();});
        assert !kdtree.isEmpty();
        assert kdtree.nearest(new Point2D(0.1, 0.8)).compareTo(new Point2D(0.2, 0.5)) == 0;
        assert kdtree.range(new RectHV(0.0, 0.0, 1.0, 1.0)).iterator().hasNext();
    }

    @Test
    void exceptionChecksKdTree(){
        KdTree kdtree = new KdTree();
        assertThrows(IllegalArgumentException.class, () -> {kdtree.insert(null);});
        assertThrows(IllegalArgumentException.class, () -> {kdtree.contains(null);});
        assertThrows(IllegalArgumentException.class, () -> {kdtree.range(null);});
        assertThrows(IllegalArgumentException.class, () -> {kdtree.nearest(null);});
    }

    @Test
    void nearestCallKdTree(){
        KdTree kdtree = new KdTree();
        In in = new In("circle10.txt");
        while (!in.isEmpty()) {
            double x = in.readDouble();
            double y = in.readDouble();
            Point2D p = new Point2D(x, y);
            kdtree.insert(p);
        }
        assert kdtree.nearest(new Point2D(0.81, 0.30)).compareTo(new Point2D(0.975528, 0.345492)) == 0;
    }
}
