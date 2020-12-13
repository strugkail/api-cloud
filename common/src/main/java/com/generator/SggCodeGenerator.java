package com.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.ArrayList;
import java.util.List;

public class SggCodeGenerator {

    public static void main(String[] args) {
        // 3、数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://10.15.194.34:8080/mail_report?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&allowPublicKeyRetrieval=true&verifyServerCertificate=false&useSSL=false");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("123456");
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new ITypeConvert() {
            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, TableField tableField) {
                System.out.println("列名---------------------" + tableField.getColumnName());
                String columnName = tableField.getColumnName();
                if (columnName.equals("update_time") || columnName.equals("create_time")) {
                    return DbColumnType.LOCAL_DATE_TIME;
                }
//                if (columnName.equals("sheet_name")) {
//                    return MyType.BACKEND_SHEETS;
//                }
                return this.processTypeConvert(globalConfig, tableField.getType());
            }

            @Override
            public IColumnType processTypeConvert(GlobalConfig globalConfig, String fieldType) {
                if (fieldType.toLowerCase().contains("test")) {
                    return DbColumnType.STRING;
                }
                return new MySqlTypeConvert().processTypeConvert(globalConfig, fieldType);
            }
        });


//         2、全局配置

        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/common/src/main/java");
        gc.setAuthor("gyf");
        gc.setOpen(false); //生成后是否打开资源管理器
        gc.setFileOverride(true); //重新生成时文件是否覆盖
        gc.setServiceName("%sService"); //去掉Service接口的首字母I
        gc.setIdType(IdType.AUTO); //主键策略
        gc.setDateType(DateType.TIME_PACK);//定义生成的实体类中日期类型
        gc.setSwagger2(false);//开启Swagger2模式


        // 4、包配置
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(null); //模块名
        pc.setParent("com.generator");
        pc.setController("controller");
        pc.setEntity("entity");
        pc.setService("service");
        pc.setMapper("mapper");


        //公共填充字段
        List<TableFill> tableFills = new ArrayList<>();
        tableFills.add(new TableFill("create_time", FieldFill.INSERT));
        tableFills.add(new TableFill("update_time", FieldFill.INSERT_UPDATE));
        tableFills.add(new TableFill("create_user", FieldFill.INSERT));
        tableFills.add(new TableFill("update_user", FieldFill.INSERT_UPDATE));


//        // 5、策略配置
//        StrategyConfig backendSheet = new StrategyConfig();
//        backendSheet.setSuperEntityClass("com.fn.bi.backend.common.entity.FatherReport<ReportOnline>");
//        backendSheet.setInclude("report_online");//对那一张表生成代码
//        backendSheet.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backendSheet.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backendSheet.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backendSheet.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backendSheet.setRestControllerStyle(true); //restful api风格控制器
//        backendSheet.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backendSheet.setTableFillList(tableFills);
//
//        InjectionConfig injectionConfig = new InjectionConfig() {
//            //自定义属性注入:abc
//            //在.ftl(或者是.vm)模板中，通过${cfg.abc}获取属性
//            @Override
//            public void initMap() {
//                Map<String, Object> map = new HashMap<>();
//                map.put("abc", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
//                this.setMap(map);
//            }
//        };
//        //1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backendSheet)
//                .setCfg(injectionConfig)
//                .execute();

//
//        // 5、策略配置
//        StrategyConfig backendSheetHistory = new StrategyConfig();
//        backendSheetHistory.setInclude("backend_sheet_history");//对那一张表生成代码
//        backendSheetHistory.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backendSheetHistory.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backendSheetHistory.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backendSheetHistory.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backendSheetHistory.setRestControllerStyle(true); //restful api风格控制器
//        backendSheetHistory.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backendSheetHistory.setTableFillList(tableFills);
//        // 1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backendSheetHistory)
//                .execute();
//
//
//
//        // 5、策略配置
//        StrategyConfig backend_report = new StrategyConfig();
//        backend_report.setInclude("backend_report");//对那一张表生成代码
//        backend_report.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backend_report.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backend_report.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backend_report.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backend_report.setRestControllerStyle(true); //restful api风格控制器
//        backend_report.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backend_report.setTableFillList(tableFills);
//        // 1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backend_report)
//                .execute();
//
//
//        // 5、策略配置
//        StrategyConfig backend_report_history = new StrategyConfig();
//        backend_report_history.setInclude("backend_report_history");//对那一张表生成代码
//        backend_report_history.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backend_report_history.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backend_report_history.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backend_report_history.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backend_report_history.setRestControllerStyle(true); //restful api风格控制器
//        backend_report_history.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backend_report_history.setTableFillList(tableFills);
//        // 1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backend_report_history)
//                .execute();
//
//
//        // 5、策略配置
//        StrategyConfig backend_user = new StrategyConfig();
//        backend_user.setInclude("backend_user");//对那一张表生成代码
//        backend_user.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backend_user.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backend_user.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backend_user.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backend_user.setRestControllerStyle(true); //restful api风格控制器
//        backend_user.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backend_user.setTableFillList(tableFills);
//        // 1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backend_user)
//                .execute();
//
//
//        // 5、策略配置
//        StrategyConfig backend_user_report = new StrategyConfig();
//        backend_user_report.setInclude("backend_user_report");//对那一张表生成代码
//        backend_user_report.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backend_user_report.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backend_user_report.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backend_user_report.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backend_user_report.setRestControllerStyle(true); //restful api风格控制器
//        backend_user_report.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backend_user_report.setTableFillList(tableFills);
//        // 1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backend_user_report)
//                .execute();


        //backend_permission
        // 5、策略配置

//        StrategyConfig backendSheet = new StrategyConfig();
//        backendSheet.setInclude("permission");//对那一张表生成代码
//        backendSheet.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backendSheet.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backendSheet.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backendSheet.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backendSheet.setRestControllerStyle(true); //restful api风格控制器
//        backendSheet.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backendSheet.setTableFillList(tableFills);
//        //1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backendSheet)
//                .execute();

        //backend_role_permission
        // 5、策略配置
//        StrategyConfig backendSheet = new StrategyConfig();
//        backendSheet.setInclude("role_permission");//对那一张表生成代码
//        backendSheet.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backendSheet.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backendSheet.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backendSheet.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backendSheet.setRestControllerStyle(true); //restful api风格控制器
//        backendSheet.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backendSheet.setTableFillList(tableFills);
//        //1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backendSheet)
//                .execute();

        //backend_role
        // 5、策略配置
//        StrategyConfig backendSheet = new StrategyConfig();
//        backendSheet.setInclude("role");//对那一张表生成代码
//        backendSheet.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backendSheet.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backendSheet.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backendSheet.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backendSheet.setRestControllerStyle(true); //restful api风格控制器
//        backendSheet.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backendSheet.setTableFillList(tableFills);
//        //1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backendSheet)
//                .execute();

        //backend_user
        // 5、策略配置
        StrategyConfig backendSheet = new StrategyConfig();
        backendSheet.setInclude("user");//对那一张表生成代码
        backendSheet.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
        backendSheet.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
        backendSheet.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
        backendSheet.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
        backendSheet.setRestControllerStyle(true); //restful api风格控制器
        backendSheet.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
        backendSheet.setTableFillList(tableFills);
        //1、创建代码生成器
        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
                .setGlobalConfig(gc)
                .setDataSource(dsc)
                .setPackageInfo(pc)
                .setStrategy(backendSheet)
                .execute();


        //backend_user_role
        // 5、策略配置
//        StrategyConfig backendSheet = new StrategyConfig();
//        backendSheet.setInclude("user_role");//对那一张表生成代码
//        backendSheet.setNaming(NamingStrategy.underline_to_camel);//数据库表映射到实体的命名策略
//        backendSheet.setColumnNaming(NamingStrategy.underline_to_camel);//数据库表字段映射到实体的命名策略
//        backendSheet.setTablePrefix(pc.getModuleName() + "_"); //生成实体时去掉表前缀
//        backendSheet.setEntityLombokModel(true); // lombok 模型 @Accessors(chain = true) setter链式操作
//        backendSheet.setRestControllerStyle(true); //restful api风格控制器
//        backendSheet.setControllerMappingHyphenStyle(true); //url中驼峰转连字符
//        backendSheet.setTableFillList(tableFills);
//        //1、创建代码生成器
//        new AutoGenerator().setTemplateEngine(new FreemarkerTemplateEngine())
//                .setGlobalConfig(gc)
//                .setDataSource(dsc)
//                .setPackageInfo(pc)
//                .setStrategy(backendSheet)
//                .execute();


    }

}