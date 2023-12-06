package src.data;

import java.util.HashMap;
import java.util.Map;

public class Data {

    public static Map<String, NodeData> data = new HashMap<>();

    static {
        data.put("server1", new NodeData(new String[0],"10.0.6.1", new String[]{"10.0.6.10"}));
        data.put("server2", new NodeData(new String[0],"10.0.0.1", new String[0]));
        data.put("client1", new NodeData(new String[0],"10.0.0.1", new String[0]));
        data.put("client2", new NodeData(new String[0],"10.0.5.1", new String[]{"10.0.5.20"}));
        data.put("client3", new NodeData(new String[0],"10.0.9.10", new String[0]));
        data.put("client4", new NodeData(new String[0],"10.0.3.1", new String[0]));
        data.put("client5", new NodeData(new String[0],"10.0.10.1", new String[0]));
        data.put("node1", new NodeData(new String[0],"10.0.2.10", new String[0]));
        data.put("node2", new NodeData(new String[0],"10.0.8.10", new String[0]));
        data.put("node3", new NodeData(new String[0],"10.0.1.2", new String[0]));
    }
}
