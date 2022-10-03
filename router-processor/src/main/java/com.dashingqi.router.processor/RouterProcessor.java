package com.dashingqi.router.processor;

import com.dashingqi.router.annotation.Route;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

/**
 * @author zhangqi61
 * @Router 注解处理器
 * @since 2022/9/27
 */
@AutoService(Processor.class)
public class RouterProcessor extends AbstractProcessor {

    /**
     * TAG
     */
    private static final String TAG = "RouterProcessor";

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
    }

    /**
     * 告诉编译器当前注解处理器要处理的注解
     *
     * @return 注解集合
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(Route.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 避免重复使用
        if (roundEnvironment.processingOver()) {
            return false;
        }

        System.out.println(TAG + ">>>>> processor start");
        // 获取标注@Route注解类的信息
        Set<? extends Element> routeElement = roundEnvironment.getElementsAnnotatedWith(Route.class);

        System.out.println("routeElement size is " + routeElement.size());
        // 当没有收集到Route注解类的信息时，就直接返回
        if (routeElement == null || routeElement.size() < 1) {
            return false;
        }

        //============生成类文件
        StringBuilder sb = new StringBuilder();
        // 添加包名
        sb.append("package com.dashingqi.router.mapping;\n\n");

        // 添加导入的包名
        sb.append("import java.util.HashMap;\n");
        sb.append("import java.util.Map;\n\n");

        // 添加类信息
        String className = "RoutersMapping_" + System.currentTimeMillis();
        sb.append("  public class " + className + " {\n\n");

        // 构建方法
        sb.append("      public static Map<String, String> get() { \n\n");

        // 构建方法实现
        sb.append("        HashMap<String, String> mapping = new HashMap<>();\n\n");

        for (Element element : routeElement) {
            final TypeElement typeElement = (TypeElement) element;
            final Route routeAnnotation = typeElement.getAnnotation(Route.class);
            if (routeAnnotation == null) {
                continue;
            }

            // 获取path参数
            String path = routeAnnotation.path();
            // 获取description参数
            String description = routeAnnotation.description();

            // 获取当前注解的全类名
            String qualifiedName = typeElement.getQualifiedName().toString();

            System.out.println("path is " + path);
            System.out.println("description is " + description);
            System.out.println("qualifiedName is " + qualifiedName);
            sb.append("        mapping.put(")
                    .append("\"" + path + "\"")
                    .append(", ")
                    .append("\"" + qualifiedName + "\"")
                    .append(");\n\n");

        }

        sb.append("        return mapping;\n\n");
        sb.append("    }\n\n");
        sb.append("}");

        System.out.println(sb.toString());

        String classFullName = "com.dashingqi.router.mapping." + className;
        Writer writer = null;
        // 写入文件
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(classFullName);
            writer = sourceFile.openWriter();
            writer.write(sb.toString());
            writer.flush();

        } catch (Exception exception) {
            exception.printStackTrace();

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


        System.out.println(TAG + ">>>>> processor end");


        return true;
    }
}
