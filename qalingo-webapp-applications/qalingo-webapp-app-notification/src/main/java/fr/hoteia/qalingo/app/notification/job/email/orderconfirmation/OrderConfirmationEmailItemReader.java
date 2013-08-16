/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.app.notification.job.email.orderconfirmation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;

import fr.hoteia.qalingo.app.notification.job.email.AbstractEmailItemReader;
import fr.hoteia.qalingo.core.domain.Email;

/**
 * Thread-safe database {@link ItemReader} implementing the process indicator
 * pattern.
 * 
 */
public class OrderConfirmationEmailItemReader<T> extends AbstractEmailItemReader<T> {

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Override
	protected List<Long> retrieveKeys() {
		synchronized (lock) {
			List<Long> keys = null;
	    	try {
    			keys = emailDao.findIdsForEmailSync(Email.EMAIl_TYPE_ORDER_CONFIRMATION);
			} catch (Exception e) {
				LOG.error("Error during the IDs loading", e);
			} 
			return keys;
		}
	}
	
}