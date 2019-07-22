import javax.xml.bind.annotation.*;
import java.util.*;

/**
 * SubwayPojo
 * 地铁站pojo，因为和主程序subway重名，所以只能改名subwayPojo
 *
 * @author hengyumo
 * @version 1.0
 * @since 2019/7/21
 */

// 将类映射为xml全局元素，也就是根元素
@XmlRootElement(name = "subway")
@XmlType(name = "subwayType", propOrder = { "lines" })
public class SubwayPojo {

    private List<Station> stations;

    private Map<String, Station> stationMap;

    private List<Line> lines;

    private Map<String, Line> lineMap;

    /**
     * 获取对应下标的线路
     *
     * @param index 下标
     * @return Line
     */
    public Line index(int index) {
        return lines.get(index);
    }

    /**
     * 添加站点
     *
     * @param station 站点
     */
    public void addStation(Station station) {
        this.stations.add(station);
        this.stationMap.put(station.getName(), station);

        // 添加到线路中
        for (String lineName : station.getLine()) {

            if (lineMap.containsKey(lineName)) {
                lineMap.get(lineName).add(station);
            }
            else {
                Line line = new Line();
                line.setName(lineName);
                line.add(station);
                lines.add(line);
                lineMap.put(lineName, line);
            }
        }
    }

    /**
     * 根据内部读入的Lines初始化其余属性，并将地铁的所有线路按照Id进行排序
     */
    public void initByLines() {
        this.stations = new ArrayList<>();
        this.stationMap = new HashMap<>();
        this.lineMap = new HashMap<>();

        for (Line line : lines) {
            String lineName = line.getName();

            lineMap.put(lineName, line);

            for (Station station : line.getStations()) {

                String stationName = station.getName();

                if (! stationMap.containsKey(stationName)) {

                    stationMap.put(stationName, station);
                    stations.add(station);
                }
                else {
                    Station oldStation = stationMap.get(stationName);

                    // 去重合并，对于换乘节点可以设置多条线路
                    Set<String> lineNameSet = new HashSet<>(Arrays.asList(oldStation.getLine()));
                    lineNameSet.addAll(Arrays.asList(station.getLine()));

                    String[] newLinesNames = lineNameSet.toArray(new String[0]);
                    oldStation.setLine(newLinesNames);

                    // 去重合并，对于换乘节点有更多个邻接站点
                    Set<String> neighBorNamesSet = new HashSet<>(Arrays.asList(oldStation.getNeighborsNames()));
                    neighBorNamesSet.addAll(Arrays.asList(station.getNeighborsNames()));

                    String[] newNeighborsNames = neighBorNamesSet.toArray(new String[0]);
                    oldStation.setNeighborsNames(newNeighborsNames);

                    // 另外一条线的换乘节点
                    station.setLine(oldStation.getLine());
                    station.setNeighborsNames(oldStation.getNeighborsNames());
                }
            }
        }

        // 设置station的邻接站点
        for (Line line : lines) {
            for (Station station : line.getStations()) {
                Station[] neighbors = new Station[station.getNeighborsNames().length];
                int i = 0;
                for (String neighborName : station.getNeighborsNames()) {
                    neighbors[i ++] = stationMap.get(neighborName);
                }
                station.setNeighbors(neighbors);
            }
        }

        // 对线路进行排序，保证顺序
        sortLines();
    }

    /**
     * 将地铁的所有线路按照Id进行排序
     * 将线路按照地铁线路名进行排序
     */
    public void sortLines() {
        for (Line line : lines) {
            line.sortById();
        }
        lines.sort(new Comparator<Line>() {
            @Override
            public int compare(Line o1, Line o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
    }

    public List<Station> getStations() {
        return stations;
    }

    @XmlTransient
    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public Map<String, Station> getStationMap() {
        return stationMap;
    }

    @XmlTransient
    public void setStationMap(Map<String, Station> stationMap) {
        this.stationMap = stationMap;
    }

    public List<Line> getLines() {
        return lines;
    }

    @XmlElementWrapper(name = "lines")
    @XmlElement(name = "line")
    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public Map<String, Line> getLineMap() {
        return lineMap;
    }

    @XmlTransient
    public void setLineMap(Map<String, Line> lineMap) {
        this.lineMap = lineMap;
    }

    public SubwayPojo() {
        this.stations = new ArrayList<>();
        this.stationMap = new HashMap<>();
        this.lines = new ArrayList<>();
        this.lineMap = new HashMap<>();
    }

    @Override
    public String toString() {
        return "SubwayPojo{" +
                "stations=" + stations +
                ", stationMap=" + stationMap +
                ", lines=" + lines +
                ", lineMap=" + lineMap +
                '}';
    }
}
