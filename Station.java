import javax.xml.bind.annotation.*;
import java.util.Arrays;

/**
 * Station
 * 保存站点的pojo类
 *
 * @author hengyumo
 * @version 1.0
 * @since 2019/7/21
 */
// 设置元素的顺序
@XmlRootElement(name = "station")
@XmlType(name = "stationType", propOrder = { "id", "name", "line", "neighborsNames" })
public class Station {

    private int id;

    private String name;

    // 一个站点可能会分别属于多条线路
    private String[] line;

    private String[] neighborsNames;

    private int[] neighborsIds;

    private Station[] neighbors;

    /**
     * 返回对应下标的邻接站点
     *
     * @param index 下标
     * @return Station
     */
    public Station neighbor(int index) {
        if (index > neighbors.length) {
            return null;
        }
        return neighbors[index];
    }

    public int getId() {
        return id;
    }

    // 映射为属性
    @XmlAttribute
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public String[] getLine() {
        return line;
    }

    @XmlAttribute
    public void setLine(String[] line) {
        this.line = line;
    }

    public String[] getNeighborsNames() {
        return neighborsNames;
    }


    // 设置list外部的包裹层
    @XmlElementWrapper(name = "neighbors")
    @XmlElement(name = "neighbor")
    public void setNeighborsNames(String[] neighborsNames) {
        this.neighborsNames = neighborsNames;
    }

    public int[] getNeighborsIds() {
        return neighborsIds;
    }

    // 设置此属性不需映射
    @XmlTransient
    public void setNeighborsIds(int[] neighborsIds) {
        this.neighborsIds = neighborsIds;
    }

    public Station[] getNeighbors() {
        return neighbors;
    }

    @XmlTransient
    public void setNeighbors(Station[] neighbors) {
        this.neighbors = neighbors;
    }

    public Station() {
    }

    public Station(int id, String name, String[] line) {
        this.id = id;
        this.name = name;
        this.line = line;
    }

    public Station(int id, String name, String[] line, String[] neighborsNames) {
        this.id = id;
        this.name = name;
        this.line = line;
        this.neighborsNames = neighborsNames;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", line=" + Arrays.toString(line) +
                ", neighborsNames=" + Arrays.toString(neighborsNames) +
                '}';
    }
}
