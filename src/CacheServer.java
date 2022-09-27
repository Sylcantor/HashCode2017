import java.util.Arrays;

public class CacheServer {
    int cacheServerId;
    int currentSpace;
    int [] endpointsLatency;
    int [] videos;

    CacheServer(int cacheServerId, int currentSpace, int nbEndpoints, int nbVideo){
        this.cacheServerId = cacheServerId;
        this.currentSpace = currentSpace;
        this.endpointsLatency = new int[nbEndpoints];
        this.videos = new int[nbVideo];
        Arrays.fill(endpointsLatency,-1);
        Arrays.fill(videos,-1);
    }
}
