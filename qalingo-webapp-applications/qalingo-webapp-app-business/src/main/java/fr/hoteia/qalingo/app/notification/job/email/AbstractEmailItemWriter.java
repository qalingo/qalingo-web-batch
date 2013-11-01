/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package fr.hoteia.qalingo.app.notification.job.email;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import fr.hoteia.qalingo.core.dao.EmailDao;
import fr.hoteia.qalingo.core.domain.Email;

/**
 * 
 */
public abstract class AbstractEmailItemWriter implements ItemWriter<Email>, InitializingBean {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected EmailDao emailDao;

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(emailDao, "You must provide an EmailDao.");
	}
	
	public void write(List<? extends Email> processIndicatorItemWrapperList) throws Exception {
		for (Email email : processIndicatorItemWrapperList) {
			int processedCount = email.getProcessedCount();
			email.setProcessedCount(++processedCount);
			emailDao.saveOrUpdateEmail(email);
		}
	}
	
	public void setEmailDao(EmailDao emailDao) {
	    this.emailDao = emailDao;
    }

}