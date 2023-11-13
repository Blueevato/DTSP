package org.example.fabricjava;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * 对json数据key进行替换
 *
 */
public class JSONUtil {

    public static JSONObject changeJsonObj(JSONObject jsonObj,Map<String, String> keyMap) {
        JSONObject resJson = new JSONObject();
        Set<String> keySet = jsonObj.keySet();
        for (String key : keySet) {
            String resKey = keyMap.get(key) == null ? key : keyMap.get(key);
            try {
                JSONObject jsonobj1 = jsonObj.getJSONObject(key);
                resJson.put(resKey, changeJsonObj(jsonobj1, keyMap));
            } catch (Exception e) {
                try {
                    JSONArray jsonArr = jsonObj.getJSONArray(key);
                    resJson.put(resKey, changeJsonArr(jsonArr, keyMap));
                } catch (Exception x) {
                    resJson.put(resKey, jsonObj.get(key));
                }
            }
        }
        return resJson;
    }

    public static JSONArray changeJsonArr(JSONArray jsonArr,Map<String, String> keyMap) {
        JSONArray resJson = new JSONArray();
        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            resJson.add(changeJsonObj(jsonObj, keyMap));
        }
        return resJson;
    }

    public static void main(String[] args) throws Exception {
        String jsonStr = FileUtil.readFileContent("/Users/valentinebeats/go/src/github.com/Blueeva/fabric-samples/twonodes/IPFS/testdata_API.json");
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("接口地址", "接口地址改");
        JSONObject jsonObj = JSONUtil.changeJsonObj(JSONObject.parseObject(jsonStr),keyMap);
        System.out.println("换值结果 》》 " + jsonObj.toString());
    }
}


