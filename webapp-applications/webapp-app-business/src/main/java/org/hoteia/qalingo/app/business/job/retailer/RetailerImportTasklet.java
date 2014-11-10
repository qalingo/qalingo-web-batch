/**
 * Most of the code in the Qalingo project is copyrighted Hoteia and licensed
 * under the Apache License Version 2.0 (release version 0.8.0)
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *                   Copyright (c) Hoteia, 2012-2014
 * http://www.hoteia.com - http://twitter.com/hoteia - contact@hoteia.com
 *
 */
package org.hoteia.qalingo.app.business.job.retailer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.hoteia.qalingo.core.domain.Company;
import org.hoteia.qalingo.core.domain.Localization;
import org.hoteia.qalingo.core.domain.MarketArea;
import org.hoteia.qalingo.core.domain.Retailer;
import org.hoteia.qalingo.core.domain.RetailerAddress;
import org.hoteia.qalingo.core.domain.Store;
import org.hoteia.qalingo.core.domain.User;
import org.hoteia.qalingo.core.domain.Warehouse;
import org.hoteia.qalingo.core.domain.enumtype.StoreType;
import org.hoteia.qalingo.core.security.helper.SecurityUtil;
import org.hoteia.qalingo.core.service.LocalizationService;
import org.hoteia.qalingo.core.service.MarketService;
import org.hoteia.qalingo.core.service.RetailerService;
import org.hoteia.qalingo.core.service.UserService;
import org.hoteia.qalingo.core.service.WarehouseService;
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
public class RetailerImportTasklet implements Tasklet, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected UserService userService;
    protected RetailerService retailerService;
    protected WarehouseService warehouseService;
    protected MarketService marketService;
    protected LocalizationService localizationService;
    protected SecurityUtil securityUtil;
    
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(userService, "You must provide an UserService.");
        Assert.notNull(retailerService, "You must provide an RetailerService.");
        Assert.notNull(warehouseService, "You must provide an WarehouseService.");
        Assert.notNull(marketService, "You must provide an MarketService.");
        Assert.notNull(securityUtil, "You must provide an SecurityUtil.");
        Assert.notNull(localizationService, "You must provide an LocalizationService.");
	}

	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
	    String inputEncoding = "UTF8";
	    String filePath = "C:\\dev\\hoteia\\qalingo\\workspace\\datas\\retailers\\retailers.csv";
	    InputStreamReader reader = new InputStreamReader(new FileInputStream(new File(filePath)), inputEncoding);
	    logger.info("File CSV encoding: " + inputEncoding);
        
        CSVFormat csvFormat = CSVFormat.DEFAULT;
        
        CSVParser readerCSV = new CSVParser(reader, csvFormat);
        List<CSVRecord> records = null;
        try {
            records = readerCSV.getRecords();
        } catch (Exception e) {
            logger.error("Failed to load: " + filePath, e);
        }

        int count = 1;
        String companyCodePattern = "CPNY_";
        String userCodePattern = "USER_";
        String retailerCodePattern = "RTLR_";
        String storeCodePattern = "STOR_";
        String warehouseCodePattern = "WHSE_";
        for (CSVRecord record : records) {
            if(count > 1){
                String companyCode = companyCodePattern + (userService.getMaxCompanyId() + 1);
                String userCode = userCodePattern + (userService.getMaxUserId() + 1);
                String retailerCode = retailerCodePattern + (retailerService.getMaxRetailerId() + 1);
                String storeCode = storeCodePattern + (retailerService.getMaxStoreId() + 1);
                String warehouseCode = warehouseCodePattern + (warehouseService.getMaxWarehouseId() + 1);
                
                String countryCode = record.get(0);
                String name = record.get(1);
                String title = record.get(2);
                String lastname = StringUtils.capitalize(record.get(3));
                String firstname = StringUtils.capitalize(record.get(4));
                String address = record.get(5);
                String addressMore = record.get(6);
                String codePostal = record.get(7);
                String stateCode = null;
                if(StringUtils.isNotEmpty(codePostal)){
                    stateCode = codePostal.substring(0, 2);
                }
                String city = record.get(8);
                String phone = record.get(9);
                String fax = record.get(10);
                String email = record.get(11);
                String website = record.get(12);

                // COMPANY
                Company company = userService.getCompanyByName(name);
                if(company == null){
                    Localization defaultLocalization = localizationService.getLocalizationByCode(countryCode.toLowerCase());
                    
                    company = new Company();
                    company.setCode(companyCode);
                    company.setName(name);
                    company.setDescription(name);
                    company.setActive(true);
                    company.setAddress1(address);
                    company.setAddress2(addressMore);
//                    company.setAddressAdditionalInformation(addressAdditionalInformation);
                    company.setPostalCode(codePostal);
                    company.setCity(city);
                    company.setStateCode(stateCode);
//                    company.setAreaCode(areaCode);
                    company.setCountryCode(countryCode);
                    company.setCreatedByUserId(new Long(1));
                    company.setDefaultLocalization(defaultLocalization);
                    List<Localization> localizations = localizationService.findLocalizations();
                    for (Iterator<Localization> iterator = localizations.iterator(); iterator.hasNext();) {
                        Localization localization = (Localization) iterator.next();
                        company.getLocalizations().add(localization);
                    }
                    userService.saveOrUpdateCompany(company);
                    
                    // USER
                    if(StringUtils.isNotEmpty(email)){
                        User user = userService.getUserByLoginOrEmail(email);
                        if(user == null){
                            String login = userCode.toLowerCase().replace("_", "");
                            user = new User();
                            user.setCode(userCode);
                            user.setLogin(login);
                            user.setTitle(title);
                            user.setFirstname(firstname);
                            user.setLastname(lastname);
                            user.setEmail(email);
                            user.setPassword(securityUtil.encodePassword(login));
                            user.setActive(true);
                            user.setAddress1(address);
                            user.setAddress2(addressMore);
//                            user.setAddressAdditionalInformation(addressAdditionalInformation);
                            user.setPostalCode(codePostal);
                            user.setCity(city);
                            user.setStateCode(stateCode);
//                            user.setAreaCode(areaCode);
                            user.setCountryCode(countryCode);
                            user.setDefaultLocalization(defaultLocalization);
                            user.setCompany(company);
                            
//                            Set<UserGroup> groups = new HashSet<UserGroup>();
//                            UserGroup userGroup = userService.getUserGroupByCode("GROUP_BO_XXX");
//                            groups.add(userGroup);
//                            user.setGroups(groups);

                            userService.saveOrUpdateUser(user);
                        }
                    }
                    
                    // RETAILER
                    Retailer retailer = new Retailer();
                    retailer.setActive(false);
                    retailer.setCode(retailerCode);
                    retailer.setName(name);
                    retailer.setDescription(name);
//                    retailer.setOfficialRetailer();
//                    retailer.setBrand(isBrand);
//                    retailer.setEcommerce(isEcommerce);
//                    retailer.setCorner(isCorner);
                    RetailerAddress retailerAddress = new RetailerAddress();
                    retailerAddress.setAddress1(address);
                    retailerAddress.setAddress2(addressMore);
                    retailerAddress.setPostalCode(codePostal);
                    retailerAddress.setCity(city);
                    retailerAddress.setStateCode(stateCode);
                    retailerAddress.setCountryCode(countryCode);
                    retailer.getAddresses().add(retailerAddress);
                    retailer = retailerService.saveOrUpdateRetailer(retailer);
                    
                    // STORE
                    Store store = new Store();
                    store.setActive(false);
                    store.setCode(storeCode);
                    store.setType(StoreType.SHOP.getPropertyKey());
                    store.setName(name);
                    store.setAddress1(address);
                    store.setAddress2(addressMore);
//                    store.setAddressAdditionalInformation(addressAdditionalInformation);
                    store.setPostalCode(codePostal);
                    store.setCity(city);
                    store.setStateCode(stateCode);
//                    store.setAreaCode(areaCode);
                    store.setCountryCode(countryCode);
                    store.setEmail(email);
                    store.setPhone(phone);
                    store.setFax(fax);
                    store.setWebsite(website);
                    store.setRetailer(retailer);

                    retailerService.saveOrUpdateStore(store);
                    
                    // WHAREHOUSE
                    Warehouse warehouse = new Warehouse();
                    warehouse.setCode(warehouseCode);
                    warehouse.setName(name);
                    warehouse.setDescription(name);
                    warehouse.setAddress1(address);
                    warehouse.setAddress2(addressMore);
//                    warehouse.setAddressAdditionalInformation(addressAdditionalInformation);
                    warehouse.setPostalCode(codePostal);
                    warehouse.setCity(city);
                    warehouse.setStateCode(stateCode);
//                    warehouse.setAreaCode(areaCode);
                    warehouse.setCountryCode(countryCode);

                    warehouseService.saveOrUpdateWarehouse(warehouse);

                    List<MarketArea> marketAreas = marketService.getMarketAreaByGeolocCountryCode(countryCode);
                    MarketArea marketArea = marketAreas.iterator().next();
                    
//                    WarehouseMarketAreaRel warehouseMarketAreaRel = new WarehouseMarketAreaRel(marketArea, warehouse);
//                    warehouse.getWarehouseMarketAreaRels().add(warehouseMarketAreaRel);
//                    warehouseService.saveOrUpdateWarehouse(warehouse);

                    retailer.getWarehouses().add(warehouse);
                    retailer = retailerService.saveOrUpdateRetailer(retailer);
                    
                    company.getRetailers().add(retailer);
                    userService.saveOrUpdateCompany(company);
                }
            }
            count++;
        }
        
		return RepeatStatus.FINISHED;
	}
	
	public void setUserService(UserService userService) {
        this.userService = userService;
    }
	
	public void setRetailerService(RetailerService retailerService) {
        this.retailerService = retailerService;
    }
	
	public void setWarehouseService(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }
	
	public void setMarketService(MarketService marketService) {
        this.marketService = marketService;
    }
	
	public void setSecurityUtil(SecurityUtil securityUtil) {
        this.securityUtil = securityUtil;
    }
	
	public void setLocalizationService(LocalizationService localizationService) {
        this.localizationService = localizationService;
    }

}