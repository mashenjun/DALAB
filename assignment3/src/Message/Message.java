package Message;

/**
 * Created by mashenjun on 22-4-15.
 */
import lombok.Data;
import lombok.Setter;

import java.io.Serializable;


@Data
public class Message implements Serializable,Cloneable{

    private static final long serialVersionUID = -1213496469674330242L;
    @Setter
    private int round;
    @Setter
    private int value;
    @Setter
    private MessageType type;

    public Message(MessageType type, int round, int value) {
        this.round=round;
        this.type=type;
        this.value=value;
    }



}
