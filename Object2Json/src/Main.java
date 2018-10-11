import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BSONObject;
import org.bson.Document;
import org.bson.json.Converter;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.*;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;

public class Main{
	public static void main(String[] args) throws JsonParseException, JsonMappingException, IOException {

		String textUri = "mongodb://tuankiet:tuankiet1234@ds119651.mlab.com:19651/chat";
		MongoClientURI uri = new MongoClientURI(textUri);
		MongoClient m = new MongoClient(uri);
        System.out.println(System.currentTimeMillis());
		DB db = m.getDB("chat");
		DBCollection coll= db.getCollection("message");
		DBCursor cursor=coll.find();
		while(cursor.hasNext()) {
			DBObject obj=cursor.next();
			BasicDBList bs=(BasicDBList) obj.get("hieu");
			String s=((BasicDBObject)bs.get(0)).toString();
			TinNhan mess= new ObjectMapper().readValue(s,TinNhan.class);
			System.out.println(mess.getMsg());
		}
		
	}
}



/*public class Main {
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		Message msg=new Message();
		TinNhan tn=new TinNhan();
		tn.setId("0");
		tn.setFrom("tuankiet");
		tn.setTo("hieu");
		tn.setMsg("Hello");
		tn.setCreatedAt(new Date());
		tn.setReceivedAt(new Date());
		tn.setTransfered(true);
		msg.setType("msg");
		msg.setParams(tn);
		msg.setHash("abcded");
		ObjectMapper om=new ObjectMapper();
		String output=om.writeValueAsString(msg);
		System.out.println(output);
		//
		String s="{\"type\":\"msg\",\"params\":{\"id\":\"0\",\"from\":\"tuankiet\",\"to\":\"hieu\",\"msg\":\"Hello\",\"createdAt\":1539267327366,\"receivedAt\":1539267327366,\"transfered\":true},\"hash\":\"abcded\"}";
		Message mess= new ObjectMapper().readValue(s,Message.class);
		System.out.println(mess.getParams().getClass().toString());
	}
}
*/
