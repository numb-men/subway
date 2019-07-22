import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Line
 * 保存线路的pojo类
 *
 * @author hengyumo
 * @version 1.0
 * @since 2019/7/21
 */

@XmlType(name = "lineType", propOrder = { "name", "stations" })
public class Line {

    private String name;

    private List<Station> stations;

    /**
     * 向线路中添加站点
     *
     * @param station 站点
     */
    public void add(Station station) {
        stations.add(station);
    }

    /**
     * 让线路中的站点根据Id进行排序
     */
    public void sortById() {
        stations.sort(new Comparator<Station>() {
            @Override
            public int compare(Station o1, Station o2) {
                return o1.getId() - o2.getId();
            }
        });
    }

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public List<Station> getStations() {
        return stations;
    }

    @XmlElementWrapper(name = "stations")
    @XmlElement(name = "station")
    public void setStations(List<Station> stations) {
        this.stations = stations;
    }

    public Line() {
        this.stations = new ArrayList<>();
    }


    @Override
    public String toString() {
        return "Line{" +
                "name='" + name + '\'' +
                ", stations=" + stations +
                '}';
    }
}
