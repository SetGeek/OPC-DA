package cn.com.sgcc.gdt.opc.client.utils;

import cn.com.sgcc.gdt.opc.client.bean.DataItem;
import cn.com.sgcc.gdt.opc.lib.da.ItemState;
import lombok.extern.slf4j.Slf4j;
import org.jinterop.dcom.core.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 格式数据转换工具
 *
 * @author ck.yang
 */
 @Slf4j
public class JiVariantUtil {
    /** java的数值、对象、字符类型的前缀 */
    public static final String BASE_TYPE_PRDFIX = "java.lang";
    /** 时间类型的前缀 */
    public static final String DATE_TYPE_PRDFIX = "java.util";

    /**
     * 解析服务器返回的数据，转换为客户端格式的数据
     * @param itemId 数据主键
     * @param itemState
     * @return
     * @throws Exception
     */
    public static DataItem parseValue(String itemId, ItemState itemState) throws Exception{
        Map<String, Object> value = getValue(itemState.getValue());
        return new DataItem(
                itemId,
                value.get("type").toString(),
                value.get("value"),
                itemState.getQuality(),
                itemState.getTimestamp().getTime()
            );
    }

    /**
     * 提取JIVariant的值，转换为java.lang下的对象   <br>
     *
     * JIVariant有如下的返回格式：   <br>
     * JIArray objectAsArray = jiVariant.getObjectAsArray();   <br>
     * IJIUnsigned objectAsUnsigned = jiVariant.getObjectAsUnsigned();   <br>
     * IJIComObject objectAsComObject = jiVariant.getObjectAsComObject();   <br>
     * JIVariant objectAsVariant = jiVariant.getObjectAsVariant();   <br>
     * JIString objectAsString = jiVariant.getObjectAsString();   <br>
     * @param jiVariant
     * @return
     * @throws Exception
     */
    private static Map<String, Object> getValue(JIVariant jiVariant) throws Exception{
        Object newValue ;
        Object oldValue = jiVariant.getObject();
        String typeName = oldValue.getClass().getTypeName();
        if(typeName.startsWith(BASE_TYPE_PRDFIX) || typeName.startsWith(DATE_TYPE_PRDFIX)){
            newValue = jiVariant.getObject();
        }else if(oldValue instanceof JIArray){
            newValue = jiVariant.getObjectAsArray();
        }else if(oldValue instanceof IJIUnsigned){
            newValue = jiVariant.getObjectAsUnsigned().getValue();
        }else if(oldValue instanceof IJIComObject){
            newValue = jiVariant.getObjectAsComObject();
        }else if(oldValue instanceof JIString){
            newValue = jiVariant.getObjectAsString().getString();
        }else if(oldValue instanceof JIVariant){
            newValue = jiVariant.getObjectAsVariant();
        }else{
            newValue = oldValue;
            log.error("无法解析服务器的数据类型'{}'！原始数据：{}", typeName, oldValue.toString());
        }

        HashMap<String, Object> result = new HashMap<>(2);
        result.put("type", newValue.getClass().getSimpleName());
        result.put("value", newValue);
        return result;
    }
}
