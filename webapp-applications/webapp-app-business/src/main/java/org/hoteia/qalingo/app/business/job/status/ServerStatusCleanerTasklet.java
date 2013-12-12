/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.7.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2013
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.app.business.job.status;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import org.hoteia.qalingo.core.dao.ServerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * 
 */
public class ServerStatusCleanerTasklet implements Tasklet, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

	protected ServerDao serverDao;

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(serverDao, "You must provide an ServerDao.");
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		GregorianCalendar calendar = new GregorianCalendar(); 
		int day = calendar.get(GregorianCalendar.DAY_OF_YEAR);
		
		// TODO : Denis : 20131209 : add number of day configuration in database ?
		
		calendar.set(GregorianCalendar.DAY_OF_YEAR, day - 7);
		int row = serverDao.deleteSendedServerStatus(new Timestamp(calendar.getTimeInMillis()));
		logger.debug(row + " server status deleted");
		return RepeatStatus.FINISHED;
	}
	
	public void setServerDao(ServerDao serverDao) {
        this.serverDao = serverDao;
    }

}
