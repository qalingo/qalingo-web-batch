/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.app.erpsync.job.stock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ReaderNotOpenException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;

import org.hoteia.qalingo.core.batch.CommonProcessIndicatorItemWrapper;
import org.hoteia.qalingo.core.domain.ProductSkuStock;

/**
 * Thread-safe database {@link ItemReader} implementing the process indicator
 * pattern.
 * 
 */
public class StockItemReader<T> implements ItemReader<CommonProcessIndicatorItemWrapper<ProductSkuStock, ProductSkuStock>>, StepExecutionListener, InitializingBean, DisposableBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final Object lock = new Object();

	private volatile boolean initialized = false;

	private volatile Iterator<ProductSkuStock> keysIterator;

	public void destroy() {
		initialized = false;
		keysIterator = null;
	}

	public final void afterPropertiesSet() throws Exception {
	}
	
	public CommonProcessIndicatorItemWrapper<ProductSkuStock, ProductSkuStock> read() throws DataAccessException {

		if (!initialized) {
			throw new ReaderNotOpenException("Reader must be open before it can be used.");
		}

		ProductSkuStock key = null;
		synchronized (lock) {
			if (keysIterator.hasNext()) {
				key = keysIterator.next();
			}
		}
//		logger.debug("Retrieved key from list: " + key);

		if (key == null) {
			return null;
		}
		ProductSkuStock result = null;
//		try {
//			result = xxxxDAO.getxxxxById(xxxxId);
//	    			
//		} catch (Exception e) {
//			logger.error("", e);
//			throw new ReaderNotOpenException("Fail to load");
//		}
		
		return new CommonProcessIndicatorItemWrapper<ProductSkuStock, ProductSkuStock>(key, result);
	}

	public ExitStatus afterStep(StepExecution stepExecution) {
		
		destroy();
		
		return stepExecution.getExitStatus();
	}

	public void beforeStep(StepExecution stepExecution) {
		synchronized (lock) {
			if (keysIterator == null) {
				List<ProductSkuStock> keys = retrieveKeys();
				if(keys == null){
					keys = new ArrayList<ProductSkuStock>();
				}
				keysIterator = keys.iterator();
//				logger.info("Keys obtained for staging.");
				initialized = true;
			}
		}
	}

	private List<ProductSkuStock> retrieveKeys() {
		synchronized (lock) {
			List<ProductSkuStock> keys = null;
//	    	try {
//
//    			keys = xxxx.findIdsForSync();
//	    		
//			} catch (Exception e) {
//				logger.error("Error during the IDs loading", e);
//			} 
			return keys;
		}
	}
	
}