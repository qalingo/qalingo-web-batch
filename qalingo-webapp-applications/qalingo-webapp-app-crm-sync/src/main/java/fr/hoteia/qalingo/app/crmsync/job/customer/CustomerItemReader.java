/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.app.crmsync.job.customer;

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

import fr.hoteia.qalingo.core.batch.CommonProcessIndicatorItemWrapper;
import fr.hoteia.qalingo.core.common.domain.Customer;

/**
 * Thread-safe database {@link ItemReader} implementing the process indicator
 * pattern.
 * 
 */
public class CustomerItemReader<T> implements ItemReader<CommonProcessIndicatorItemWrapper<Customer, Customer>>, StepExecutionListener, InitializingBean, DisposableBean {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final Object lock = new Object();

	private volatile boolean initialized = false;

	private volatile Iterator<Customer> keysIterator;

	public void destroy() {
		initialized = false;
		keysIterator = null;
	}

	public final void afterPropertiesSet() throws Exception {
	}
	
	public CommonProcessIndicatorItemWrapper<Customer, Customer> read() throws DataAccessException {

		if (!initialized) {
			throw new ReaderNotOpenException("Reader must be open before it can be used.");
		}

		Customer key = null;
		synchronized (lock) {
			if (keysIterator.hasNext()) {
				key = keysIterator.next();
			}
		}
//		LOG.debug("Retrieved key from list: " + key);

		if (key == null) {
			return null;
		}
		Customer result = null;
//		try {
//			result = xxxxDAO.getxxxById(xxxxId);
//	    			
//		} catch (Exception e) {
//			LOG.error("", e);
//			throw new ReaderNotOpenException("Fail to load");
//		}
		
		return new CommonProcessIndicatorItemWrapper<Customer, Customer>(key, result);
	}

	public ExitStatus afterStep(StepExecution stepExecution) {
		
		destroy();
		
		return stepExecution.getExitStatus();
	}

	public void beforeStep(StepExecution stepExecution) {
		synchronized (lock) {
			if (keysIterator == null) {
				List<Customer> keys = retrieveKeys();
				if(keys == null){
					keys = new ArrayList<Customer>();
				}
				keysIterator = keys.iterator();
//				LOG.info("Keys obtained for staging.");
				initialized = true;
			}
		}
	}

	private List<Customer> retrieveKeys() {
		synchronized (lock) {
			List<Customer> keys = null;
//	    	try {
//
//    			keys = xxxxDao.findIdsForSync();
//	    		
//			} catch (Exception e) {
//				LOG.error("Error during the IDs loading", e);
//			} 
			return keys;
		}
	}
	
}