import java.io.IOException;
import java.nio.charset.StandardCharsets;
//import java.nio.file.Files;
//import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.IntStream;

public class Parser {
    int numberOfVideos;
    int numberOfEndpoints;
    int numberOfRequests;
    int numberOfCacheServers;
    int[] numberOfCacheServersUsed;
    int cacheServerCapacity;
    int k = 0;

    String file;
    Video[] videos;
    EndPoint[] endPoints;
    CacheServer[] cacheServers;
    Request[] requests;

    Parser(String file) {
        this.file = file;
    }

    public void setupData() throws IOException {
        // Scanner from system.in

        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

        /*
         * byte[] encoded = Files.readAllBytes(Paths.get(file));
         * String fileContent = new String(encoded, StandardCharsets.UTF_8);
         * String[] lines = fileContent.split("\n");
         * String[] line = lines[k].split(" ");
         * k++;
         */

        numberOfVideos = sc.nextInt();
        numberOfEndpoints = sc.nextInt();
        numberOfRequests = sc.nextInt();
        numberOfCacheServers = sc.nextInt();
        numberOfCacheServersUsed = new int[numberOfCacheServers];
        cacheServerCapacity = sc.nextInt();
        // line = lines[k].split(" ");
        // k++;

        videos = new Video[numberOfVideos];

        for (int i = 0; i < numberOfVideos; i++) {
            videos[i] = new Video(i, sc.nextInt());
        }

        endPoints = new EndPoint[numberOfEndpoints];
        for (int i = 0; i < numberOfEndpoints; i++) {
            // line = lines[k].split(" ");
            // k++;
            int datacenterLatency = sc.nextInt();
            int numberConnected = sc.nextInt();
            endPoints[i] = new EndPoint(i, datacenterLatency, numberOfCacheServers);

            for (int u = 0; u < numberConnected; u++) {
                // line = lines[k].split(" ");
                // k++;
                endPoints[i].cacheServers[sc.nextInt()] = sc.nextInt();
            }
        }

        requests = new Request[numberOfRequests];
        for (int i = 0; i < numberOfRequests; i++) {
            // line = lines[k].split(" ");
            // k++;
            requests[i] = new Request(i, sc.nextInt(), sc.nextInt(), sc.nextInt());
        }

        cacheServers = new CacheServer[numberOfCacheServers];
        for (int i = 0; i < numberOfCacheServers; i++) {
            cacheServers[i] = new CacheServer(i, cacheServerCapacity, numberOfEndpoints, numberOfVideos);
        }
        sc.close();
    }

    void solver() {
        Arrays.sort(requests, Collections.reverseOrder());
        int minCacheLatencyAvailable;
        int indexMinCacheLatency;
        for (Request request : requests) {
            // int numberRequested = request.number;
            int finalVideoSize = videos[request.videoID].size;
            Integer[] finalEndPointCacheServerLatency = endPoints[request.endPointID].cacheServers;
            int finalEndPointDataServerLatency = endPoints[request.endPointID].dataCenterLatency;
            if (IntStream.range(0, finalEndPointCacheServerLatency.length)
                    .filter(i -> finalEndPointCacheServerLatency[i] > -1
                            && cacheServers[i].currentSpace >= finalVideoSize
                            && finalEndPointCacheServerLatency[i] < finalEndPointDataServerLatency)
                    .mapToObj(i -> finalEndPointCacheServerLatency[i]).min(Integer::compare).isPresent()) {

                minCacheLatencyAvailable = IntStream.range(0, finalEndPointCacheServerLatency.length)
                        .filter(i -> finalEndPointCacheServerLatency[i] > -1
                                && cacheServers[i].currentSpace >= finalVideoSize)
                        .mapToObj(i -> finalEndPointCacheServerLatency[i]).min(Integer::compare).get();

                // System.out.println(minCacheLatencyAvailable);
                indexMinCacheLatency = Arrays.asList(finalEndPointCacheServerLatency).indexOf(minCacheLatencyAvailable);
                // System.out.println(indexMinCacheLatency);

                cacheServers[indexMinCacheLatency].currentSpace -= finalVideoSize;
                cacheServers[indexMinCacheLatency].videos[request.videoID] = 1;
                numberOfCacheServersUsed[indexMinCacheLatency] = 1;
            }
        }
    }

    void printResult() {
        int i;
        // sum of numberOfCacheServersUsed
        int sum = Arrays.stream(numberOfCacheServersUsed).sum();
        System.out.println(sum);
        for (CacheServer cacheServer : cacheServers) {
            if (cacheServer.currentSpace != cacheServerCapacity) {
                System.out.print(cacheServer.cacheServerId);
                for (i = 0; i < cacheServer.videos.length; i++) {
                    if (cacheServer.videos[i] == 1) {
                        System.out.print(" " + i);
                    }
                }
                System.out.println();
            }
        }
    }
}
