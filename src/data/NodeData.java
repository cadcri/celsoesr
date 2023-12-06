package src.data;

public class NodeData {

    private String[] adj;
    private String prox;

    public NodeData(String[] adj, String prox){
        this.adj = adj;
        this.prox = prox;
    }

    public String[] getAdj(){
        return this.adj;
    }

    public String getProx(){
        return this.prox;
    }

}
