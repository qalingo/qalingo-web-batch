/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.app.erpsync.job.stock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import fr.hoteia.qalingo.core.batch.CommonProcessIndicatorItemWrapper;
import fr.hoteia.qalingo.core.domain.ProductSkuStock;


/**
 * 
 */
public class StockItemProcessor<T> implements ItemProcessor<CommonProcessIndicatorItemWrapper<ProductSkuStock, ProductSkuStock>, ProductSkuStock>, InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void afterPropertiesSet() throws Exception {
	}

	/**
	 * Use the technical identifier to mark the input row as processed and
	 * return unwrapped item.
	 */
	public ProductSkuStock process(CommonProcessIndicatorItemWrapper<ProductSkuStock, ProductSkuStock> wrapper) throws Exception {

		ProductSkuStock sourceStock = wrapper.getItem();
		
			
		return sourceStock;
	}
	
}