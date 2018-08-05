package com.starkogi.mamamboga.MpesaApi;

/**
 *
 */

public class AppConstants {

    /**
     * Connection timeout duration
     */
    public static final int CONNECT_TIMEOUT = 60 * 1000;
    /**
     * Connection Read timeout duration
     */
    public static final int READ_TIMEOUT = 60 * 1000;
    /**
     * Connection write timeout duration
     */
    public static final int WRITE_TIMEOUT = 60 * 1000;
    /**
     * Base URL
     */
    public static final String BASE_URL = "https://sandbox.safaricom.co.ke/";
    /**
     * global topic to receive app wide push notifications
     */
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String SHARED_PREF = "ah_firebase";

    //STKPush Properties
    public static final String BUSINESS_SHORT_CODE = "174379";
    public static final String PASSKEY = "bfb279f9aa9bdbcf158e97dd71a467cd2e0c893059b10f78e6b72ada1ed2c919";
    public static final String TRANSACTION_TYPE = "CustomerPayBillOnline";
    public static final String PARTYB = "174379";
    public static final String CALLBACKURL = "http://starkogi.com/felister_water_bills/mpesa_api_callback.php?acc_no=";


    public static final String WATER_BILLS_BASE_URL = "http://starkogi.com/felister_water_bills/";
    //public static final String WATER_BILLS_BASE_URL = "http://10.0.2.2:86/WaterBillsFelister/";
    //public static final String WATER_BILLS_BASE_URL = "http://192.168.0.10:86/WaterBillsFelister/";


    public static final String MPESA_API_BASE_URL = "https://sandbox.safaricom.co.ke/";
}
