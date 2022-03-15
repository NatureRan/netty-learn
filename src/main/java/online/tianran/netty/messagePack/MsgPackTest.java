package online.tianran.netty.messagePack;

import org.msgpack.MessagePack;
import org.msgpack.template.Template;
import org.msgpack.template.TemplateReference;
import org.msgpack.template.Templates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MsgPackTest {

    public static void main(String[] args) throws IOException {
//        List<String> strList = new ArrayList<>();
//        strList.add("hello");
//        strList.add("world");
//        strList.add("hahahaha");
//        MessagePack messagePack = new MessagePack();
//        byte[] row = messagePack.write(strList);
//        List<String> res = messagePack.read(row, Templates.tList(Templates.TString));
//        System.out.println(res.get(0));
//        System.out.println(res.get(1));
//        System.out.println(res.get(2));
        MessagePack messagePack = new MessagePack();
        byte[] row = messagePack.write(new UserInfo(1L, "name"));
        UserInfo userInfo = messagePack.read(row, UserInfo.class);
        System.out.println(userInfo);
    }
}
