package src.data;

import java.util.HashMap;
import java.util.Map;

public class Data {

    public static Map<String, NodeData> data = new HashMap<>();

    static {
        data.put("server1", new NodeData(new String[0],"10.0.8.21", new String[]{"10.0.8.20"}));
        data.put("server2", new NodeData(new String[0],"10.0.9.21", new String[0]));
        data.put("client1", new NodeData(new String[0],"10.0.0.20", new String[]{"10.0.0.21"}));
        data.put("client2", new NodeData(new String[0],"10.0.4.21", new String[]{"10.0.4.20"}));
        data.put("client3", new NodeData(new String[0],"10.0.1.21", new String[0]));
        data.put("client4", new NodeData(new String[0],"10.0.2.21", new String[0]));
        data.put("node1", new NodeData(new String[0],"10.0.6.21", new String[]{"10.0.2.1"}));
        data.put("node2", new NodeData(new String[0],"10.0.3.20", new String[0]));
        data.put("node3", new NodeData(new String[0],"10.0.7.21", new String[0]));
    }
}
