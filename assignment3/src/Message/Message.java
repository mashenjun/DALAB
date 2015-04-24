package Message;

/**
 * Created by mashenjun on 22-4-15.
 */
import lombok.*;

import java.io.Serializable;

@Data
public class Message implements Serializable{

    private static final long serialVersionUID = -1213496469674330242L;
    private int round;
    private int value;
    private MessageType type;

    public Message(MessageType type, int round, int value) {
        setRound(round);
        setValue(value);
        setType(type);
    }

}
