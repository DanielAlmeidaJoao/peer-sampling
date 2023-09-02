public class MSGSentReceivedCount {


    public int sentCount;
    private int receivedCount;

    final int disseminatedSecId;
    public int getReceivedCount(){
        return receivedCount;
    }

    public long sentTime, sumElapsed;
    public final int mid;


    public void deliver(long deliverTime){
        long sub = (deliverTime-sentTime);
        sumElapsed +=sub;
        receivedCount++;
    }



    public MSGSentReceivedCount(int MID,int sent, int receivedCount, long sentTime, int disseminatedSecId){
        this.mid=MID;
        this.sentCount = sent;
        this.receivedCount = receivedCount;
        sumElapsed = 0;
        if(sentTime>0){
            this.sentTime = sentTime;
        }
        this.disseminatedSecId = disseminatedSecId;
    }
}
