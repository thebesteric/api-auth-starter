package com.sourceflag.framework.auth.core;

import com.sourceflag.framework.auth.core.exception.UnsupportedEncryptException;
import com.sourceflag.framework.auth.core.processor.EncryptAuthProcessor;
import com.sourceflag.framework.auth.core.processor.MappingProcessor;
import com.sourceflag.framework.auth.starter.ApiAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * ApiAuthInitialization
 *
 * @author Eric Joe
 * @version 1.0
 * @date 2020-11-11 23:59
 * @since 1.0
 */
@Slf4j
@RequiredArgsConstructor
public class ApiAuthInitialization implements ApplicationRunner, ApplicationContextAware {

    private GenericApplicationContext applicationContext;

    private final ApiAuthProperties properties;

    private final List<MappingProcessor> mappingProcessors;

    public static final Map<String, Method> URL_MAPPING = new ConcurrentHashMap<>();

    public static final Set<String> IGNORE_URIS = new HashSet<>();

    static {
        IGNORE_URIS.add("/favicon.ico");
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        // check enable
        if (!properties.isEnable()) {
            log.info("API AUTH is disabled");
            return;
        }

        String encrypt = properties.getEncrypt();

        checkCustomEncryption(encrypt);

        String projectPath = getProjectPath();
        log.info("PROJECT_PATH is {}, ENCRYPT_MODEL is {}", projectPath, encrypt.toUpperCase());

        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        if (url != null) {
            if (url.getProtocol().equals("file")) {
                doScan(new File(projectPath + "/"));
            } else if (url.getProtocol().equals("jar")) {
                try {
                    JarURLConnection connection = (JarURLConnection) url.openConnection();
                    JarFile jarFile = connection.getJarFile();
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry jar = jarEntries.nextElement();
                        if (jar.isDirectory() || !jar.getName().endsWith(".class")) {
                            continue;
                        }
                        String jarName = jar.getName();
                        String classPath = jarName.replaceAll("/", ".");
                        String className = classPath.substring(0, classPath.lastIndexOf("."));
                        processClassFile(className);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // print url_mapping to console
        if (log.isTraceEnabled()) {
            URL_MAPPING.forEach((k, v) -> log.info("API AUTH SCAN {} => {}", k, v.getName()));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (GenericApplicationContext) applicationContext;
    }

    private void checkCustomEncryption(String encrypt) {
        boolean isBuiltInEncryptType = false;
        for (ApiAuthProperties.EncryptType encryptType : ApiAuthProperties.EncryptType.values()) {
            if (encrypt.equalsIgnoreCase(encryptType.name())) {
                isBuiltInEncryptType = true;
                break;
            }
        }
        if (!isBuiltInEncryptType) {
            if (getBean(EncryptAuthProcessor.class) == null) {
                throw new UnsupportedEncryptException("Verify that the EncryptAuthProcessor has been provided");
            }
        }
    }

    private String getProjectPath() {
        ClassLoader defaultClassLoader = ClassUtils.getDefaultClassLoader();
        if (defaultClassLoader != null) {
            String path = Objects.requireNonNull(defaultClassLoader.getResource("")).getPath();
            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                path = path.substring(1);
            }
            return path.replaceAll("%20", " ").replace("target/classes/", "");
        }
        return null;
    }

    private void doScan(File file) {
        if (file.isDirectory()) {
            for (File _file : Objects.requireNonNull(file.listFiles())) {
                doScan(_file);
            }
        } else {
            String filePath = file.getPath();
            int index = filePath.lastIndexOf(".");
            if (index != -1 && filePath.substring(index).equals(".class")) {
                int i = filePath.indexOf("target\\classes");
                if (i != -1) {
                    filePath = filePath.substring(i + 15);
                    String classPath = filePath.replaceAll("\\\\", ".");
                    String className = classPath.substring(0, classPath.lastIndexOf("."));
                    processClassFile(className);
                }
            }
        }
    }

    private void processClassFile(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Controller.class) || clazz.isAnnotationPresent(RestController.class)) {
                RequestMapping classRequestMapping = clazz.getAnnotation(RequestMapping.class);
                String[] classRequestMappingUrls = null;
                if (classRequestMapping != null) {
                    classRequestMappingUrls = classRequestMapping.value();
                }
                for (Method method : clazz.getDeclaredMethods()) {

                    // TODO Determine if there is an @ApiAuth annotation

                    for (MappingProcessor mappingProcessor : mappingProcessors) {
                        if (mappingProcessor.supports(method)) {
                            mappingProcessor.processor(classRequestMappingUrls);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException ignored) {
            return null;
        }
    }
}
