package main.query.irtree;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;

import java.util.Date;

/**
 * Created by wiyee on 2018/4/20.
 * irtree的构建以及mbr结构分析
 */
public class Main {
    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        RStarTree rStarTree = new RStarTree();
        System.out.println("load poi data...");
        RTree<String, Geometry> tree = rStarTree.loadPOIData();
        System.out.println("generate ir*-tree...");
        rStarTree.analyseIRtree(tree);
        System.out.println("save mbr&poi to oracle success...");
        Long endTime = System.currentTimeMillis();
        System.out.println((endTime-startTime)/1000f/60f + "min");
    }
}
