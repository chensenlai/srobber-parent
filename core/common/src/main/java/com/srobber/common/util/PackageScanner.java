package com.srobber.common.util;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.srobber.common.exeption.WrapException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;


/**
 * 包扫描器,可以扫描某目录下满足条件的类
 *
 * @author chensenlai
 */
@Slf4j
public class PackageScanner {
	
	private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
	private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory(RESOURCE_PATTERN_RESOLVER);
	/**
	 * 资源的格式  ant匹配符号格式
	 */
	private static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";
	
	
	public static Collection<Class<?>> scanPackages(String... packageNames){
		return scanPackages(packageNames, new TrueFilter());
	}
	
	/**
	 * 扫描指定包中所有的类(包括子类)
	 * @param packageNames 包名支持权限定包名和ant风格匹配符(同spring)
	 * @param filter 过滤器
	 * @return 扫描类
	 */
	public static Collection<Class<?>> scanPackages(String[] packageNames, Filter filter){
		Collection<Class<?>> clazzCollection = new HashSet<Class<?>>();

		for (String packageName : packageNames) {
			
			try {
				// 搜索资源
				String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
						+ resolveBasePackage(packageName) + "/" + DEFAULT_RESOURCE_PATTERN;
				Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(packageSearchPath);
				
				for(Resource resource : resources){
					String className = "";
					try {
						if (!resource.isReadable()) {
							continue;
						}
						// 判断是否静态资源
						MetadataReader metaReader = METADATA_READER_FACTORY.getMetadataReader(resource);
						className = metaReader.getClassMetadata().getClassName();

						//这里不要触发类的初始化,否则扫包时会触发类的静态代码块初始化
						//使用线程上下文类加载器
						Class<?> clazz = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
						if(filter.accept(clazz)) {
							clazzCollection.add(clazz);
						}
						
					} catch (ClassNotFoundException e) {
						log.error("package {} scan error: class {} not found", packageName, className);
						throw new RuntimeException(e);
					}
				}
			} catch (IOException e) {
				log.error("package {} scan error. {}", packageName, e.getMessage());
				throw new WrapException(e);
			}
		}
		
		return clazzCollection;
		
	}
	
	/**
	 * 将包名转换成目录名(com.xxx-->com/xxx)
	 * @param basePackage 包名
	 * @return
	 */
	private static String resolveBasePackage(String basePackage) {
		//${classpath}替换掉placeholder 引用的变量值
		String placeHolderReplace = SystemPropertyUtils.resolvePlaceholders(basePackage);
		return ClassUtils.convertClassNameToResourcePath(placeHolderReplace);
	}
	
	
	
	public static class TrueFilter implements Filter{
		@Override
		public boolean accept(Class<?> clazz) {
			return true;
		}
	}
	
	public interface Filter {
		/**
		 * 扫描过滤器
		 * @param clazz
		 * @return true 接受, false 拒绝
		 */
		boolean accept(Class<?> clazz);
	}
}
