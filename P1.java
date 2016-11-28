public class P1 {

    public static void main(String[] args) {
        PageRank pageRank = new PageRank();
        pageRank.readGraphFromInput();
        pageRank.removeSinkNode();
        pageRank.computePageRank();
    }
}
