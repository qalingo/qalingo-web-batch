/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.app.business.job.email;

import java.util.List;

import org.hoteia.qalingo.core.domain.Email;
import org.hoteia.qalingo.core.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 
 */
public abstract class AbstractEmailItemWriter implements ItemWriter<Email>, InitializingBean {
	
	private final Logger logger = LoggerFactory.getLogger(getClass());

	protected EmailService emailService;

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(emailService, "You must provide an EmailService.");
	}
	
	public void write(List<? extends Email> processIndicatorItemWrapperList) throws Exception {
		for (Email email : processIndicatorItemWrapperList) {
			int processedCount = email.getProcessedCount();
			email.setProcessedCount(++processedCount);
			emailService.saveOrUpdateEmail(email);
		}
	}
	
	public void setEmailService(EmailService emailService) {
	    this.emailService = emailService;
    }

}