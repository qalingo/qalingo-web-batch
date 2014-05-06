/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.app.business.job.email.process;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

import org.hoteia.qalingo.app.business.job.email.AbstractEmailItemReader;

/**
 * Thread-safe database {@link ItemReader} implementing the process indicator
 * pattern.
 * 
 */
public class EmailItemReader<T> extends AbstractEmailItemReader<T> {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected List<Long> retrieveKeys() {
		synchronized (lock) {
			List<Long> keys = null;
	    	try {
    			keys = emailDao.findIdsForEmailSync();
			} catch (Exception e) {
				logger.error("Error during the IDs loading", e);
			} 
			return keys;
		}
	}
	
}