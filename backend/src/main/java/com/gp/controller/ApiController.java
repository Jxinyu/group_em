package com.gp.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageInfo;
import com.gp.filter.JWTFilter;
import com.gp.mapper.*;
import com.gp.pojo.*;
import com.gp.service.*;
import com.gp.utils.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.Permission;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresGuest;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author ljb
 * @create 2023/6/2
 */
@RestController
@RequestMapping("/api/gp/")
@Slf4j
public class ApiController {

    // 封装数据
    private static final Map<String, Object> map = new HashMap<>();

    // 存储验证码有效值
    private static final Map<String, String> codeMap = new ConcurrentHashMap<>();

    // 文件上传路径
    @Value("${global.file_upload_path}")
    private String FILE_UPLOAD_PATH;

    @Value("${global.notice_img_upload_path}")
    private String NOTICE_IMG_UPLOAD_PATH;

    @Resource
    private UserInfMapper userInfMapper;

    @Resource
    private NoticeImgMapper noticeImgMapper;

    @Resource
    private DocumentInfService documentInfService;

    @Resource
    private NoticeInfService noticeInfService;

    @Resource
    private NoticeInfMapper noticeInfMapper;

    @Resource
    private DeptInfService deptInfService;

    @Resource
    private EmployeeInfService employeeInfService;

    @Resource
    private EmployeeInfMapper employeeInfMapper;

    @Resource
    private JobInfService jobInfService;

    @Resource
    private UserInfService userInfService;

    @Resource
    private WaitNoticeMapper waitNoticeMapper;

    @Resource
    private EmailService emailService;

    @Resource
    private PushedNoticeMapper pushedNoticeMapper;

    @Resource
    private ChattingMapper chattingMapper;

    @Resource
    private GroupChatMapper groupChatMapper;

    //region 李锦彪

    /**
     * 生成验证码
     *
     * @param request 请求
     * @return {@link R}
     */
    @GetMapping("/login/code")
    public R createCode(HttpServletRequest request) {
        log.info("获取图片验证码");
        //定义图形验证码的长、宽、验证码字符数、干扰线宽度
        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(100, 40, 4, 5);

        //verifyCode = captcha.getCode();
        // 设置10秒过期时间
        addCode(request.getSession().getId(), captcha.getCode(), 1000 * 60);
        return R.ok(200, null, captcha.getImageBase64());
    }

    /**
     * 添加验证码
     *
     * @param sessionId      用户代理
     * @param code           验证码
     * @param expirationTime 过期时间 毫秒
     */
    public static void addCode(String sessionId, String code, long expirationTime) {
        log.info("添加验证码：{}", code);
        codeMap.put(sessionId, code);
        // 设置过期时间，可以使用定时任务或者其他方式清理过期的验证码
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(() -> {
            codeMap.remove(sessionId);
        }, expirationTime, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取验证码
     *
     * @param sessionId 用户代理
     * @return {@link String}
     */
    public static String getCode(String sessionId) {
        return codeMap.get(sessionId);
    }

    /**
     * 登录
     *
     * @param logMap  参数：loginName   password   code
     * @param request 请求
     * @return {@link R}
     */
    @PostMapping("/users/login")
    public R loginByUP(@RequestBody Map<String, Object> logMap, HttpServletRequest request) {
        log.info("用户登录  账号： {}  密码：{}  验证码：{}", logMap.get("loginName"), logMap.get("password"), logMap.get("code"));

        if (getCode(request.getSession().getId()) == null) {
            return R.error(400, "验证码失效");
        }
        if (!(getCode(request.getSession().getId()).equals(logMap.get("code")))) {
            return R.error(400, "验证码错误");
        }

        // 盐值加密，对比
        String pwd = MyUtils.PWD(logMap.get("loginName").toString(), logMap.get("password").toString());
        QueryWrapper<UserInf> wrapper = new QueryWrapper<>();
        wrapper.eq("Loginname", logMap.get("loginName"))
                .eq("PASSWORD", pwd);
        boolean exists = userInfMapper.exists(wrapper);
        if (!exists) {
            return R.error(400, "账号不存在,或密码错误");
        }

        map.clear();
        String token = JWTUtil.createToken((String) logMap.get("loginName"));
        map.put("token", token);
        return R.ok(map);
    }

    /**
     * 人脸登录
     *
     * @param reMap 参数: base(图片的base64编码)
     * @return {@link R}
     */
    @PostMapping("/users/login/face")  // map中只有k = base
    public R loginByFace(@RequestBody Map<String, Object> reMap) {
        log.info("人脸登录");

        try {
            String entityId = SearchFace.JudgementFace((String) reMap.get("base"));
            if (entityId == null) {
                return R.error(400, "人脸未注册");
            }
            QueryWrapper<UserInf> wrapper = new QueryWrapper<>();
            wrapper.eq("Loginname", entityId);
            boolean exists = userInfMapper.exists(wrapper);
            if (!exists) {
                return R.error(400, "对不起，请使用账号登录");
            }
            String token = JWTUtil.createToken(entityId);
            map.clear();
            map.put("token", token);
            log.info("{} 登录成功", entityId);
            return R.ok(map);

        } catch (Exception e) {
            e.printStackTrace();
            log.info("base  出错");
            return R.error(400, "请稍后再试");
        }
    }

    @GetMapping("/user/logout")
    public R logout(HttpServletRequest request) {
        String header = request.getHeader(JWTFilter.TOKE_NAME);
        String username = JWTUtil.getUsername(header);
        JWTToken jwtToken = new JWTToken(header);
        Subject subject = SecurityUtils.getSubject();
        subject.logout();

        return R.ok();
    }


    /**
     * 注册人脸信息
     *
     * @param reMap   – 参数: base(图片的base64编码)
     * @param request 请求
     * @return {@link R}
     */
    @PostMapping("/user/register/face")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R registerFace(@RequestBody Map<String, Object> reMap, ServletRequest request) {
        log.info("人脸注册");
        // 获取用户登录名
        HttpServletRequest request1 = (HttpServletRequest) request;
        String token = request1.getHeader(JWTFilter.TOKE_NAME);
        String loginname = JWTUtil.getUsername(token);
        // 获取用户名
        UserInf userInf = new UserInf();
        userInf.setLoginname(loginname);
        PageInfo<UserInf> info = userInfService.queryOneUser(userInf, 1, 1);
        if (info.getList().size() == 0) {
            return R.error(400, "查无此人，请重新登录");
        }
        String username = info.getList().get(0).getUsername();

        try {
            Map<String, Object> resMap = SearchFace.addFace(String.valueOf(reMap.get("base")), loginname, username);
            if (Integer.parseInt(resMap.get("statusCode").toString()) != 200) {
                return R.error(400, resMap.get("Message").toString());
            }
            return R.ok();
        } catch (Exception e) {
            return R.error(400, e.getMessage());
        }
    }

    /**
     * 通过登录名获取 获取用户信息
     *
     * @return {@link R}
     */
    @GetMapping("/users/info")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R queryUsersInfoByLoginName(ServletRequest request) {
        log.info("获取用户信息");
        // 从请求头拿去token
        HttpServletRequest req = (HttpServletRequest) request;
        String remoteAddr = req.getRemoteAddr();
        log.info("用户的ip地址：{}", remoteAddr);
        String token = req.getHeader(JWTFilter.TOKE_NAME);

        String username = JWTUtil.getUsername(token);
        QueryWrapper<UserInf> wrapper = new QueryWrapper<>();
        wrapper.eq("Loginname", username);
        UserInf userInf = userInfMapper.selectOne(wrapper);
        List<String> list = new ArrayList<>();
        list.add(String.valueOf(userInf.getStatus()));
        map.clear();
        map.put("roles", list);
        map.put("loginName", username);
        return R.ok(map);
    }

    /**
     * 通过登录名 用户信息修改
     *
     * @return {@link R}
     */
    @PostMapping("/user/info/change")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R userInfoChangeByLoginName(@RequestBody UserInf userInf, ServletRequest servletRequest) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String token = request.getHeader(JWTFilter.TOKE_NAME);
        String loginName = JWTUtil.getUsername(token);
        userInf.setLoginname(loginName)
                .setPassword(MyUtils.PWD(loginName, userInf.getPassword()));
        boolean b = userInfMapper.updateUserByLoginName(userInf);
        if (!b) {
            return R.error(400, "稍后重试");
        }
        return R.ok();
    }

    /**
     * 上传文档文件
     *
     * @param title   标题
     * @param remark  备注
     * @param file    文件
     * @param request 请求
     * @return {@link R}
     */
    @PostMapping("/user/upload/docu")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R uploadDocumentFile(@RequestParam("title") String title,  // 标题
                                @RequestParam("remark") String remark,  // 文件描述
                                @RequestParam("file") MultipartFile file,  // 文件
                                ServletRequest request) {
        log.info("文件上传");

        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String token = servletRequest.getHeader(JWTFilter.TOKE_NAME);
        if (token == null) {
            return R.error(400, "请重新登录后，再上传");
        }

        // 判断文件大小
        long size = file.getSize();
        if (size > 50 * 1024 * 1024) {
            return R.error(400, "上传的文件过大，应保持在50MB以内");
        }
        if (size == 0) {
            return R.error(400, "上传的文件为空");
        }

        String username = JWTUtil.getUsername(token);  // 获取当前登录的用户账号
        String fileName = file.getOriginalFilename();  // 获取文件名

        int length = title.length();
        int length1 = remark.length();
        if (length > 30) {
            return R.error(400, "标题过长, 文件：" + fileName);
        }
        if (length1 > 220) {
            return R.error(400, "描述过长, 文件：" + fileName);
        }

        if (fileName == null) {  // 判断文件名是否为空
            return R.error(400, "文件名为空");
        }
        String[] split = fileName.split("\\.");
        String type = split[split.length - 1];
        String fileNameNotContainsType = split[0];

        // 定义哪些文件类型可以上传
        String[] contains = {"xlsx", "doc", "pdf", "txt", "docx", "sql", "pptx",
                "rar", "zip", "xls", "jpg", "gif", "png", "md", "jpeg"};
        // 判断上传的文件类型在不在定义的类型里
        if (!(Arrays.asList(contains).contains(type))) {
            return R.error(400, "暂不支持, 文件：" + fileName + "文件类型上传, " + type);
        }

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            // 拿到文件流
            inputStream = file.getInputStream();
            // 以用户名+文件名+时间戳生成UUID
            String fileNameUUID = UUID.randomUUID(true).toString() + new Date().getTime() + "." + type;
            // 拿到文件路径
            File targetFile = new File(FILE_UPLOAD_PATH + fileNameUUID);

            try {
                // 存储数据库
                DocumentInf inf = new DocumentInf();
                inf.setTitle(title)
                        .setFilename(fileNameNotContainsType)
                        .setRemark(remark)
                        .setFileType(type)
                        .setCreateDate(String.valueOf(DateUtil.date()))
                        .setFilePath(fileNameUUID);
                int i = documentInfService.insertUploadFile(inf, username);
                if (i == 0) {
                    return R.error(400, "存储出错, 文件：" + fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return R.error(400, "存储出错, 文件：" + fileName);
            }

            // 判断文件父目录是否存在
            if (!targetFile.getParentFile().exists()) {
                // 不存在就创建一个
                targetFile.getParentFile().mkdir();
            }
            // 获取文件的输出流
            outputStream = new FileOutputStream(targetFile);
            // 拷贝到文件夹中
            FileCopyUtils.copy(inputStream, outputStream);
            return R.ok("文件：" + fileName + "，上传成功！");
        } catch (IOException e) {
            e.printStackTrace();
            return R.error(400, "文件：" + fileName + ", 上传失败");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close(); // 关闭输入流
                } catch (IOException e) {
                    log.info("关闭输入流失败：" + e.getMessage());
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close(); // 关闭输出流
                } catch (IOException e) {
                    e.printStackTrace();
                    log.info("关闭输出流失败：" + e.getMessage());
                }
            }
        }
    }

    /**
     * 查询文档列表
     *
     * @param documentInf 文档正
     * @param currentPage 当前页面
     * @param size        大小
     * @return {@link R}
     */
    @GetMapping("/table/document")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R queryDocumentList(DocumentInf documentInf,
                               @RequestParam("currentPage") int currentPage,
                               @RequestParam("size") int size) {
        log.info("查询文档列表  当前页： {}  页大小： {}   参数：{}", currentPage, size, documentInf.toString());
        PageInfo<DocumentInf> pageInfo = documentInfService
                .queryDocumentListByPage(documentInf, currentPage, size);
        map.clear();
        map.put("data", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return R.ok(map);
    }

    /**
     * 删除各类  通过id
     *
     * @param idString : 121&120&
     * @return {@link R}
     */
    @DeleteMapping("/table/{type}")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R deleteById(@RequestBody Map<String, String> idString, @PathVariable("type") String type) {
        Collection<Integer> listId;
        try {
            String[] split = idString.get("idString").split("&");
            listId = Arrays.stream(split)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return R.error(400, "请刷新后重试");
        }
        boolean b = false;
        switch (type) {
            case "document": {
                b = documentInfService.removeBatchByIds(listId);
                break;
            }
            case "notice": {
                b = noticeInfService.removeBatchByIds(listId);
                break;
            }
            case "user": {
                // 删除人脸信息
                for (Integer integer : listId) {
                    // 获取entity = loginname
                    if (integer == null) {
                        break;
                    }
                    UserInf byId = userInfService.getById(integer);
                    SearchFace.deleteFace(byId.getLoginname());
                }
                b = userInfService.removeBatchByIds(listId);
                break;
            }
            case "dept": {
                b = deptInfService.removeBatchByIds(listId);
                break;
            }
            case "job": {
                b = jobInfService.removeBatchByIds(listId);
                break;
            }
            case "employee": {
                b = employeeInfService.removeBatchByIds(listId);
                break;
            }
        }
        if (b) {
            return R.ok();
        }
        return R.error(400, "请刷新重试");

    }

    /**
     * 更新文档通过id
     *
     * @param documentInf 文档正
     * @return {@link R}
     */
    @PutMapping("/table/document")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R updateDocumentById(@RequestBody DocumentInf documentInf) {
        log.info("文档修改");
        boolean b = documentInfService.updateById(documentInf);
        if (b) {
            return R.ok();
        }
        return R.error(400, "修改错处，刷新重试");
    }

    /**
     * 下载文件通过文件id  返回下载的路径
     *
     * @return {@link R}
     */
    @GetMapping("/table/download/{id}")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R downloadFileById(@PathVariable Integer id) {
        QueryWrapper<DocumentInf> wrapper = new QueryWrapper<>();
        wrapper.select("Filename", "FilePath", "file_type").eq("ID", id);
        DocumentInf documentInf = documentInfService.getOne(wrapper);

        String path = "/file/download/" + documentInf.getFilePath();

        return R.ok(path);
    }

    /**
     * 查询通知列表
     *
     * @param noticeInf   注意到正无穷
     * @param currentPage 当前页面
     * @param size        大小
     * @return {@link R}
     */
    @GetMapping("/table/notice")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R queryNoticeList(NoticeInf noticeInf,
                             @RequestParam("currentPage") int currentPage,
                             @RequestParam("size") int size) {
        log.info("公告查询 页码： {}， 大小：{}, 对象:{}", currentPage, size, noticeInf.toString());
        PageInfo<NoticeInf> pageInfo = noticeInfService.queryNoticeList(noticeInf, currentPage, size);
        map.clear();
        map.put("data", pageInfo.getList());
        map.put("total", pageInfo.getTotal());
        return R.ok(map);
    }


    /**
     * 更新通知通过id
     *
     * @param noticeInf 注意到正无穷
     * @return {@link R}
     */
    @PutMapping("/table/notice")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R updateNoticeById(@RequestBody NoticeInf noticeInf, ServletRequest request) {
        log.info("公告更新： {}", noticeInf.toString());
        HttpServletRequest request1 = (HttpServletRequest) request;
        String token = request1.getHeader(JWTFilter.TOKE_NAME);
        String loginName = JWTUtil.getUsername(token);
        noticeInf.setCreateDate(String.valueOf(DateUtil.date()));
        boolean b = noticeInfService.updateNoticeById(noticeInf, loginName);
        if (b) {
            return R.ok();
        }
        return R.error(400, "刷新重试");
    }

    /**
     * 插入通知
     *
     * @return {@link R}
     */
    @PostMapping("/table/notice")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R insertNotice(@RequestBody NoticeInf noticeInf, ServletRequest request) {
        log.info("新增通告");
        HttpServletRequest request1 = (HttpServletRequest) request;
        String token = request1.getHeader(JWTFilter.TOKE_NAME);
        String loginName = JWTUtil.getUsername(token);
        noticeInf.setCreateDate(String.valueOf(DateUtil.date()));
        boolean b = noticeInfService.insertNotice(noticeInf, loginName);
        if (b) {
            return R.ok();
        }
        return R.error(400, "请稍后重试");
    }

    /**
     * 上传通知img
     *
     * @param file 文件
     * @return {@link R}
     */
    @PostMapping("/table-chat/{chat_notice}/{file_type}")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R uploadNoticeImg(@PathVariable("chat_notice") String chat_notice,
                             @PathVariable("file_type") String file_type,
                             @RequestParam("file") MultipartFile file) {
        log.info("上传 通知的图像文件");

        long size = file.getSize();
        if (size / 1024 / 1024 > 10) {
            return R.error(400, "文件过大，建议8MB以内");
        }else if(size == 0){
            return R.error(400, "文件为空");
        }

        // 获取文件名
        String filename = file.getOriginalFilename();

        String uuid = UUID.randomUUID(true).toString();
        String[] fileType = filename.split("\\.");

        // 定义哪些文件类型可以上传
        String[] contains = {"jpg", "gif", "png", "jpeg"};
        // 判断上传的文件类型在不在定义的类型里
        if (!(Arrays.asList(contains).contains(fileType[fileType.length - 1]))) {
            return R.error(400, "暂不支持, " + filename + "文件类型发送");
        }

        // 生成url
        String store_filename = uuid + new Date().getTime() + "." + fileType[fileType.length - 1];

        // 保存文件
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            // 拿到文件流
            inputStream = file.getInputStream();
            // 拿到文件存储目录
            File targetFile = new File(NOTICE_IMG_UPLOAD_PATH + store_filename);
            // 判断父目录是否存在，不存在就创建一个
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdir();
            }
            // 获取输出流
            outputStream = new FileOutputStream(targetFile);
            // 将文件拷贝到目录中
            FileCopyUtils.copy(inputStream, outputStream);

            // if (chat_notice.equals("notice") && file_type.equals("img")){
                try {
                    // 存数据库
                    NoticeImg noticeImg = new NoticeImg();
                    noticeImg.setName(filename).setUrl(store_filename);
                    int b = noticeImgMapper.insert(noticeImg);
                    if (b <= 0) {
                        return R.error(400, "图片上传失败，稍后重试");
                    }
                    return R.ok(200, "success", "/api/v1/notice/img/" + store_filename);
                } catch (Exception e) {
                    e.printStackTrace();
                    return R.error(400, "图片上传失败，稍后重试");
                }
            // }
            //
            // log.info("上传聊天文件");
            // return R.ok();

        } catch (IOException e) {
            e.printStackTrace();
            return R.error(400, "图片上传失败，稍后重试");
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 推送通知
     * scope解析：
     * 1：管理员
     * 2：普通用户
     * 3：全部用户
     * 4：全部员工
     * 5：全部人员
     * <p>
     * pushWay解析：
     * 1：普通方式推送
     * 2：邮件方式推送
     *
     * @param waitNotice
     * @param type       是否再次推送
     * @return {@link R}
     */
    @PostMapping("/table/notice/push/{type}")
    @RequiresPermissions(value = {"1"})
    public R pushNoticeToUser(@RequestBody WaitNotice waitNotice, @PathVariable("type") boolean type) {

        //先判断此次的推送是否已经推送过

        int exist = waitNoticeMapper.isExist(waitNotice);
        if (exist > 0) {
            if (!type) {  // type为false，说明已经推送过，需要返回给用户，告诉用户已经推送过。
                return R.ok(201, "此种方式已经推送过目标人群", null);
            }
        }
        // 当type为true时，说明用户想再次推送

        // 拿到要推送公告的id和推送方式，插入已经推送的表中,
        try {
            waitNotice.setPushDate(String.valueOf(DateUtil.date()));
            int insert = waitNoticeMapper.insert(waitNotice);
            if (insert <= 0) {
                return R.error(400, "推送失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.error(400, "推送失败");
        }

        // 邮件推送方式
        if (waitNotice.getPushWay() != null && waitNotice.getPushWay() == 2) {
            System.out.println("邮件推送");
            Set<String> emails = null;
            UserInf userInf = new UserInf();

            try {
                switch (waitNotice.getScope()) {
                    case 1: {
                        System.out.println("管理员");
                        userInf.setStatus(1);
                        emails = userInfMapper.queryOneUser(userInf).stream().map(UserInf::getEmail).collect(Collectors.toSet());
                        break;
                    }
                    case 2: {
                        System.out.println("普通用户");
                        userInf.setStatus(2);
                        emails = userInfMapper.queryOneUser(userInf).stream().map(UserInf::getEmail).collect(Collectors.toSet());
                        break;
                    }
                    case 3: {
                        System.out.println("全部用户");
                        emails = userInfMapper.queryOneUser(userInf).stream().map(UserInf::getEmail).collect(Collectors.toSet());
                        break;
                    }
                    case 4: {
                        System.out.println("全部员工");
                        emails = employeeInfMapper.queryOneEmployee(null).stream().map(EmployeeInf::getEmail).collect(Collectors.toSet());
                        break;
                    }
                    default: {
                        System.out.println("全部人员");
                        emails = userInfMapper.queryOneUser(userInf).stream().map(UserInf::getEmail).collect(Collectors.toSet());
                        emails.addAll(employeeInfMapper.queryOneEmployee(null).stream().map(EmployeeInf::getEmail).collect(Collectors.toSet()));
                        break;
                    }
                }
            } catch (Exception e) {
                R.error(400, "推送失败");
            }

            //查询公告信息
            NoticeInf inf = new NoticeInf();
            inf.setId(waitNotice.getNId());
            inf = noticeInfMapper.queryNoticeList(inf).get(0);

            // 开始发送邮件
            if (emails != null) {
                for (String email : emails) {
                    emailService.groupEmail(email, inf.getTitle(), inf.getContent(), null);
                }
            }

            return R.ok();
        }

        return R.ok();
    }

    /**
     * 根据公告id获取，该公告已经推送的方式人群
     *
     * @param nId
     * @return {@link R}
     */
    @GetMapping("table/notice/pushed/{nId}")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R getPushedNoticeById(@PathVariable("nId") Integer nId) {
        List<WaitNotice> noticeId = waitNoticeMapper.getListByNoticeId(nId);
        map.clear();
        map.put("data", noticeId);
        return R.ok(map);
    }

    /**
     * 长轮询，请求当前用户是否有公告
     *
     * @param request
     * @return {@link R}
     */
    @GetMapping("/long-polling")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R longPollingGetPushNotice(HttpServletRequest request) {

        String token = request.getHeader(JWTFilter.TOKE_NAME);
        String username = JWTUtil.getUsername(token);
        UserInf userInf = userInfMapper.queryOneUserByUsername(username);
        //获取要推的公告
        List<NoticeInf> pushed = noticeInfMapper.getPushedByUsernameAndStatus(username, userInf.getStatus());
        map.clear();
        map.put("data", pushed);
        return R.ok(map);

    }

    /**
     * 标记已读的通知
     *
     * @param nId     n id
     * @param request 要求
     * @return {@link R}
     */
    @PostMapping("/user/readed/pushed/noticed/{id}")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R signReadedNotice(@PathVariable("id") Integer nId, HttpServletRequest request) {
        String username = JWTUtil.getUsername(request.getHeader(JWTFilter.TOKE_NAME));
        // 标记已经获取的公告
        pushedNoticeMapper.insertPushed(username, nId);
        return R.ok();
    }


    //endregion

    //region 祁浩

    /**
     * 查询部门
     *
     * @param deptInf     部门正
     * @param currentPage 当前页面
     * @param size        大小
     * @return {@link R}
     */
    @GetMapping("/table/dept")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R queryOneDept(DeptInf deptInf,
                          @RequestParam("currentPage") int currentPage,
                          @RequestParam("size") int size) {
        PageInfo<DeptInf> info = deptInfService.queryOneDept(deptInf, size, currentPage);
        map.clear();
        map.put("data", info.getList());
        map.put("total", info.getTotal());
        return R.ok(map);
    }

    /**
     * 更新部门
     *
     * @param deptInf 部门正
     * @return {@link R}
     */
    @PutMapping("/table/dept")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R updateDept(@RequestBody DeptInf deptInf) {
        int i = deptInfService.updateDept(deptInf);
        if (i <= 0) {
            return R.error(400, "修改失败");
        }
        return R.ok();
    }

    /**
     * 添加部门
     *
     * @param deptInf 部门正
     * @return {@link R}
     */
    @PostMapping("/table/dept")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R addDept(@RequestBody DeptInf deptInf) {
        int i = deptInfService.addDept(deptInf);
        if (i <= 0) {
            return R.error(400, "增加失败");
        }
        return R.ok();
    }

    /**
     * 查询职位
     *
     * @param jobInf      工作正
     * @param currentPage 当前页面
     * @param size        大小
     * @return {@link R}
     */
    @GetMapping("/table/job")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R queryOneJob(JobInf jobInf,
                         @RequestParam("currentPage") int currentPage,
                         @RequestParam("size") int size) {
        PageInfo<JobInf> info = jobInfService.queryOneJob(jobInf, size, currentPage);

        map.clear();
        map.put("data", info.getList());
        map.put("total", info.getTotal());
        return R.ok(map);
    }

    /**
     * 更新职位
     *
     * @param jobInf 工作正
     * @return {@link R}
     */
    @PutMapping("/table/job")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R updateJob(@RequestBody JobInf jobInf) {
        int i = jobInfService.updateJob(jobInf);
        if (i <= 0) {
            return R.error(400, "修改失败");
        }
        return R.ok();
    }


    /**
     * 添加职位
     *
     * @param jobInf 工作正
     * @return {@link R}
     */
    @PostMapping("/table/job")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R addJob(@RequestBody JobInf jobInf) {
        int i = 0;
        try {
            i = jobInfService.addJob(jobInf);
        } catch (Exception e) {
            return R.error(400, "添加失败");
        }
        if (i <= 0) {
            return R.error(400, "添加失败");
        }
        return R.ok();
    }

    //endregion

    //region 李飞龙


    /**
     * 查询用户
     *
     * @param userInf     用户正
     * @param currentPage 当前页面
     * @param size        大小
     * @return {@link R}
     */
    @GetMapping("/table/user")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R queryListUser(UserInf userInf,
                           @RequestParam("currentPage") int currentPage,
                           @RequestParam("size") int size) {
        PageInfo<UserInf> info = userInfService.queryOneUser(userInf, size, currentPage);
        map.clear();
        map.put("data", info.getList());
        map.put("total", info.getTotal());
        return R.ok(map);
    }

    /**
     * 更新用户
     *
     * @param userInf 用户正
     * @return {@link R}
     */
    @PutMapping("/table/user")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R updateUser(@Valid @RequestBody UserInf userInf) {
        if (userInf.getPassword().equals("")) {
            userInf.setPassword(null);
        }
        userInf.setPassword(MyUtils.PWD(userInf.getLoginname(), userInf.getPassword()));
        boolean b = userInfService.updateById(userInf);
        if (!b) {
            return R.error(400, "修改失败");
        }
        return R.ok();
    }


    /**
     * 添加用户
     *
     * @param userInf 用户正
     * @return {@link R}
     */
    @PostMapping("/table/user")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R addUser(@Valid @RequestBody UserInf userInf) {
        userInf.setCreatedate(String.valueOf(DateUtil.date()))
                .setPassword(MyUtils.PWD(userInf.getLoginname(), userInf.getPassword()));
        int i = 0;
        try {
            i = userInfService.addUser(userInf);
        } catch (Exception e) {
            return R.error(400, "增加失败,请检查账号是否已经存在");
        }
        if (i <= 0) {
            return R.error(400, "增加失败");
        }
        return R.ok();
    }

    //endregion

    //region 臧毅华

    /**
     * 员工查询
     *
     * @param employeeInf 员工正
     * @param currentPage 当前页面
     * @param size        大小
     * @return {@link R}
     */
    @GetMapping("/table/employee")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R queryOneEmployee(EmployeeInf employeeInf,
                              @RequestParam("currentPage") int currentPage,
                              @RequestParam("size") int size) {
        PageInfo<EmployeeInf> info = employeeInfService.queryOneEmployee(employeeInf, size, currentPage);
        map.clear();
        map.put("data", info.getList());
        map.put("total", info.getTotal());
        return R.ok(map);
    }

    /**
     * 更新员工
     *
     * @param employeeInf 员工正
     * @return {@link R}
     */
    @PutMapping("/table/employee")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R updateEmployee(@Valid @RequestBody EmployeeInf employeeInf) {
        int i = employeeInfService.updateEmployee(employeeInf);
        if (i <= 0) {
            return R.error(400, "修改失败");
        }
        return R.ok();
    }


    /**
     * 添加员工
     *
     * @param employeeInf 员工正
     * @return {@link R}
     */
    @PostMapping("/table/employee")
    @RequiresPermissions(value = {"1"}, logical = Logical.OR)
    public R addEmployee(@Valid @RequestBody EmployeeInf employeeInf) {
        employeeInf.setCreateDate(String.valueOf(DateUtil.date()));
        int i = employeeInfService.addEmployee(employeeInf);
        if (i <= 0) {
            return R.error(400, "增加失败");
        }
        return R.ok();
    }

    //endregion

    //region chatting

    /**
     * 获取隐私历史聊天记录
     *
     * @param key_username  密钥用户名
     * @param side_username 侧面用户名
     * @return {@link R}
     */
    @GetMapping("/chat/{key}/{side}")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R getPrivacyHistoryChatRecords(@PathVariable("key") String key_username,
                                          @PathVariable("side") String side_username) {
        log.info("获取历史聊天记录：{}<====>{}", key_username, side_username);
        List<Chatting> keyAndSide = chattingMapper.getChatByKeyAndSide(key_username, side_username);

        map.clear();
        map.put("data", keyAndSide);
        return R.ok(map);
    }


    /**
     * 获取群历史聊天记录
     *
     * @param chatName 聊天室名称
     * @return {@link R}
     */
    @GetMapping("/chat/group/{chatName}/his")
    @RequiresPermissions(value = {"1", "2"}, logical = Logical.OR)
    public R getGroupHistoryChatRecords(@PathVariable("chatName") String chatName) {
        log.info("获取历史聊天记录：<====>{}", chatName);
        List<GroupChat> groupChats = groupChatMapper.getChatByUsernameAndChatName(chatName);

        map.clear();
        map.put("data", groupChats);
        return R.ok(map);
    }

    //endregion

}
