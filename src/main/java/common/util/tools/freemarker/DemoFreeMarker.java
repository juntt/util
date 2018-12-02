package common.util.tools.freemarker;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.NullCacheStorage;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * DemoFreeMarker
 * 
 * @author jieli
 *
 */
public class DemoFreeMarker {
	// FTL模板文件所在路径
	protected static final String FTL_PATH = "/freemarker";
	// FreeMarker应用级设置
	protected static final Configuration CONFIGURATION = new Configuration(Configuration.VERSION_2_3_25);
	static {
		CONFIGURATION.setTemplateLoader(new ClassTemplateLoader(DemoFreeMarker.class, FTL_PATH));
		CONFIGURATION.setDefaultEncoding("UTF-8");
		CONFIGURATION.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		CONFIGURATION.setCacheStorage(NullCacheStorage.INSTANCE);
	}

	/**
	 * 根据FTL模板生成字符串
	 * 
	 * @param templateName
	 *            FTL模型文件名
	 * @param dataModel
	 *            数据模型
	 * @return
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static String generate(final String templateName, Map<String, Object> dataModel)
			throws TemplateException, IOException {
		// 获取模板
		Template template = CONFIGURATION.getTemplate(templateName);
		// 合并模板和数据模型
		Writer out = new StringWriter();
		template.process(dataModel, out);
		return out.toString();
	}

	/** 清空Template缓存 */
	public static void clearTemplateCache() {
		CONFIGURATION.clearTemplateCache();
	}
}
