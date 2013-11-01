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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Blob;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;

import fr.hoteia.qalingo.core.Constants;
import fr.hoteia.qalingo.core.batch.CommonProcessIndicatorItemWrapper;
import fr.hoteia.qalingo.core.dao.EmailDao;
import fr.hoteia.qalingo.core.domain.Email;
import fr.hoteia.qalingo.core.util.impl.MimeMessagePreparatorImpl;


/**
 * 
 */
public abstract class AbstractEmailItemProcessor<T> implements ItemProcessor<CommonProcessIndicatorItemWrapper<Long, Email>, Email>, InitializingBean {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private JavaMailSender mailSender;
	
	protected EmailDao emailDao;

	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(mailSender, "You must provide a JavaMailSender.");
		Assert.notNull(emailDao, "You must provide an EmailDao.");
	}

	public Email process(CommonProcessIndicatorItemWrapper<Long, Email> wrapper) throws Exception {
		Email email = wrapper.getItem();
		Blob emailcontent = email.getEmailContent();
		
		InputStream is = emailcontent.getBinaryStream();
	    ObjectInputStream oip = new ObjectInputStream(is);
	    Object object = oip.readObject();
	    
	    MimeMessagePreparatorImpl mimeMessagePreparator = (MimeMessagePreparatorImpl) object;
	    
	    oip.close();
	    is.close();

	    try {
	    	// SANITY CHECK
	    	if(email.getStatus().equals(Email.EMAIl_STATUS_PENDING)){
	    		
	    		if (mimeMessagePreparator.isMirroringActivated()) {
	    			String filePathToSave = mimeMessagePreparator.getMirroringFilePath();
	                File file = new File(filePathToSave);
	                
	                // SANITY CHECK : create folders
                	String absoluteFolderPath = file.getParent();
                	File absolutePathFile = new File(absoluteFolderPath);
                	if(!absolutePathFile.exists()){
                		absolutePathFile.mkdirs();
                	}
                	
	                if(!file.exists()){
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, Constants.UTF8);
                        Writer out = new BufferedWriter(outputStreamWriter);
                        if(StringUtils.isNotEmpty(mimeMessagePreparator.getHtmlContent())) {
	                        out.write(mimeMessagePreparator.getHtmlContent());
                        } else {
	                        out.write(mimeMessagePreparator.getPlainTextContent());
                        }
                        
                        try {
                            if (out != null){
                                out.close();
                            }
                        } catch (IOException e) {
                            logger.debug("Cannot close the file", e);
                        }
	                } else {
	                    logger.debug("File already exists : " + filePathToSave);
	                }
	            }
	    		
				mailSender.send(mimeMessagePreparator);
				email.setStatus(Email.EMAIl_STATUS_SENDED);
	    	} else {
	    		logger.warn("Batch try to send email was already sended!");
	    	}
            
        } catch (Exception e) {
        	logger.error("Fail to send email! Exception is save in database, id:" + email.getId());
    		email.setStatus(Email.EMAIl_STATUS_ERROR);
    		emailDao.handleEmailException(email, e);
        }

	    return email;
    }
	
	public void setMailSender(JavaMailSender mailSender) {
	    this.mailSender = mailSender;
    }

	public void setEmailDao(EmailDao emailDao) {
	    this.emailDao = emailDao;
    }
	
}