import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * SubwayTest
 * 单元测试
 *
 * @author hengyumo
 * @version 1.0
 * @since 2019/7/22
 */
public class SubwayTest {

    private subway main;

    @Before
    public void setUp () throws Exception {
        main = new subway();
    }

    @After
    public void tearDown() throws Exception {

    }

    /**
     * 判断两个文件的内容是否相同
     *
     * @param resultFileName 结果
     * @param expectFileName 预期
     *
     * @return 是否相同
     */
    private boolean fileIsEquals(String resultFileName, String expectFileName) {

        boolean isEquals = false;
        List<String> result = new ArrayList<>();
        List<String> expect = new ArrayList<>();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(resultFileName)));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
            bufferedReader.close();
            bufferedReader = new BufferedReader(new FileReader(new File(expectFileName)));

            while((line = bufferedReader.readLine()) != null) {
                expect.add(line);
            }
            bufferedReader.close();

            isEquals = result.toString().equals(expect.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isEquals;
    }

    /**
     * 测试 -map
     */
    @Test
    public void test1() {
        main.call("-map src/subway.txt");
    }

    /**
     * 测试 -a，输出从线路的起始站点开始，依次输出该地铁线经过的所有站点，直到终点站
     * 并输出结果到目标文件
     */
    @Test
    public void test2() {
        main.call("-a 1号线 -map src/subway.txt -o test/result1.txt");
        Assert.assertTrue(fileIsEquals("test/result1.txt", "test/expect1.txt"));
    }
    @Test
    public void test3() {
        main.call("-a 3号线 -map src/subway.txt -o test/result2.txt");
        Assert.assertTrue(fileIsEquals("test/result2.txt", "test/expect2.txt"));
    }
    @Test
    public void test4() {
        main.call("-a 5号线 -map src/subway.txt -o test/result3.txt");
        Assert.assertTrue(fileIsEquals("test/result3.txt", "test/expect3.txt"));
    }
    @Test
    public void test5() {
        main.call("-a 6号线 -map src/subway.txt -o test/result4.txt");
        Assert.assertTrue(fileIsEquals("test/result4.txt", "test/expect4.txt"));
    }

    /**
     * 测试 -b，计算从出发到目的站点之间的最短（经过的站点数最少）路线，并输出经过的站点的个数和路径（包括出发与目的站点）。
     * 注意，如果需要换乘，请在换乘站的下一行输出换乘的线路
     * 并输出结果到目标文件
     */
    @Test
    public void test6() {
        main.call("-b 洪湖里 复兴路 -map src/subway.txt -o test/result5.txt");
        Assert.assertTrue(fileIsEquals("test/result5.txt", "test/expect5.txt"));
    }
    @Test
    public void test7() {
        main.call("-b 梅江道 复兴路 -map src/subway.txt -o test/result6.txt");
        Assert.assertTrue(fileIsEquals("test/result6.txt", "test/expect6.txt"));
    }
    @Test
    public void test8() {
        main.call("-b 梅江道 天津站 -map src/subway.txt -o test/result7.txt");
        Assert.assertTrue(fileIsEquals("test/result7.txt", "test/expect7.txt"));
    }
    @Test
    public void test9() {
        main.call("-b 会展中心 杨伍庄 -map src/subway.txt -o test/result8.txt");
        Assert.assertTrue(fileIsEquals("test/result8.txt", "test/expect8.txt"));
    }
    @Test
    public void test10() {
        main.call("-b 高庄子 民权门 -map src/subway.txt -o test/result9.txt");
        Assert.assertTrue(fileIsEquals("test/result9.txt", "test/expect9.txt"));
    }

    /**
     * 测试Util类
     */
    @Test
    public void test11() {
//        Util.enterSubway();
        Util.testJAXB();
        Util.testJAXB2();
        SubwayPojo subwayPojo1 = Util.readSubway("src/subway.txt");
        System.out.println(subwayPojo1);

//        Util.enterLines();

        SubwayPojo subwayPojo2 = Util.readSubway("src/subway.txt");
        Util.saveSubway(subwayPojo2, "src/test2.xml");
        System.out.println(subwayPojo2);
    }
}
