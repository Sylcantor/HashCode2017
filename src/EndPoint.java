import java.util.Arrays;

public class EndPoint {

    int endPointId;
    int dataCenterLatency;

    Integer[] cacheServers;

    EndPoint(int endPointId,int dataCenterLatency, int nbCacheServer) {
        this.endPointId = endPointId;
        this.dataCenterLatency = dataCenterLatency;
        this.cacheServers = new Integer[nbCacheServer];
        Arrays.fill(cacheServers,-1);
    }
}
