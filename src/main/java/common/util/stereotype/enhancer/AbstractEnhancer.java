package common.util.stereotype.enhancer;

import net.sf.cglib.proxy.Enhancer;

/**
 * CGLIB动态代理类
 * 
 * @author jieli
 *
 */
public abstract class AbstractEnhancer<U, V> {
	/** 创建代理类对象，增强委托类 */
	private Object newProxyInstance(Object obj) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(obj.getClass()); // 将委托类作为自己的父类
		enhancer.setCallback(new JMethodInterceptor()); // 拦截注解
		return enhancer.create();
	}

	public V execute(U request) throws Exception {
		AbstractEnhancer<U, V> proxy = (AbstractEnhancer<U, V>) newProxyInstance(this);
		// 代理类对象执行
		return (V) proxy.doExecute(request);
	}

	/** 自定义执行体 */
	protected abstract V doExecute(U request) throws Exception;
}
