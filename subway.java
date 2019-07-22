import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * subway
 * 主程序
 *
 * @author hengyumo
 * @version 1.0
 * @since 2019/7/21
 */
public class subway {

    // 输入文件
    private String inputFileName;

    // 线路名称
    private String lineName;

    // 结果输出文件
    private String outputFileName;

    // 起始站
    private String stationStart;

    // 终点站
    private String stationEnd;

    // 运行的功能类型
    private int functionType;

    // 从线路的起始站点开始，依次输出该地铁线经过的所有站点，直到终点站
    private final static int OUTPUT_LINE = 0;

    // 计算从出发到目的站点之间的最短（经过的站点数最少）路线，并输出经过的站点的个数和路径（包括出发与目的站点）。
    // 注意，如果需要换乘，请在换乘站的下一行输出换乘的线路
    private final static int FIND_WAY = 1;

    // 地铁信息
    private SubwayPojo subwayPojo;


    /**
     * 通过命令字符串进行调用，方便测试
     *
     * @param cmdArgs 命令字符串
     */
    public void call(String cmdArgs) {
        String[] args = cmdArgs.split(" ");
        loadArgs(args);
        doFunction();
    }

    /**
     * 解析命令行参数
     *
     * @param args 命令行参数
     */
    private void loadArgs(String[] args) {
        if (args.length > 0){
            for (int i = 0; i < args.length; i++){
                switch (args[i]){
                    case "-map":
                        inputFileName = args[++i];
                        break;
                    case "-a":
                        functionType = OUTPUT_LINE;
                        lineName = args[++i];
                        break;
                    case "-o":
                        outputFileName = args[++i];
                        break;
                    case "-b":
                        functionType = FIND_WAY;
                        stationStart = args[++i];
                        stationEnd = args[++i];
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 根据选择的功能类型执行对应的功能
     */
    private void doFunction() {

        // 前提要有对应的地铁配置文件
        if (inputFileName != null && new File(inputFileName).exists()) {
            subwayPojo = Util.readSubway(inputFileName);
            // 要有对应的输出文件
            if (outputFileName != null) {
                switch (functionType) {
                    case OUTPUT_LINE:
                        if (lineName != null) {
                            outputLine();
                        }
                        break;
                    case FIND_WAY:
                        if (stationStart != null && stationEnd != null) {
                            findWay();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 从线路的起始站点开始，依次输出该地铁线经过的所有站点，直到终点站
     * 并输出结果到目标文件
     *
     * 命令：java subway -a 1号线 -map subway.txt -o station.txt
     */
    private void outputLine() {
        try {
            PrintWriter out = new PrintWriter(new OutputStreamWriter(
                    new FileOutputStream(outputFileName), StandardCharsets.UTF_8));
            Map<String, Line> lineMap = subwayPojo.getLineMap();
            if (lineMap.containsKey(lineName)) {
                Line line = lineMap.get(lineName);
                List<Station> stations = line.getStations();
                List<String> stationsNames = new ArrayList<>();
                for (Station station : stations) {
                    stationsNames.add(station.getName());
                }
                out.print(String.join("\n", stationsNames));
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算从出发到目的站点之间的最短（经过的站点数最少）路线，并输出经过的站点的个数和路径（包括出发与目的站点）。
     * 注意，如果需要换乘，请在换乘站的下一行输出换乘的线路
     * 并输出结果到目标文件
     *
     * 命令：java subway -b 洪湖里 复兴路 -map subway.txt -o routine.txt
     */
    private void findWay() {
        Map<String, Station> stationMap = subwayPojo.getStationMap();

        // 保存是否访问过
        Map<String, Boolean> visited = new HashMap<>();
        // 保存距离
        Map<String, Integer> distance = new HashMap<>();
        // 保存路径
        Map<String, ArrayList<Station>> rows = new HashMap<>();

        Station start = stationMap.get(stationStart);

        visited.put(start.getName(), true);
        distance.put(start.getName(), 0);
        ArrayList<Station> startRow = new ArrayList<>();
        startRow.add(start);
        rows.put(start.getName(), startRow);

        // 初始化
        for (Station neighbor : start.getNeighbors()) {
            distance.put(neighbor.getName(), 1);
            visited.put(neighbor.getName(), false);
            rows.put(neighbor.getName(), new ArrayList<>(rows.get(start.getName())));
        }

        while (true) {
            Station unVisited = null;
            // 找邻接距离最短的一个节点 未访问过的节点
            for (Map.Entry<String, Integer> entry : distance.entrySet()) {
                if (unVisited == null) {
                    if (! visited.get(entry.getKey())) {
                        unVisited = stationMap.get(entry.getKey());
                    }
                }
                else {
                    if (entry.getValue() < distance.get(unVisited.getName())
                            && ! visited.get(entry.getKey())) {
                        unVisited = stationMap.get(entry.getKey());
                    }
                }
            }

            if (unVisited == null) {
                // 都访问过了
                break;
            }

            // 填入路径
            ArrayList<Station> row = rows.get(unVisited.getName());
            row.add(unVisited);

            // 距离重置
            for (Station neighbor : unVisited.getNeighbors()) {
                if (! visited.containsKey(neighbor.getName())) {
                    // 新站点
                    visited.put(neighbor.getName(), false);
                    distance.put(neighbor.getName(), distance.get(unVisited.getName()) + 1);
                    rows.put(neighbor.getName(), new ArrayList<>(row));
                }
                else {
                    if (distance.get(neighbor.getName()) > distance.get(unVisited.getName()) + 1) {
                        // 替换距离
                        distance.put(neighbor.getName(), distance.get(unVisited.getName()) + 1);
                        // 替换线路
                        rows.put(neighbor.getName(), new ArrayList<>(row));
                    }
                }
            }

            // 设置访问过
            visited.put(unVisited.getName(), true);
        }

        if (rows.containsKey(stationEnd)) {
            try {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(
                        new FileOutputStream(outputFileName), StandardCharsets.UTF_8));

                ArrayList<Station> row = rows.get(stationEnd);
                out.println(row.size());
                for (int i = 0; i < row.size() ; i++) {
                    Station station = row.get(i);

                    if (i != row.size() - 1) {
                        out.println(station.getName());
                    }
                    else {
                        // 防止文件末尾的空行
                        out.print(station.getName());
                    }

                    if (station.getLine().length > 1 && i < row.size() - 1 && i > 0) {
                        // 需要换乘（不是最后一站）
                        // 前一站和后一站不存在交集线路，则该点是换乘点
                        Set<String> preStationLines = new HashSet<>(Arrays.asList(row.get(i - 1).getLine()));
                        Set<String> nextStationLines = new HashSet<>(Arrays.asList(row.get(i + 1).getLine()));
                        preStationLines.retainAll(nextStationLines);
                        if (preStationLines.size() == 0) {
                            // 打印换乘线路
                            out.println(row.get(i + 1).getLine()[0]);
                        }
                    }
                }
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("路径不存在");
        }
    }

    public static void main(String[] args) {
        subway main = new subway();
        main.loadArgs(args);
        main.doFunction();
    }

}
