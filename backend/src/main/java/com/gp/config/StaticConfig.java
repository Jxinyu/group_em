package com.gp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticConfig implements WebMvcConfigurer {

    @Value("${global.file_upload_path}")
    private String FILE_UPLOAD_PATH;

    @Value("${global.notice_img_upload_path}")
    private String NOTICE_IMG_UPLOAD_PATH;


    /**
     * 添加资源处理程序
     *
     * @param registry 注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //添加默认的静态资源访问路径
        registry.addResourceHandler("/api/gp/static/*")
                .addResourceLocations("classpath:static/","classpath:META-IFA/resources/",
                        "classpath:resources/","classpath:public/","classpath:/");
        registry.addResourceHandler("**.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // 文件下载资源映射
        registry.addResourceHandler("/api/gp/file/download/**")
                .addResourceLocations("file:"+ FILE_UPLOAD_PATH);
        // 公告图片资源映射
        registry.addResourceHandler("/api/gp/notice/img/**")
                .addResourceLocations("file:"+ NOTICE_IMG_UPLOAD_PATH);

    }
}
