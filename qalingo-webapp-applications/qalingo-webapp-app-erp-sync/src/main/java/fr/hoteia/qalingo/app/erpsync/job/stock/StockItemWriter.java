/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version ${license.version})
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.app.erpsync.job.stock;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;

import fr.hoteia.qalingo.core.common.domain.ProductSkuStock;


/**
 * 
 */
public class  StockItemWriter implements ItemWriter<ProductSkuStock>, InitializingBean {
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	public final void afterPropertiesSet() throws Exception {
	}
	
	public void write(List<? extends ProductSkuStock> processIndicatorItemWrapperList) throws Exception {
		for (ProductSkuStock targetStock : processIndicatorItemWrapperList) {
			
	        
		}
	}

}
