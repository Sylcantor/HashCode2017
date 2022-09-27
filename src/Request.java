public class Request implements Comparable<Request>{
    Integer requestID;
    int videoID;
    int endPointID;
    Integer number;
    Request(int requestID,int videoID, int endPointID, Integer number){
        this.requestID = requestID;
        this.videoID = videoID;
        this.endPointID = endPointID;
        this.number = number;
    }


    public int getNumber(){
        return this.number;
    }

    public Integer getRequestID(){
        return this.requestID;
    }

    @Override
    public String toString(){
        return "{" + "requestId="+ requestID + ", videoID=" + videoID + ", enPointID=" + endPointID +
                ", number=" + number + "}";
    }

    @Override
    public int compareTo(Request o){
        if(this.number != o.getNumber()){
            return this.number - o.getNumber();
        }
        return this.requestID.compareTo(o.getRequestID());
    }
}
