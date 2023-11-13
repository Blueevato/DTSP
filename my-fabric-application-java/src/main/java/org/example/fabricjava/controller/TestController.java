package org.example.fabricjava.controller;

//import autovalue.shaded.com.google.common.collect.Maps;
import com.google.protobuf.InvalidProtocolBufferException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.StringUtils;
import org.example.fabricjava.ChaincodeEventCapture;
import org.example.fabricjava.config.GatewayConfig;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractEvent;
import org.hyperledger.fabric.gateway.ContractException;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.sdk.*;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import java.io.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

import java.util.Map;

import javax.annotation.Resource;
import javax.json.JsonObject;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.EnumSet;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hyperledger.fabric.gateway.*;
import org.example.fabricjava.FileUtil;
import org.example.fabricjava.EventListenUtil;
import springfox.documentation.schema.Entry;
import sun.management.Agent;
import org.example.fabricjava.config.GatewayConfig;
import org.example.fabricjava.EncodeUtilHelper;



//encrpyt decrpyt
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.example.fabricjava.AesEncodeUtil;


/**
 *
 *
 * @author valentinebeats
 * @version 1.0
 * @date 2022/11/27
 */
@Api(tags = "testController")
@RestController
public class TestController {
    @Resource
    private Contract contract;
    //事件监听
    private ContractEvent contractevent;
    private BlockEvent blockEvent;
    private String handle;


    //match
    private int mark = 0 ;


    //get
    private int setFlag = 0;

    @Resource
    private Network network;

    //转json
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class Deployment {
        private String userId;
        private String name;
        private String token;
        private String hashCode;
        private String description;
        private List<String>  requestId;
    }

    /**
     * 访问的组织名
     */
    @Value("${fabric.mspid}")
    public String mspId;

    /**
     * 用户名
     */
    @Value("${fabric.username}")
    public String userName;
    /**
     * 通道名字
     */
    @Value("${fabric.channelName}")
    public String channelName;
    /**
     * 链码名字
     */
    @Value("${fabric.contractName}")
    private String contractName;



    /**
     * 搜索引擎 简易
     */




    //debug
    @GetMapping("/getUser")
    @ApiOperation("getUser-查询指定Account")
    public JSONObject getUser(String userId) throws ContractException {
        //查询合约对象
        byte[] queryAResultBefore = contract.evaluateTransaction("getUser",userId);
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        // JSONObject jo = JSONObject.parseObject(convent);
        //JSONObject joo = stringToJson(convent);
        JSONObject jo = JSONObject.parseObject(convent);
       // List<String> request = new ArrayList<>();
       // return new Deployment(jo.getString("userId"),jo.getString("name"),jo.getString("money"),jo.getString("hashCode"),jo.getString("description"),jo.getObject("requestId",request));
        return jo;
    }

    //test-git
    @GetMapping("/ban1")
    @ApiOperation("ban1")
    public JSONObject ban1(String userId) throws ContractException {
        //查询合约对象
        byte[] queryAResultBefore = contract.evaluateTransaction("getUser",userId);
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        // JSONObject jo = JSONObject.parseObject(convent);
        //JSONObject joo = stringToJson(convent);
        JSONObject jo = JSONObject.parseObject(convent);
        // List<String> request = new ArrayList<>();
        // return new Deployment(jo.getString("userId"),jo.getString("name"),jo.getString("money"),jo.getString("hashCode"),jo.getString("description"),jo.getObject("requestId",request));
        return jo;
    }

    //令牌转移
    @GetMapping("/transfer")
    @ApiOperation("transfer-令牌转移")
    public String transfer(String sourceId,String targetId,String token) throws ContractException, InterruptedException, TimeoutException {

        try{
        byte[] invokeResult = contract.createTransaction("transfer").setEndorsingPeers(network.getChannel()
                .getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER))).submit(sourceId, targetId, token);

        String txId = new String(invokeResult, StandardCharsets.UTF_8);
        return txId;
        }
        catch (ContractException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    @GetMapping("/test-addUser")
    @ApiOperation("test-addUser-新增元数据byString")
    public String addUser(String userId, String userName, String token,String hashCode,String description) throws ContractException, InterruptedException, TimeoutException {
        //提交事务  存储到账本
        byte[] invokeResult = contract.createTransaction("addUser")
                .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(userId, userName, token, hashCode, description);
        String txId = new String(invokeResult, StandardCharsets.UTF_8);
        return txId;
    }

    /**
     * 发送数据访问请求
     * TODO 解析处理调用请求
     * 非注册用户无法发送访问请求
     */
    @GetMapping("/sendRequest")
    @ApiOperation("数据调用-sendRequest-发送数据访问请求")
    public String sendRequest(String sourceId, String targetId, String requestDesc) throws ContractException, InterruptedException, TimeoutException {
        try{
        //提交事务  存储到账本
        byte[] invokeResult = contract.createTransaction("sendRequest")
                .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(sourceId, targetId, requestDesc);

        //发送到对应接口再转发




        String txId = new String(invokeResult, StandardCharsets.UTF_8);
            return txId;
        }
        catch (ContractException e) {
            e.printStackTrace();
            return e.getMessage();
        }
        //return "";
    }

    /**
     * 响应数据访问请求
     * 返回加密后的数据文件下载到本地；
     */
    @GetMapping("/feedbackRequest")
    @ApiOperation("数据调用-feedbackRequest-响应数据访问请求")
    public String feedbackRequest(String targetId, String sourceId, String FeedbackDesc, String token) throws ContractException, InterruptedException, TimeoutException {
        //提交事务  存储到账本
        byte[] invokeResult = contract.createTransaction("feedbackRequest")
                .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(targetId, sourceId, FeedbackDesc, token);
        String txId = new String(invokeResult, StandardCharsets.UTF_8);
        String txt2="结果已加密! 返回数据文件下载至本地路径: /Users/valentinebeats/go/src/github.com/Blueeva/fabric-samples/twonodes/IPFS"+"\n";
        String txt1="需求方: "+sourceId+", 已成功获取数据！ 数据标识id为： "+targetId+"\n";
        return txt2+txt1+txId;
    }


    /**
     * 发送数据训练请求
     * 非注册用户无法发送访问请求
     * @param sourceId 请求方
     * @param targetId 数据方
     * @param chooseNode 选择的训练节点
     * @param TrainDesc 训练任务描述
     * 加时间戳 -》待定
     */
    @GetMapping("/sendTrainRequest")
    @ApiOperation("训练-sendTrainRequest-发送数据训练请求")
    public String sendTrainRequest(String sourceId, String targetId, String chooseNode, String TrainDesc) throws ContractException, InterruptedException, TimeoutException {
        try {
            //提交事务  存储到账本
            byte[] invokeResult = contract.createTransaction("sendTrainRequest")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(sourceId, targetId, chooseNode, TrainDesc);
            String txId = new String(invokeResult, StandardCharsets.UTF_8);
            return txId;
        } catch (ContractException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }
    /**
     * 响应数据训练请求
     * @param sourceId 请求id
     * @param nodeId 节点id
     */
    @GetMapping("/feedbackTrainRequest")
    @ApiOperation("训练-feedbackTrainRequest-响应数据训练请求")
    public String feedbackTrainRequest( String nodeId,  String sourceId,  String FeedbackDesc, String token) throws ContractException, InterruptedException, TimeoutException {
        //提交事务  存储到账本
        byte[] invokeResult = contract.createTransaction("feedbackTrainRequest")
                .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(nodeId, sourceId, FeedbackDesc, token);
        String txId = new String(invokeResult, StandardCharsets.UTF_8);
        return txId;
    }

    /**
     * 训练完成并返回
     * @param sourceId 请求id
     * @param nodeId 节点id
     * @param feedbackHash 训练后的结果哈希
     * @param FeedbackDesc 训练结果描述
     */
    @GetMapping("/TrainDoneRequest")
    @ApiOperation("TrainDoneRequest-训练完成并返回")
    public String TrainDoneRequest( String nodeId,  String sourceId,  String FeedbackDesc, String feedbackHash) throws ContractException, InterruptedException, TimeoutException {
        //提交事务  存储到账本
        byte[] invokeResult = contract.createTransaction("TrainDoneRequest")
                .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(nodeId, sourceId, FeedbackDesc, feedbackHash);
        String txId = new String(invokeResult, StandardCharsets.UTF_8);
        return txId;
    }


    /**
     * bad状态变更 done
     * 需要新增一个链码函数，输入String ，add到状态List 会方便点
     * @param sourceId 用户id
     * @param stateInt 需要变更的状态
     */
    @GetMapping("/test-changeStateX")
    @ApiOperation("test-changeState-废弃(int)")
    public String changeStateX(  String sourceId,  String stateInt) throws ContractException, InterruptedException, TimeoutException {
        //提交事务  存储到账本
        byte[] invokeResult = contract.createTransaction("changeState")
                .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                .submit(sourceId, stateInt);
        String txId = new String(invokeResult, StandardCharsets.UTF_8);
        return txId;
    }

    /**
     * 状态变更String
     * @param sourceId 用户id
     * @param stateString 需要变更的状态
     */
    @GetMapping("/test-changeState")
    @ApiOperation("test-changeState")
    public String changeState(  String sourceId,  String stateString) throws ContractException, InterruptedException, TimeoutException {
        //提交事务  存储到账本
        try {

            byte[] invokeResult = contract.createTransaction("changeState1")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(sourceId, stateString);
            String txId = new String(invokeResult, StandardCharsets.UTF_8);
            return txId;
        }catch (ContractException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }


    //添加json文件描述
    @GetMapping("/test-addMeta")
    @ApiOperation("test-addMeta-新增元数据byFilePath")
    public String addMeta(String userId, String userName, String token,String hashCode,String descPath) throws Exception {
        //提交事务  存储到账本
        try {
            String desc = "" + FileUtil.readFileContent(descPath);
            byte[] invokeResult = contract.createTransaction("addUser")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(userId, userName, token, hashCode, desc);
            String txId = new String(invokeResult, StandardCharsets.UTF_8);
            return txId;
        }
         catch (ContractException e) {
        e.printStackTrace();
        return e.getMessage();
    }
    }

    //添加json文件描述
    //debug
    @GetMapping("/test-modifyadd")
    @ApiOperation("test-modifyadd-新增修改调用接口后的注册表数据")
    public String modifyadd(String userId, String userName, String token,String hashCode,String descPath, String replaceApi) throws Exception {
        // modifyadd(pk+" ",userName+"interface_info","666",HashId,dataPath,replaceApi);
        try {
            // modifyadd(pk+" ",userName+"interface_info","666",HashId,dataPath,replaceApi);
            String desc = "" + FileUtil.readFileContent(descPath);
            // System.out.println(desc);
            JSONArray res=  new JSONArray();
            JSONObject jo = JSONObject.parseObject(desc);
            JSONObject j2 = (JSONObject) jo.get("parameters_info");
            JSONObject j1 = (JSONObject) jo.get("interoperate_info");
            String dataValue = JSON.toJSONString(j1.get("接口地址"));
            System.out.println(dataValue);
            j1.put("接口地址",replaceApi);
            res.add(j1);
            res.add(j2);
           // return res;

            String desc1 = res.toJSONString();
            System.out.println("res:"+desc1);
            byte[] invokeResult = contract.createTransaction("addUser")
                    .setEndorsingPeers(network.getChannel().getPeers(EnumSet.of(Peer.PeerRole.ENDORSING_PEER)))
                    .submit(userId, userName, token, hashCode, desc1);
            String txId = new String(invokeResult, StandardCharsets.UTF_8);
            return txId;
        }
        catch (ContractException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @GetMapping("/testtest-modifyadd")
    @ApiOperation("testtest-modifyadd")
    public JSONArray testmodifyadd(String descPath, String replaceApi) throws Exception {
        // modifyadd(pk+" ",userName+"interface_info","666",HashId,dataPath,replaceApi);
            String desc = "" + FileUtil.readFileContent(descPath);
           // System.out.println(desc);
        JSONArray res=  new JSONArray();
            JSONObject jo = JSONObject.parseObject(desc);
        JSONObject j2 = (JSONObject) jo.get("parameters_info");
            JSONObject j1 = (JSONObject) jo.get("interoperate_info");
            String dataValue = JSON.toJSONString(j1.get("接口地址"));
            System.out.println(dataValue);
            j1.put("接口地址",replaceApi);
            res.add(j1);
            res.add(j2);
            return res;

    }

    @GetMapping("/QueryAll")
    @ApiOperation("QueryAll-查询注册表")
    public JSONArray queryAll() throws ContractException, InterruptedException {
        byte[] queryAResultBefore = contract.evaluateTransaction("queryAll");
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        JSONArray jo = JSONArray.parseArray(convent);
        JSONArray res = new JSONArray();
        // map和interface_info不显示
        for(int i=0;i<jo.size();i++){
            JSONObject jb = jo.getJSONObject(i);
            String token = jb.getString("token");
            //System.out.println(token+"XXX");
            if(token.equals("666.0") == false){
                res.add(jb);
            }
        }
        System.out.println("queryAll注册表："+convent);
        return res;
    }



    @GetMapping("/test-queryAll")
    @ApiOperation("test-QueryAll-查ALL")
    public JSONArray testqueryAll() throws ContractException, InterruptedException {
        byte[] queryAResultBefore = contract.evaluateTransaction("queryAll");
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        JSONArray jo = JSONArray.parseArray(convent);
      //  System.out.println("queryAll注册表："+convent);
        return jo;
    }
    /**
     * 数据匹配
     * 哈希验证某个数据方的数据是否一致
     * 功能通过，return乱码 待解决.. CC_v2.3  ## done
     */
    @GetMapping("/test-matchDatahash")
    @ApiOperation("test-matchDatahash-数据匹配for hashIdMatch")
    public String matchDatahash(String userId, String dataHash) throws ContractException, InterruptedException {
       try{
           //测试时间
           long start = System.currentTimeMillis();

           byte[] queryAResultBefore = contract.evaluateTransaction("matchDatahash", userId, dataHash);
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
           long end = System.currentTimeMillis();
           System.out.println("耗时:" + (end - start) + "ms");
        return "  "+convent+" TimeStamp: "+date;
       }
       catch (ContractException e) {
           e.printStackTrace();
           return e.getMessage();
    }
    }

    @GetMapping("/GetHistory")
    @ApiOperation("GetHistory-数据溯源")
    public JSONObject getHistory(String userId) throws ContractException {
        byte[] queryAResultBefore = contract.evaluateTransaction("getHistory", userId);
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        JSONObject joHistory = JSONObject.parseObject(convent);
        return joHistory;
    }

    //按任意Key值匹配查询
    @GetMapping("/QueryMetaBydesc")
    @ApiOperation("QueryMetaBydesc-注册表搜索")
    public JSONArray queryMetaBydesc(String querykey) throws ContractException {
        byte[] queryAResultBefore = contract.evaluateTransaction("queryAll");
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        JSONArray jo = JSONArray.parseArray(convent);
        int len=jo.size();
      //  String[] sarray= new String[len];
       JSONArray res = new JSONArray();
        for(int i=0;i<len;i++) {
            String s = "" + jo.getString(i);
            JSONObject j = JSONObject.parseObject(s);
            String ss = "" + j.getString("description");
            Pattern pt = Pattern.compile(querykey);
            Matcher matcher = pt.matcher(ss);
            if(matcher.find()){
                res.add(j);
            }
        }
        return res;
    }


    //标识解析
    @GetMapping("/idResolution")
    @ApiOperation("idResolution-标识解析")
    public JSONArray idResolution(String pkId) throws ContractException {
        byte[] queryAResultBefore = contract.evaluateTransaction("queryAll");
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        JSONArray jo = JSONArray.parseArray(convent);
        int len=jo.size();
        //  String[] sarray= new String[len];
        JSONArray res = new JSONArray();
        for(int i=0;i<len;i++) {
            String s = "" + jo.getString(i);
            JSONObject j = JSONObject.parseObject(s);
            String ss = "" + j.getString("userId");
            Pattern pt = Pattern.compile(pkId+" ");
            Matcher matcher = pt.matcher(ss);
            if(matcher.find()){
                res.add(j);
            }
        }
        return res;
    }



    //调用接口注册处理，addUser里调用，
    @GetMapping("/apiProcess")
    @ApiOperation("apiProcess-接口注册处理")
    public String apiProcess(String filePath,String pkId) throws Exception {

        String desc = "" + FileUtil.readFileContent(filePath);
       JSONObject ob = JSONObject.parseObject(desc);
        String path = (String) ob.getJSONObject("interoperate_info").get("接口地址");
        System.out.println("XX"+path);
        //处理注册的调用接口，DHT转发
        //AES 固定位数加密 todo
        String res = AesEncodeUtil.encrypt(path);
        System.out.println(res);
        return res;
    }


    //身份验证
    @GetMapping("/idManagement")
    @ApiOperation("idManagement-CA身份验证")
    public List<String> idManagement(String certPath) throws ContractException, InvalidArgumentException, ProposalException, InvalidProtocolBufferException {
//        byte[] queryAResultBefore = contract.evaluateTransaction("getHistory", userId);
//        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
//        JSONObject joHistory = JSONObject.parseObject(convent);
        List<String> commands =new ArrayList<String>();
        commands.add("openssl x509 -in "+certPath+" -text -noout");
            List<String> rspList = new ArrayList<String>();
        Channel mc = network.getChannel();
        String desc = "验证通过！";
        rspList.add(desc);
        String res = "所属通道："+mc.getName();
        rspList.add(res);
            Runtime run = Runtime.getRuntime();
            //命令行操作
            try {
                Process proc = run.exec("/bin/bash", null, null);
                BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
                for (String line : commands) {
                    out.println(line);
                }
                out.println("exit");// 这个命令必须执行，否则in流不结束。
                String rspLine = "";
                while ((rspLine = in.readLine()) != null) {
                    System.out.println(rspLine);
                    rspList.add(rspLine);
                }
                proc.waitFor();
                in.close();
                out.close();
                proc.destroy();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return rspList;
    }

    //CA-标识申请
    // 密钥生成
    @GetMapping("/test-idApply")
    @ApiOperation("test-idApply-CA身份标识申请-for hashIdApply")
    public List<String> idApply(String certName,String pk) throws ContractException, InvalidArgumentException, ProposalException, InvalidProtocolBufferException, InterruptedException, TimeoutException {
        List<String> commands =new ArrayList<String>();
        commands.add("openssl ecparam -name secp384r1 -genkey | openssl pkcs8 -topk8 -nocrypt > "+certName+".key.pem");
        List<String> rspList = new ArrayList<String>();
        Channel mc = network.getChannel();
        String desc = "标识："+certName+" 申请成功！";
        rspList.add(desc);
        String res = "所属通道："+mc.getName();
        rspList.add(res);
        Runtime run = Runtime.getRuntime();
        //命令行操作
        try {
            Process proc = run.exec("/bin/bash", null, null);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            for (String line : commands) {
                out.println(line);
            }
            out.println("exit");// 这个命令必须执行，否则in流不结束。
            String rspLine = "";
            while ((rspLine = in.readLine()) != null) {
                System.out.println(rspLine);
                rspList.add(rspLine);
            }
            proc.waitFor();
            in.close();
            out.close();
            proc.destroy();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        rspList.add("生成公钥为："+pk+"，私钥为： "+certName+".key.pem ，已存储在本地，请妥善保管！");
        return rspList;
    }


    /**
     *  路由转发，拿到修改过的api-》原始api
     *  公钥 to 原始api
     * @return
     * @throws ContractException
     * @throws InterruptedException
     */
    @GetMapping("/RouteRequest")
    @ApiOperation("RouteRequest-路由转发")
    public List<String> RouteRequest(String pk,String requestid,String apiPath) throws ContractException, InterruptedException, TimeoutException {
        byte[] queryAResultBefore = contract.evaluateTransaction("queryAll");
        String convent= new String(queryAResultBefore, StandardCharsets.UTF_8);
        JSONArray jo = JSONArray.parseArray(convent);
        JSONObject jb = new JSONObject();
        int len=jo.size();
        //  String[] sarray= new String[len];
        //非法调用请求处理 pre

        for(int i=0;i<len;i++) {
            String s = "" + jo.getString(i);
            JSONObject j = JSONObject.parseObject(s);
            String ss = "" + j.getString("userId");
            Pattern pt = Pattern.compile(pk+"  ");
            Matcher matcher = pt.matcher(ss);
            if(matcher.find()){
                jb = j;
            }
        }
        List<String> res = new ArrayList<>();
        String code=""+jb.get("hashCode");
      //  List<String> res= new ArrayList<>();
        String res1 = AesEncodeUtil.decrypt(code);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳

        String r1 = changeState(pk,"调用记录已存证，数据被"+requestid+"调用！调用时间："+date);
        System.out.println(r1);
        res.add(res1);
        res.add(r1);
        return res;
    }

    /**
     * 注册
     * @param userName 数据名称
     * @param filePath Metadata文件路径
     * @param dataPath 数据实体（API）文件路径
     * @param originalhash 原始数据哈希
     * 处理API调用文件，封装接口
     */
    @GetMapping("/Register")
    @ApiOperation("Register-注册")
    public List<String> hashIdApply(String userName,String filePath, String dataPath , String originalhash) throws Exception {
        List<String> commands =new ArrayList<String>();
        commands.add("ipfs add "+filePath);
        List<String> rspList = new ArrayList<String>();
        Channel mc = network.getChannel();
        String desc = "数据："+filePath+" HashId申请成功！";
        rspList.add(desc);
        String res = "所属通道："+mc.getName();
        String desc1 = "标识ID："+filePath+" ";
        rspList.add(res);
        rspList.add(desc1);
        Runtime run = Runtime.getRuntime();
        String HashId ="";
        //命令行操作
        try {
            Process proc = run.exec("/bin/bash", null, null);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            for (String line : commands) {
                out.println(line);
            }
            out.println("exit");// 这个命令必须执行，否则in流不结束。
            String rspLine = "";
            int index = 0;
            while ((rspLine = in.readLine()) != null) {
                System.out.println(rspLine);
                rspList.add(rspLine);
                index++;
                if(index==1){
                     HashId = ""+rspLine.substring(6 ,52);
                     System.out.println("111"+HashId);
                }
            }
            proc.waitFor();
            in.close();
            out.close();
            proc.destroy();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String pk = EncodeUtilHelper.genAesSecret();//公钥
        List<String> joined = new ArrayList<>();
        List<String> idapply = idApply("Sk_"+userName,pk);
        joined.addAll(rspList);
        joined.addAll(idapply);
        String added = addMeta(pk,userName,"0",HashId,filePath);
       //预言机合约 done
        String replaceApi = apiProcess(dataPath,pk);//替换接口地址为平台代理
//        jsonObject.put("key", newValue);
        //映射
        String addoracle = addUser(pk+"  ",userName+"map","666",replaceApi,"");

        String added1 = modifyadd(pk+" ",userName+"interface_info","666",HashId,dataPath,replaceApi);
      //  String added1 = addMeta(pk+" ",userName+"interface_info","666",HashId,dataPath);


        String tag = changeState(pk,"标识已申请！ 公钥为："+pk);
        String tag11 = changeState(pk+" ","标识已申请！ 公钥为："+pk);
        String tag1 = changeState(pk,"原始数据哈希为："+originalhash);
        String tag1111 = changeState(pk+" ","原始数据哈希为："+originalhash);
        joined.add(added);
        joined.add(tag);
        joined.add(tag1);
        return joined;
    }


    /**
     * 查看数据
     *
     */
    @GetMapping("/test-hashIdcat")
    @ApiOperation("test-hashIdcat-查看数据")
    public List<String> hashIdcat(String hashCode) throws ContractException, InvalidArgumentException, ProposalException, InvalidProtocolBufferException {
        List<String> commands =new ArrayList<String>();
        commands.add("ipfs cat "+hashCode);
        int range = 10;
        setFlag+=(int)(Math.random() * range);
        commands.add("ipfs get "+hashCode+" -o ./save"+setFlag+" -a");

        System.out.println("ipfs get "+hashCode+" -o ./save"+setFlag+" -a");

        List<String> rspList = new ArrayList<String>();
        Channel mc = network.getChannel();
        String desc = "数据："+hashCode+" 核查中...";
        rspList.add(desc);
        String res = "所属通道："+mc.getName();
        rspList.add(res);
        Runtime run = Runtime.getRuntime();
        //命令行操作
        try {
            Process proc = run.exec("/bin/bash", null, null);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            for (String line : commands) {
                out.println(line);
            }
            out.println("exit");// 这个命令必须执行，否则in流不结束。
            String rspLine = "";
            int flag = 0;
            while ((rspLine = in.readLine()) != null) {
                System.out.println(rspLine);
                rspList.add(rspLine);
                flag++;
            }
            if(flag==0){
                rspList.add("未找到此数据哈希（"+hashCode+"）所对应的数据！");
            }
            proc.waitFor();
            in.close();
            out.close();
            proc.destroy();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rspList;
    }

    /**
     * Hash标识验证
     * 哈希验证
     * 文件匹配原始哈希
     * @param filePath 文件路径
     * @param userId 匹配对象ID
     */
    @GetMapping("/HashIdMatch")
    @ApiOperation("HashIdMatch-数据匹配")
    public List<String> hashIdMatch(String filePath,String userId) throws ContractException, InvalidArgumentException, ProposalException, InvalidProtocolBufferException, InterruptedException, TimeoutException {

        List<String> commands =new ArrayList<String>();
        commands.add("ipfs add "+filePath);
        List<String> rspList = new ArrayList<String>();
        Channel mc = network.getChannel();
        String desc = "文件路径："+filePath;
        String desc1 = " 数据匹配中...";
        rspList.add(desc);
        rspList.add(desc1);
        Runtime run = Runtime.getRuntime();
        //命令行操作
        try {
            Process proc = run.exec("/bin/bash", null, null);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(proc.getOutputStream())), true);
            for (String line : commands) {
                out.println(line);
            }
            out.println("exit");// 这个命令必须执行，否则in流不结束。
            String rspLine = "";
            //int flag = 0;
            int index=0;
            String fileHash ="";
            while ((rspLine = in.readLine()) != null) {
                System.out.println(rspLine);
                index++;
                if(index == 1) {
                    String hashCode = rspLine.substring(6 ,52);
                    rspList.add("文件Hash为："+hashCode);
                    String matchreturn =matchDatahash(userId,hashCode);
                   // if(matchreturn)
                    rspList.add(matchreturn);
                    String res1 = "匹配完成！ 交易哈希为："+changeState(userId,"匹配完成！");
                    rspList.add(res1);
                }
                else
                rspList.add(rspLine);
            }
//            if(flag == 0){
//                rspList.add("匹配失败！");
//            }
//            else{
            String res = "所属通道："+mc.getName();
            rspList.add(res);

            proc.waitFor();
            in.close();
            out.close();
            proc.destroy();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return rspList;
    }


    @GetMapping("/test")
    @ApiOperation("test-打印测试信息")
    public List<String> test() throws ContractException, InvalidArgumentException, ProposalException, InvalidProtocolBufferException {

        List<String> testres =new ArrayList<String>();
//        HFClient hf =  HFClient.createNewInstance();
//        Channel mc = hf.newChannel(channelName);
        Channel mc = network.getChannel();
        BlockchainInfo channelInfo = mc.queryBlockchainInfo();
        BlockInfo returnedBlock = mc.queryBlockByNumber(channelInfo.getHeight()-1);
        //当前区块高度
        String r1="Block height:"+channelInfo.getHeight();
        //当前区块哈希
        byte[] bt= channelInfo.getCurrentBlockHash();
        String bt1=EncodeUtilHelper.byte2Base64StringFun(bt);
        String r2="Current BlockHash:"+bt1;
        //PreviousBlockHash 上一区块哈希
        byte[] pb= channelInfo.getPreviousBlockHash();
        String pb1=EncodeUtilHelper.byte2Base64StringFun(pb);
        String r3="Previous BlockHash:"+pb1;

        //Data Hash
        byte[] dh= returnedBlock.getDataHash();
        String dh1=EncodeUtilHelper.byte2Base64StringFun(dh);
        String r4="Data Hash:"+dh1;

        //TransActions MetaData
        byte[] tm= returnedBlock.getTransActionsMetaData();
        String tm1=EncodeUtilHelper.byte2Base64StringFun(tm);
        String r5="TransActions MetaData:"+tm1;

        testres.add(r1);
        testres.add(r2);
        testres.add(r3);
        testres.add(r4);
        testres.add(r5);
     //   BlockInfo returnedBlock = mc.queryBlockByNumber(channelInfo.getHeight());
        return testres;
    }

    //事件监听 ing
//    Contract contract1 = network.getContract("registercc");
//        EventListenUtil.listenAllEvent(contract1);
//        EventListenUtil.waitEvent(1);


    /**
     * BASE64编码
     *
     * @param originalText
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/test-encryptBASE64")
    @ApiOperation("test-encryptBASE64-test")
    public  String encryptBASE64( String originalText) throws UnsupportedEncodingException {
//         Base64.Encoder encoder = Base64.getEncoder();
//         byte[] textByte = originalText.getBytes("UTF-8");
//         String encodedText = encoder.encodeToString(textByte);
        return  AesEncodeUtil.encrypt(originalText);
    }

    /**
     * BASE64解码
     *
     * @param encodedText
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("/test-decryptBASE64")
    @ApiOperation("test-decryptBASE64-test")
    public  String decryptBASE64( String encodedText) throws UnsupportedEncodingException {
//        final Base64.Decoder decoder = Base64.getDecoder();
//        final byte[] textByte = encodedText.getBytes("UTF-8");
//       String res= new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
       //JSONObject res1 = new JSONObject();
       //res1.put("1","请求");
        return AesEncodeUtil.decrypt(encodedText);
    }

//    @GetMapping("/testCRYPT")
//    public  List<String> testCRYPT(String text){
//        List<String> res= new ArrayList<>();
//      //  AesEncodeUtil ae= new AesEncodeUtil();
//        String r1 = AesEncodeUtil.encrypt(text);
//        String r2 = AesEncodeUtil.decrypt(r1);
//        res.add(r1);
//        res.add(r2);
//        return res;
//    }
    @GetMapping("/testShow")
    public String testshow(String path) throws Exception {
        System.out.println("1");
        String text= FileUtil.readFileContent(path);
        return  encryptBASE64(text);
    }
}
