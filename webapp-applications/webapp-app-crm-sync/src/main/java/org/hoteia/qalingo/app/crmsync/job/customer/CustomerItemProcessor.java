/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.app.crmsync.job.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;

import org.hoteia.qalingo.core.batch.CommonProcessIndicatorItemWrapper;
import org.hoteia.qalingo.core.domain.Customer;


/**
 * 
 */
public class CustomerItemProcessor<T> implements ItemProcessor<CommonProcessIndicatorItemWrapper<Customer, Customer>, Customer>, InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public void afterPropertiesSet() throws Exception {
	}

	/**
	 * Use the technical identifier to mark the input row as processed and
	 * return unwrapped item.
	 */
	public Customer process(CommonProcessIndicatorItemWrapper<Customer, Customer> wrapper) throws Exception {

		Customer sourceCustomer = wrapper.getItem();
			
		return sourceCustomer;
	}
	
}