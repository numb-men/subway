import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Util
 * 工具类
 *
 * @author hengyumo
 * @version 1.0
 * @since 2019/7/21
 */
public class Util {

    private final static Integer[] LINES_ID = { 1, 2, 3, 5, 6, 9 };

    public static void main(String[] args) {
//        Util.enterSubway();
//        Util.testJAXB();
//        Util.testJAXB2();
//        SubwayPojo subway = Util.readSubway("src/test.xml");
//        System.out.println(subway);

        Util.enterLines();

//        SubwayPojo subway = Util.readSubway("subway.txt");
//        System.out.println(subway);
    }

    /**
     * 从命令行录入所有的线路，相比enterSubway输入更简单
     */
    public static void enterLines() {
        System.out.println("线路录入程序，欢迎您的使用，请设置您预期生成的文件名：");
        Scanner in = new Scanner(System.in);
        String fileName = in.nextLine();

        System.out.println("请根据提示录入信息，录入完成后将会自动生成" + fileName + "文件。");
        System.out.println("请按照线路的站点顺序进行录入。在每一条线路录入完时输入/end line 进行下一条线路的输入。" +
                "所有线路输入完成请输入/end 结束录入");

        SubwayPojo subway = new SubwayPojo();
        List<Line> lines = new ArrayList<>();
        List<Integer> LINES = Arrays.asList(LINES_ID);

        int id = 0;
        while (true) {
            System.out.println("请输入线路号：（快捷输入1代表1号线，天津地铁仅支持"
                    + Arrays.toString(LINES_ID) + "号线。所有线路输入完成请输入/end 结束录入。）");

            String lineName = in.nextLine();

            if ("/end".equals(lineName)) {
                break;
            }

            Line line = new Line();
            lines.add(line);

            try{
                if (LINES.contains(Integer.parseInt(lineName))){
                    lineName = lineName + "号线";
                    line.setName(lineName);
                }
            }
            catch (NumberFormatException e) {}

            Station prePreStation = null;
            Station preStation = null;
            while (true) {

                System.out.println("请按站点顺序录入站点名：（输入/end line结束该条站点的录入）");

                String stationName = in.nextLine();

                if ("/end line".equals(stationName)) {
                    System.out.println(lineName + "结束录入。");
                    if (prePreStation != null) {
                        preStation.setNeighborsNames(new String[] { prePreStation.getName() });
                    }
                    break;
                }

                Station station = new Station(id, stationName, new String[] { lineName });
                if (prePreStation != null) {
                    String[] neighborsNames = { prePreStation.getName(), stationName };
                    preStation.setNeighborsNames(neighborsNames);
                }
                else if (preStation != null) {
                    preStation.setNeighborsNames(new String[] { stationName });
                }
                prePreStation = preStation;
                preStation = station;
                line.add(station);
                id ++;
            }
        }

        subway.setLines(lines);
        subway.initByLines();
        Util.saveSubway(subway, fileName);
        System.out.println(fileName + "已经生成，谢谢您的使用！Bye！");
    }

    /**
     * 从命令行读取subway，并设生成对应的文件
     */
    public static void enterSubway() {

        System.out.println("站点录入程序，欢迎您的使用，请设置您预期生成的文件名：");
        Scanner in = new Scanner(System.in);
        String fileName = in.nextLine();

        System.out.println("请根据提示录入信息，录入完成后将会自动生成" + fileName + "文件。");
        System.out.println("请按照线路的站点顺序进行录入。");

        SubwayPojo subway = new SubwayPojo();
        List<Integer> LINES = Arrays.asList(LINES_ID);
        int id = 0;
        while (true) {

            System.out.println("请输入 站点名：（输入 /end 结束录入）");
            String name = in.nextLine();

            if ("/end".equals(name)) {
                break;
            }

            System.out.println("请输入 站点线路：(便捷输入，1代表一号线）");
            String[] lines = in.nextLine().split(" ");
            for (int i = 0; i < lines.length; i++) {
                try{
                    if (LINES.contains(Integer.parseInt(lines[i]))){
                        lines[i] = lines[i] + "号线";
                    }
                }
                catch (NumberFormatException e) {}
            }

            System.out.println("请输入 站点的邻接站点名称列表：（以空格分离）");
            String[] neighbors = in.nextLine().split(" ");

            Station station = new Station(id, name, lines, neighbors);
            subway.addStation(station);

            id ++;
        }

        System.out.println(subway);
        Util.saveSubway(subway, fileName);
        System.out.println(fileName + "已经生成，谢谢您的使用！Bye！");
    }

    /**
     * 将subway保存到文件
     *
     * @param subway 地铁
     * @param path 文件路径
     */
    public static void saveSubway(SubwayPojo subway, String path) {

        JAXBContext context = null;

        try {
            context = JAXBContext.newInstance(SubwayPojo.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            PrintStream out = new PrintStream(new File(path));
            marshaller.marshal(subway, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中读取subway
     *
     * @param path 文件路径
     *
     * @return SubwayPojo 地铁
     */
    public static SubwayPojo readSubway(String path) {

        JAXBContext context = null;
        SubwayPojo subway = null;

        try {
            context = JAXBContext.newInstance(SubwayPojo.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            InputStream stream = new FileInputStream(new File(path));
            subway = (SubwayPojo) unmarshaller.unmarshal(stream);
            subway.initByLines();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return subway;
    }

    /**
     * 测试使用JAXB存对象到xml文件
     */
    public static void testJAXB() {
        // 获取JAXB的上下文环境，需要传入具体的 Java bean -> 这里使用Station
        JAXBContext context = null;
        Station station = new Station(1, "站点1", new String[]{"1号线", "2号线"}, new String[] {"站点1", "站点3"});
        String[] neighborsNames = { "站点2", "站点3" };
        station.setNeighborsNames(neighborsNames);

        try {
            context = JAXBContext.newInstance(Station.class);

            // 创建 Marshaller 实例
            Marshaller marshaller = context.createMarshaller();
            // 设置转换参数 -> 这里举例是告诉序列化器是否格式化输出
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            // 构建输出环境 -> 这里使用标准输出，输出到控制台Console
            PrintStream out = new PrintStream(new File("src/test.xml"));
            // 将所需对象序列化 -> 该方法没有返回值
            marshaller.marshal(station, out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试 读XML文件到对象
     */
    public static void testJAXB2() {
        // 获取JAXB的上下文环境，需要传入具体的 Java bean -> 这里使用Station
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(Station.class);
            // 创建 UnMarshaller 实例
            Unmarshaller unmarshaller = context.createUnmarshaller();
            // 加载需要转换的XML数据 -> 这里使用InputStream，还可以使用File，Reader等
            InputStream stream = new FileInputStream(new File("src/test.xml"));
            // 将XML数据序列化 -> 该方法的返回值为Object基类，需要强转类型
            Station station = (Station) unmarshaller.unmarshal(stream);
            // 将结果打印到控制台
            System.out.println(station);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
