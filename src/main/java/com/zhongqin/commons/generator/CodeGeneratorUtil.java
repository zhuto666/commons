package com.zhongqin.commons.generator;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;

import java.util.Collections;
import java.util.List;

/**
 * @author Kevin
 * @version 1.0
 * @date 2024/4/15 22:25 星期一
 */
public class CodeGeneratorUtil {

    /**
     * 生成代码
     *
     * @param url           mysql连接地址
     * @param username      用户名
     * @param password      密码
     * @param tableList     表
     * @param author        作者名
     * @param outputDir     输出目录
     * @param parentPackage 父包名
     * @param moduleName    模块名称
     * @param pathInfo      xml路径
     */
    public static void codeGenerator(String url, String username, String password,
                                     String author, String outputDir,
                                     String parentPackage, String moduleName,
                                     List<String> tableList, String pathInfo) {
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    // 设置作者
                    builder.author(author)
                            // 开启 swagger 模式
                            .enableSwagger()
                            // 禁止弹窗
                            .disableOpenDir()
                            // 指定输出目录
                            .outputDir(StringUtils.isBlank(outputDir) ? System.getProperty("user.dir") : outputDir);
                })
                .packageConfig(builder -> {
                    // 设置父包名
                    builder.parent(parentPackage)
                            // 设置父包模块名
                            .moduleName(moduleName)
                            // 设置mapperXml生成路径
                            .pathInfo(Collections.singletonMap(OutputFile.xml, pathInfo));
                })
                .strategyConfig(builder -> {
                    // 启用 Lombok
                    builder.entityBuilder().enableLombok().idType(IdType.ASSIGN_ID);
                    // 启用 @Mapper 注解
                    builder.mapperBuilder().enableMapperAnnotation().build();
                    // 启用驼峰转连字符样式
                    builder.controllerBuilder().enableHyphenStyle().enableRestStyle();
                    // 设置需要生成的表名
                    tableList.forEach(builder::addInclude);
                })
                .execute();
    }

}
