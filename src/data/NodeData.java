package src.data;

public class NodeData {

    private String[] ips;
    private String[] adj;
    private String prox;

    public NodeData(String[] adj, String prox, String[] ips){
        this.adj = adj;
        this.prox = prox;
        this.ips = ips;
    }

    public String[] getAdj(){
        return this.adj;
    }

    public String getProx(){
        return this.prox;
    }

    public String[] getIps(){
        return this.ips;
    }

}
