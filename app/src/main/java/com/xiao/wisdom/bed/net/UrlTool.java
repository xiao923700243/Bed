package com.xiao.wisdom.bed.net;



/**
 * @author liyp
 * @version 1.0.0
 * @description Url工具
 * @createTime 2015/11/13
 * @editTime
 * @editor
 */
public abstract class UrlTool {

    public static final String BASE_HEAD = "http://www.lockaxial.com";
    public static final String REGISTER_URL = "/app/auth/userRegister";
    public static final String LOGIN_URL = "/app/auth/login";
    public static final String UPDATE_PASSWORD_URL = "/app/user/saveUser";
    public static final String TEMPKEY_LIST = "/app/unit/retrieveTempKeyList";
    public static final String CREATE_TEMPKEY = "/app/unit/saveTempKey";
    public static final String RECEVICE_ACCESS = "/app/unit/retrieveAccessList";
    public static final String RECEVICE_HOUSEINFO =  "/app/unit/retrieveUnitRelatedInfo";
    public static final String RECEVICE_CITYLIST = "/app/unit/retrieveCityList";
    public static final String RECEVICE_COMMUNITY = "/app/unit/retrieveCommunityList";
    public static final String RECEVICE_BLOCK = "/app/unit/retrieveBlockList";
    public static final String RECEVICE_UNIT = "/app/unit/retrieveBlockUnitList";
    public static final String APPLY_HOUSE = "/app/user/sendUnitApplication";
    public static final String OWNER_LIST = "/app/user/retrieveUnitOwnerList";
    public static final String CAR_LIST = "/app/unit/retrieveUnitRelatedInfo";
    public static final String APPLY_CAR = "/app/unit/saveUnitCar";
    public static final String RECEVICE_ADVICE = "/app/advice/retrieveAdviceList";
    public static final String RECEVICE_PROPERTY = "/app/unit/retrieveContactList";
    public static final String RECEVICE_TROUBLE = "/app/trouble/retrieveTroubleList";
    public static final String UP_LOAD_IMAGE = "/app/upload/image";
    public static final String RECEVICE_AROUND = "/app/shop/retrieveShopList";
    public static final String RECEVICE_SHOPTYPE = "/app/shop/retrieveCategoryList";
    public static final String RECEVICE_SHOPLIST = "/app/shop/retrieveGoodsList";
}
