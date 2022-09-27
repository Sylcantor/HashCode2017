public class Video {
    int videoId;
    int size;

    int cacheServerId;

    Video(int videoID, int size){
        this.videoId = videoID;
        this.size = size;
        this.cacheServerId = -1;
    }
}
