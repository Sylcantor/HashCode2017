import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Parser {
    int numberOfVideos;
    int numberOfEndpoints;
    int numberOfRequests;
    int numberOfCacheServers;
    int cacheServerCapacity;
    int k = 0;

    String file;
    Video[] videos;
    EndPoint[] endPoints;
    CacheServer[] cacheServers;
    Request[] requests;

    Parser(String file){
        this.file = file;
    }

    public void setupData() throws IOException {
        //a changer par System.in
        byte[] encoded = Files.readAllBytes(Paths.get(file));
        String fileContent = new String(encoded, StandardCharsets.UTF_8);
        String[] lines = fileContent.split("\n");
        String[] line = lines[k].split(" "); k++;
        numberOfVideos = Integer.parseInt(line[0]);
        numberOfEndpoints = Integer.parseInt(line[1]);
        numberOfRequests = Integer.parseInt(line[2]);
        numberOfCacheServers = Integer.parseInt(line[3]);
        cacheServerCapacity = Integer.parseInt(line[4]);

        line = lines[k].split(" "); k++;
        videos = new Video[numberOfVideos];
        for(int i=0;i<numberOfVideos;i++){
            videos[i] = new Video(i,Integer.parseInt(line[i]));
        }


        endPoints = new EndPoint[numberOfEndpoints];
        for(int i=0;i<numberOfEndpoints;i++){
            line = lines[k].split(" "); k++;
            int datacenterLatency = Integer.parseInt(line[0]);
            int numberConnected = Integer.parseInt(line[1]);
            endPoints[i] = new EndPoint(i,datacenterLatency,numberOfCacheServers);

            for(int u=0;u<numberConnected;u++){
                line = lines[k].split(" "); k++;
                endPoints[i].cacheServers[Integer.parseInt(line[0])] = Integer.parseInt(line[1]);
            }
        }

        requests = new  Request[numberOfRequests];
        for(int i=0;i<numberOfRequests;i++){
            line = lines[k].split(" "); k++;
            requests[i] = new Request(i,Integer.parseInt(line[0]),Integer.parseInt(line[1]),Integer.parseInt(line[2]));
        }

        cacheServers = new CacheServer[numberOfCacheServers];
        for(int i=0;i<numberOfCacheServers;i++){
            cacheServers[i] = new CacheServer(i,cacheServerCapacity,numberOfEndpoints,numberOfVideos);
        }
    }

    void solver(){
        Arrays.sort(requests);


        int minCacheLatencyAvailable;
        int indexMinCacheLatency;
        for (Request request: requests) {
            int numberRequested = request.number;

            int finalVideoSize = videos[request.videoID].size;
            Integer[] finalEndPointCacheServerLatency = endPoints[request.endPointID].cacheServers;
            int finalEndPointDataServerLatency = endPoints[request.endPointID].dataCenterLatency;
            if(IntStream.range(0,finalEndPointCacheServerLatency.length)
                    .filter(i -> finalEndPointCacheServerLatency[i]>-1 && cacheServers[i].currentSpace>finalVideoSize
                    && finalEndPointCacheServerLatency[i]< finalEndPointDataServerLatency)
                    .mapToObj(i -> finalEndPointCacheServerLatency[i]).min(Integer::compare).isPresent()
            ){
                minCacheLatencyAvailable = IntStream.range(0,finalEndPointCacheServerLatency.length)
                        .filter(i -> finalEndPointCacheServerLatency[i]>-1 && cacheServers[i].currentSpace>finalVideoSize)
                        .mapToObj(i -> finalEndPointCacheServerLatency[i]).min(Integer::compare).get();
            }
            else break;

            //System.out.println(minCacheLatencyAvailable);
            indexMinCacheLatency = Arrays.asList(finalEndPointCacheServerLatency).indexOf(minCacheLatencyAvailable);
            //System.out.println(indexMinCacheLatency);

            cacheServers[indexMinCacheLatency].currentSpace -= finalVideoSize;
        }
    }

    void printResult(){}
}
