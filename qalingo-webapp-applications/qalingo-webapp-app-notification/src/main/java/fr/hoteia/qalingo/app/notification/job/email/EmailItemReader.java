/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version ${license.version})
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.app.notification.job.email;

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
import fr.hoteia.qalingo.core.common.domain.Email;

/**
 * Thread-safe database {@link ItemReader} implementing the process indicator
 * pattern.
 * 
 */
public class EmailItemReader<T> implements ItemReader<CommonProcessIndicatorItemWrapper<Email, Email>>, StepExecutionListener, InitializingBean, DisposableBean {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	private final Object lock = new Object();

	private volatile boolean initialized = false;

	private volatile Iterator<Email> keysIterator;

	public void destroy() {
		initialized = false;
		keysIterator = null;
	}

	public final void afterPropertiesSet() throws Exception {
	}
	
	public CommonProcessIndicatorItemWrapper<Email, Email> read() throws DataAccessException {

		if (!initialized) {
			throw new ReaderNotOpenException("Reader must be open before it can be used.");
		}

		Email key = null;
		synchronized (lock) {
			if (keysIterator.hasNext()) {
				key = keysIterator.next();
			}
		}
//		LOG.debug("Retrieved key from list: " + key);

		if (key == null) {
			return null;
		}
		Email result = null;
//		try {
//			result = xxxxDAO.getxxxById(xxxxId);
//	    			
//		} catch (Exception e) {
//			LOG.error("", e);
//			throw new ReaderNotOpenException("Fail to load");
//		}
		
		return new CommonProcessIndicatorItemWrapper<Email, Email>(key, result);
	}

	public ExitStatus afterStep(StepExecution stepExecution) {
		
		destroy();
		
		return stepExecution.getExitStatus();
	}

	public void beforeStep(StepExecution stepExecution) {
		synchronized (lock) {
			if (keysIterator == null) {
				List<Email> keys = retrieveKeys();
				if(keys == null){
					keys = new ArrayList<Email>();
				}
				keysIterator = keys.iterator();
//				LOG.info("Keys obtained for staging.");
				initialized = true;
			}
		}
	}

	private List<Email> retrieveKeys() {
		synchronized (lock) {
			List<Email> keys = null;
//	    	try {
//
//    			keys = xxxDao.findIdsForSync();
//	    		
//			} catch (Exception e) {
//				LOG.error("Error during the IDs loading", e);
//			} 
			return keys;
		}
	}
	
}