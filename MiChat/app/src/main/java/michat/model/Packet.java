package michat.model;

import michat.model.Msg;

public class Packet{
    String type;
    Msg data;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Msg getData() {
        return data;
    }

    public void setData(Msg data) {
        this.data = data;
    }
}