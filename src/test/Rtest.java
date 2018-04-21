package test;


import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;
import main.jdbc.OJDBC;
import main.pojo.MBR;
import main.query.irtree.RStarTree;
import main.tools.StaticMethod;
import main.tools.StaticValue;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Rtest {
    public static void main(String[] args) throws InterruptedException {
        String regex = "y2";
        String content = "mbr=Rectangle [x1=-118.199, y1=-9.09514, x2=0.0, y2=54.388]";
        String pattern = regex+"=[^,|\\]]+";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(content);
        if (m.find()){
            System.out.println(Double.parseDouble(m.group(0).replaceFirst(regex+"=","")));
        }
    }

}
