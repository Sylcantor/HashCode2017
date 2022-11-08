
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.stream.IntStream;
import java.util.Random;
import java.util.Arrays;

class Main {

    public static void main(String[] args) throws IOException {
        //save time
        long start = System.currentTimeMillis();
        Parser parser = new Parser();
        parser.setupData();
        parser.solver();

        while (System.currentTimeMillis() - start < 10000) { // 10 seconds of execution of local research
            parser.local_research();
        }
        parser.printResult();
        /*
        System.out.println(parser.score);
        //print scoreByCacheServerByVideo
        for (int i = 0; i < parser.scoreByCacheServerByVideo.length; i++) {
            System.out.println(Arrays.toString(parser.scoreByCacheServerByVideo[i]));
        }
        */
       

    }

}

class CacheServer {
    int cacheServerId;
    int currentSpace;
    int[] endpointsLatency;
    int[] videos;

    CacheServer(int cacheServerId, int currentSpace, int nbEndpoints, int nbVideo) {
        this.cacheServerId = cacheServerId;
        this.currentSpace = currentSpace;
        this.endpointsLatency = new int[nbEndpoints];
        this.videos = new int[nbVideo];
        Arrays.fill(endpointsLatency, -1);
        Arrays.fill(videos, -1);
    }
}

class EndPoint {

    int endPointId;
    int dataCenterLatency;

    Integer[] cacheServers;

    EndPoint(int endPointId, int dataCenterLatency, int nbCacheServer) {
        this.endPointId = endPointId;
        this.dataCenterLatency = dataCenterLatency;
        this.cacheServers = new Integer[nbCacheServer];
        Arrays.fill(cacheServers, -1);
    }
}

class Latency {
    EndPoint endPoint;
    CacheServer cacheServer;
    int latency;

}

class Request implements Comparable<Request> {
    Integer requestID;
    int videoID;
    int endPointID;
    Integer number;

    Request(int requestID, int videoID, int endPointID, Integer number) {
        this.requestID = requestID;
        this.videoID = videoID;
        this.endPointID = endPointID;
        this.number = number;
    }

    public int getNumber() {
        return this.number;
    }

    public Integer getRequestID() {
        return this.requestID;
    }

    @Override
    public String toString() {
        return "{" + "requestId=" + requestID + ", videoID=" + videoID + ", enPointID=" + endPointID +
                ", number=" + number + "}";
    }

    @Override
    public int compareTo(Request o) {
        if (this.number != o.getNumber()) {
            return this.number - o.getNumber();
        }
        return this.requestID.compareTo(o.getRequestID());
    }
}

class Video {
    int videoId;
    int size;

    int cacheServerId;

    Video(int videoID, int size) {
        this.videoId = videoID;
        this.size = size;
        this.cacheServerId = -1;
    }
}

class Parser {
    int numberOfVideos;
    int numberOfEndpoints;
    int numberOfRequests;
    int numberOfCacheServers;
    int[] numberOfCacheServersUsed;
    int cacheServerCapacity;
    int k = 0;
    double score = 0.0f;
    // score by each cache server by each video
    double[][] scoreByCacheServerByVideo;
    Video[] videos;
    EndPoint[] endPoints;
    CacheServer[] cacheServers;
    Request[] requests;
    int totalNumberOfRequests;


    public void setupData() throws IOException {
        // Scanner from system.in
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);

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
        totalNumberOfRequests = getTotalNumberOfRequests();
        scoreByCacheServerByVideo = new double[numberOfCacheServers][numberOfVideos];
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
                
                // calulate score and store and score by cache server by video
                score += (((double)finalEndPointDataServerLatency - (double)minCacheLatencyAvailable) * (double)request.number)/(double)totalNumberOfRequests;
                scoreByCacheServerByVideo[indexMinCacheLatency][request.videoID] = (((double)finalEndPointDataServerLatency - (double)minCacheLatencyAvailable) * (double)request.number)/(double)totalNumberOfRequests;
            }
        }
    }

    // check if cache server is empty  
    boolean isCacheServerEmpty(int cacheServerId) {
        for (int i = 0; i < numberOfVideos; i++) {
            if (cacheServers[cacheServerId].videos[i] == 1) {
                return false;
            }
        }
        return true;
    }

    // check if video is in cache server
    boolean isVideoInCacheServer(int cacheServerId, int videoId) {
        return cacheServers[cacheServerId].videos[videoId] == 1;
    }

    // check if there is enough space to add a video in cache server
    boolean isEnoughSpace(int cacheServerId, int videoId, int videoId2) {
        return cacheServers[cacheServerId].currentSpace - videoId2 >= videos[videoId].size;
    }

    // get total of number of requests
    int getTotalNumberOfRequests() {
        int total = 0;
        for (Request request : requests) {
            total += request.number;
        }
        return total;
    }

    

    
    void local_research(){
        double scoretmp = score;

        double[][] scoreByCacheServerByVideotmp = Arrays.copyOf(scoreByCacheServerByVideo, scoreByCacheServerByVideo.length);




        Random rand = new Random();
        int randomCacheServer = rand.nextInt(numberOfCacheServers);
        while (isCacheServerEmpty(randomCacheServer)) {
            randomCacheServer = rand.nextInt(numberOfCacheServers);
        }
        int randomCacheServer2 = rand.nextInt(numberOfCacheServers);
        while (isCacheServerEmpty(randomCacheServer2) && (randomCacheServer2 != randomCacheServer)) {
            randomCacheServer2 = rand.nextInt(numberOfCacheServers);
        }
        int randomVideo = rand.nextInt(numberOfVideos);
        while (!isVideoInCacheServer(randomCacheServer, randomVideo)) {
            randomVideo = rand.nextInt(numberOfVideos);
        }
        int randomVideo2 = rand.nextInt(numberOfVideos);
        while (!isVideoInCacheServer(randomCacheServer2, randomVideo2)) {
            randomVideo2 = rand.nextInt(numberOfVideos);
        }
        if (isEnoughSpace(randomCacheServer2, randomVideo, randomVideo2) && isEnoughSpace(randomCacheServer, randomVideo2, randomVideo)) {

            scoretmp -= scoreByCacheServerByVideotmp[randomCacheServer][randomVideo];
            scoreByCacheServerByVideotmp[randomCacheServer][randomVideo] = 0;
            scoretmp -= scoreByCacheServerByVideotmp[randomCacheServer2][randomVideo2];
            scoreByCacheServerByVideotmp[randomCacheServer2][randomVideo2] = 0;

            // update score by checking all requests that contain the video and that end point is connected to the cache server
            for (Request request : requests) {
                if (request.videoID == randomVideo) {
                    //for each endpoint calculate the score
                    for (int i = 0; i < numberOfEndpoints; i++) {
                        if (endPoints[i].cacheServers[randomCacheServer2] > -1) {
                            scoretmp += (((double)endPoints[i].dataCenterLatency - (double)endPoints[i].cacheServers[randomCacheServer2]) * (double)request.number)/(double)totalNumberOfRequests;
                            scoreByCacheServerByVideotmp[randomCacheServer2][randomVideo] = (((double)endPoints[i].dataCenterLatency - (double)endPoints[i].cacheServers[randomCacheServer2]) * (double)request.number)/(double)totalNumberOfRequests;
                        }
                    }
                }
                else if (request.videoID == randomVideo2) {
                    //for each endpoint calculate the score
                    for (int i = 0; i < numberOfEndpoints; i++) {
                        if (endPoints[i].cacheServers[randomCacheServer] > -1) {
                            scoretmp += (((double)endPoints[i].dataCenterLatency - (double)endPoints[i].cacheServers[randomCacheServer]) * (double)request.number)/(double)totalNumberOfRequests;
                            scoreByCacheServerByVideotmp[randomCacheServer][randomVideo2] = (((double)endPoints[i].dataCenterLatency - (double)endPoints[i].cacheServers[randomCacheServer]) * (double)request.number)/(double)totalNumberOfRequests;
                        }
                    }
                }
            }
            
            if(scoretmp > score){
                score = scoretmp;
                scoreByCacheServerByVideo = Arrays.copyOf(scoreByCacheServerByVideotmp, scoreByCacheServerByVideotmp.length);
                cacheServers[randomCacheServer].videos[randomVideo] = 0;
                cacheServers[randomCacheServer2].videos[randomVideo2] = 0;
                cacheServers[randomCacheServer].videos[randomVideo2] = 1;
                cacheServers[randomCacheServer2].videos[randomVideo] = 1;

                cacheServers[randomCacheServer].currentSpace += videos[randomVideo].size;
                cacheServers[randomCacheServer2].currentSpace += videos[randomVideo2].size;
                cacheServers[randomCacheServer].currentSpace -= videos[randomVideo2].size;
                cacheServers[randomCacheServer2].currentSpace -= videos[randomVideo].size;
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
