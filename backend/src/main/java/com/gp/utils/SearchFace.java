package com.gp.utils;

import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class SearchFace {

    public static Client client = null;
    public static final RuntimeOptions runtime = new RuntimeOptions();

    static {
        try {
            String accessKeyId = System.getenv("ALIYUN_ACCESS_KEY_ID");
            String accessKeySecret = System.getenv("ALIYUN_ACCESS_KEY_SECRET");
            if (accessKeyId == null || accessKeySecret == null) {
                throw new IllegalStateException("Missing Aliyun access key environment variables.");
            }
            client = SearchFace.createClient(accessKeyId, accessKeySecret);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "facebody.cn-shanghai.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 查询脸
     *
     * @param base 基地
     * @return {@link String}
     * @throws Exception 异常
     */
    public static String JudgementFace(String base) throws Exception {
        // 场景一，使用本地文件
        //InputStream inputStream = new FileInputStream(base);

        // 场景二  使用base64编码
        InputStream inputStream = base64ToInputStream(base);

        SearchFaceAdvanceRequest searchFaceAdvanceRequest = new SearchFaceAdvanceRequest()
                .setDbName("default")
                .setLimit(1)
                .setImageUrlObject(inputStream);
        try {
            // 复制代码运行请自行打印 API 的返回值
            SearchFaceResponse searchFaceResponse = client.searchFaceAdvance(searchFaceAdvanceRequest, runtime);
            // 获取整体结果
            // System.out.println(Common.toJSONString(TeaModel.buildMap(searchFaceResponse)));
            // System.out.println("==========================");
            // 获取单个字段：Confidence转换后的置信度
            // System.out.println(searchFaceResponse
            //         .getBody()
            //         .getData()
            //         .getMatchList()
            //         .iterator()
            //         .next()
            //         .getFaceItems()
            //         .iterator()
            //         .next()
            //         .getConfidence());
            // System.out.println(searchFaceResponse
            //         .getBody()
            //         .getData()
            //         .getMatchList()
            //         .iterator()
            //         .next()
            //         .getFaceItems()
            //         .iterator()
            //         .next()
            //         .getEntityId());
            if (searchFaceResponse
                    .getBody()
                    .getData()
                    .getMatchList()
                    .iterator()
                    .next()
                    .getFaceItems()
                    .iterator()
                    .next()
                    .getConfidence() > 70){
                return searchFaceResponse
                        .getBody()
                        .getData()
                        .getMatchList()
                        .iterator()
                        .next()
                        .getFaceItems()
                        .iterator()
                        .next()
                        .getEntityId();
            }
            return null;
        } catch (TeaException teaException) {
            throw new RuntimeException("人脸识别失败");
            // // 获取整体报错信息
            // System.out.println(Common.toJSONString(teaException));
            // // 获取单个字段
            // System.out.println(teaException.getCode());
        }
    }


    /**
     * 添加脸
     *
     * @param base     基地
     * @param entity   实体
     * @param username 用户名
     * @return {@link String}
     * @throws Exception 异常
     */
    public static Map<String, Object> addFace(String base, String entity, String username) throws Exception{
        // 在添加人脸数据之前，检测是否已经存在
        boolean b = faceIsExist(entity);
        if (!b){ // 人脸样本不存在，则先进行插入样本
            Map<String, Object> map = addFaceEntity(entity, username);
            if (map.get("EntityId") == null || map.get("EntityId") != entity){
                return map;
            }
        }

        // 场景二  使用base64编码
        InputStream inputStream = base64ToInputStream(base);
        AddFaceAdvanceRequest addFaceAdvanceRequest = new AddFaceAdvanceRequest()
                .setDbName("default")  // 数据库
                .setImageUrlObject(inputStream)  // 图片流
                .setEntityId(entity)  // 样本id
                .setExtraData(username); // 标签

        try {
            AddFaceResponse addFaceResponse = client.addFaceAdvance(addFaceAdvanceRequest, runtime);
            // 获取整体结果
            //System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(addFaceResponse)));
            // 获取单个字段
            //System.out.println(addFaceResponse.getBody().getData().getFaceId());
            return addFaceResponse.toMap();
        } catch (TeaException teaException) {
            // 获取整体报错信息
            //System.out.println(com.aliyun.teautil.Common.toJSONString(teaException));
            // 获取单个字段
            //System.out.println(teaException.getCode());
            return teaException.getData();
        }
    }

    /**
     * 添加人脸实体
     *
     * @param entity 实体
     * @param label  标签
     * @return boolean
     * @throws Exception 异常
     */
    public static Map<String, Object> addFaceEntity(String entity, String label) throws Exception {


        AddFaceEntityRequest addFaceEntityRequest = new AddFaceEntityRequest ()
                .setDbName("default")  // 数据库
                .setEntityId(entity)  // 样本id
                .setLabels(label);  //标签
        RuntimeOptions runtime = new RuntimeOptions();

        try {
            AddFaceEntityResponse  addFaceEntityResponse = client.addFaceEntityWithOptions(addFaceEntityRequest, runtime);
            // 获取整体结果
            //System.out.println(com.aliyun.teautil.Common.toJSONString(TeaModel.buildMap(addFaceEntityResponse)));
            // 获取单个字段
            //System.out.println(addFaceEntityResponse.getStatusCode());
            return addFaceEntityRequest.toMap();
        } catch (TeaException teaException) {
            //region获取整体报错信息
            //System.out.println(com.aliyun.teautil.Common.toJSONString(teaException));
            // {"code":"ClientError.IllegalArgument",
            // "message":"code: 400, entity has existed request id: 719B3DF2-F2FB-524B-B561-A6F52F2425DD",
            // "data":{"RequestId":"719B3DF2-F2FB-524B-B561-A6F52F2425DD","HostId":"facebody.cn-shanghai.aliyuncs.com","Code":"ClientError.IllegalArgument","Message":"entity has existed","Recommend":"https://api.aliyun.com/troubleshoot?q=ClientError.IllegalArgument&product=facebody","statusCode":400},
            // "statusCode":400,"stackTrace":[],"suppressedExceptions":[]}
            //endregion

            // data
            //{RequestId=719B3DF2-F2FB-524B-B561-A6F52F2425DD,
            // HostId=facebody.cn-shanghai.aliyuncs.com,
            // Code=ClientError.IllegalArgument,
            // Message=entity has existed,
            // Recommend=https://api.aliyun.com/troubleshoot?q=ClientError.IllegalArgument&product=facebody,
            // statusCode=400}
            return teaException.getData();
        }
    }

    /**
     * 查询脸是否存在
     *
     * @param entity 实体
     * @return boolean
     */
    public static boolean faceIsExist(String entity){
        GetFaceEntityRequest getFaceEntityRequest = new GetFaceEntityRequest()
                .setDbName("default")
                .setEntityId(entity);
        try {
            GetFaceEntityResponse getFaceEntityResponse = client.getFaceEntity(getFaceEntityRequest);

            //{headers={access-control-allow-origin=*, date=Wed, 21 Jun 2023 02:46:43 GMT, content-length=121, keep-alive=timeout=25, x-acs-request-id=E4D16000-BFCA-5309-8B2F-B179B7C9E081, connection=keep-alive, content-type=application/json;charset=utf-8, access-control-expose-headers=*, x-acs-trace-id=b46a46e52d6756ed56382b5545824f91}, body={RequestId=E4D16000-BFCA-5309-8B2F-B179B7C9E081, Data={EntityId=123123123, DbName=default, Labels=祁浩, Faces=null}}, statusCode=200}

            // System.out.println(getFaceEntityResponse.toMap());
            // System.out.println("-------------------------");
            // System.out.println(getFaceEntityResponse.getBody().getData());
            return getFaceEntityResponse.getBody().getData() != null;
        } catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }

    }

    public static void deleteFace(String entity){
        log.info("人脸实体删除");
        boolean b = faceIsExist(entity);
        if (!b){
            log.info("人脸实体不存在");
            return;
        }
        DeleteFaceEntityRequest deleteFaceEntityRequest = new DeleteFaceEntityRequest()
                .setDbName("default")
                .setEntityId(entity);
        try {
            DeleteFaceEntityResponse deleteFaceEntityResponse = client.deleteFaceEntity(deleteFaceEntityRequest);
            // System.out.println(deleteFaceEntityResponse.getStatusCode());
            // System.out.println(deleteFaceEntityResponse);
            // System.out.println(deleteFaceEntityResponse.getBody());
            // System.out.println(deleteFaceEntityResponse.getHeaders());
        } catch (Exception e) {
            e.printStackTrace();
            log.warn("人脸实体删除错误  实体id：{}", entity);
        }

    }

    /**
     * base64转化成 inputStream
     *
     * @param base64
     * @return
     */
    public static InputStream base64ToInputStream(String base64) {
        ByteArrayInputStream stream = null;
        // 去掉：data:image/jpeg;base64,
        base64 = base64.replace("data:image/jpeg;base64,", "");
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            stream = new ByteArrayInputStream(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream;
    }
}
