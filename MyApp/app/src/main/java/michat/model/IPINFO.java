package michat.model;

public class IPINFO {
    IP pubIP;
    IP priIP;
    boolean isPub=false;
    public IPINFO(IP pubIP, IP priIP) {
        this.pubIP = pubIP;
        this.priIP = priIP;
    }

    public IP getPubIP() {
        return pubIP;
    }
    public boolean getIsPub(){
        return isPub;
    }
    public void setIsPub(boolean b){
        isPub=b;
    }
    public void setPubIP(IP pubIP) {
        this.pubIP = pubIP;
    }

    public IP getPriIP() {
        return priIP;
    }

    public void setPriIP(IP priIP) {
        this.priIP = priIP;
    }
}
