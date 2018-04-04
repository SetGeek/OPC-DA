package cn.com.sgcc.gdt.opc.da.simple.reader;

import cn.com.sgcc.gdt.opc.lib.common.ConnectionInformation;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * TODO
 *
 * @author ck
 */
public class PropertiesUtil {

    /**
     * 加载配置文件
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    public static Properties loadProperties(String fileName) throws IOException {
        InputStream stream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
        Properties properties = new Properties();
        properties.load(stream);
        return properties;
    }

    /**
     * 加载opc配置
     * @return
     */
    public static List<ConnectionInformation> getOpcConfig() throws IOException {
        Properties properties = loadProperties("opc.properties");
        String[] runProfixes = properties.getProperty("runProfix").split(",");
        List<ConnectionInformation> result = new ArrayList<>();
        for (String profix: runProfixes){
            String host = properties.getProperty(profix + "-host");
            String user = properties.getProperty(profix + "-user");
            String password = properties.getProperty(profix + "-password");
            String domain = properties.getProperty(profix + "-domain", host);
            String clsId = properties.getProperty(profix + "-clsId");
            String progId = properties.getProperty(profix + "-progId");

            ConnectionInformation connInfo = new ConnectionInformation(host, domain, user, password, clsId, progId);
            result.add(connInfo);
        }
        return result;
    }

//    dashi-host=192.168.2.254
//    dashi-user=Administrator
//    dashi-domain=192.168.2.254
//    dashi-clsId=7BC0CC8E-482C-47CA-ABDC-0FE7F9C6E729
//    dashi-progId=Kepware.KEPServerEX.V6
//    dashi-kafkaHost=192.168.2.254
//    dashi-kafkaTopic=opc
}
