package tech.codegarage.tidetwist.util;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public class AllConstants {

    //Database table key
    public static final String TABLE_KEY_FOOD_ITEM = "product_id";
    public static final String TABLE_KEY_VOICE_SEARCH_ITEM_CATEGORY = "itemCategory";
    public static final String TABLE_KEY_VOICE_SEARCH_ITEM_ID = "itemId";
    public static final String TABLE_KEY_VOICE_SEARCH_ITEM_NAME = "itemName";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_1 = "key1";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_2 = "key2";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_3 = "key3";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_4 = "key4";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_5 = "key5";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_6 = "key6";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_7 = "key7";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_8 = "key8";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_9 = "key9";
    public static final String TABLE_KEY_VOICE_SEARCH_KEY_10 = "key10";

    //Voice search keyword
    public static final String ASSETS_PATH_VOICE_SEARCH_KEYWORD = "voicesearchkeyword/voice_search_item.xml";
    public static final String TAG_VOICE_SEARCH_KEYWORD = "item";

    //SSL
    public static final String R_S_I = "vusiontradelive";
    public static final String R_S_P = "5BA102B61C42D45953";
    public static final String D_S_I = "vusio5ba7a47bd3c0e";
    public static final String D_S_P = "vusio5ba7a47bd3c0e@ssl";

    //Session keys
    public static final String SESSION_KEY_USER = "SESSION_KEY_USER";
    public static final String SESSION_KEY_KITCHEN = "SESSION_KEY_KITCHEN";
    public static final String SESSION_KEY_DRIVER = "SESSION_KEY_DRIVER";
    public static final String SESSION_KEY_TIMES = "SESSION_KEY_TIMES";
    public static final String SESSION_KEY_CITY_WITH_AREA = "SESSION_KEY_CITY_WITH_AREA";
    public static final String SESSION_KEY_CUISINES = "SESSION_KEY_CUISINES";
    public static final String SESSION_KEY_SELECTED_VOICE_SEARCH = "SESSION_KEY_SELECTED_VOICE_SEARCH";

    //Intent keys
    public static final String INTENT_KEY_KITCHEN_TYPE = "INTENT_KEY_KITCHEN_TYPE";
    public static final String INTENT_KEY_DETAIL_TYPE = "INTENT_KEY_DETAIL_TYPE";
    public static final String INTENT_KEY_CUISINE = "INTENT_KEY_CUISINE";
    public static final String INTENT_KEY_TIME = "INTENT_KEY_TIME";
    public static final String INTENT_KEY_KITCHEN = "INTENT_KEY_KITCHEN";
    public static final String INTENT_KEY_FOOD_CATEGORY = "INTENT_KEY_FOOD_CATEGORY";
    public static final String INTENT_KEY_FOOD_REVIEW = "INTENT_KEY_FOOD_REVIEW";
    public static final String INTENT_KEY_FOOD_ITEM = "INTENT_KEY_FOOD_ITEM";
    public static final String INTENT_KEY_IS_KITCHEN_OPEN = "INTENT_KEY_IS_KITCHEN_OPEN";
    public static final String INTENT_KEY_KITCHEN_ID = "INTENT_KEY_KITCHEN_ID";
    public static final String INTENT_KEY_ORDER_ITEM = "INTENT_KEY_ORDER_ITEM";

    //Intent request code
    public static final int INTENT_REQUEST_CODE_KITCHEN_DETAIL = 422;
    public static final int INTENT_REQUEST_CODE_FOOD_ITEM_DETAIL = 420;
    public static final int INTENT_REQUEST_CODE_CHECKOUT = 4200;
    public static final int INTENT_REQUEST_CODE_REVIEW = 42000;
    public static final int INTENT_REQUEST_CODE_IMAGE_PICKER = 42100;
    public static final int INTENT_REQUEST_CODE_ORDER_ITEM_DETAIL = 42110;

    public static final int FLASHING_DEFAULT_DELAY = 1000;
    public static final int PER_PAGE_ITEM = 10;
    public static final int NAVIGATION_DRAWER_CLOSE_DELAY = 200;
    public static final int TRANSACTION_MAXIMUM_RANDOM_NUMBER = 6;
    public static final int WAVE_SWIPE_REFRESH_TIME = 3000;
    public static final String PREFIX_BASE64_STRING = "data:image/jpeg;base64,";

    public static final String DEFAULT_KITCHEN_TIMES = "{\"data\":[{\"time_id\":\"1\",\"prepare_time\":\"breakfast\",\"active\":\"1\",\"image\":\"https:\\/\\/d30y9cdsu7xlg0.cloudfront.net\\/png\\/23469-200.png\"},{\"time_id\":\"2\",\"prepare_time\":\"morning snacks\",\"active\":\"1\",\"image\":\"https:\\/\\/pearlvending.com\\/wp-content\\/uploads\\/2017\\/12\\/healthy.jpg\"},{\"time_id\":\"3\",\"prepare_time\":\"lunch\",\"active\":\"1\",\"image\":\"https:\\/\\/cdn1.iconfinder.com\\/data\\/icons\\/love-wedding-valentine-collection\\/90\\/134-512.png\"},{\"time_id\":\"4\",\"prepare_time\":\"evening snacks\",\"active\":\"1\",\"image\":\"http:\\/\\/www.myiconfinder.com\\/uploads\\/iconsets\\/256-256-007bcbfbd7c9d2c7d7b4b021ee745e22.png\"},{\"time_id\":\"5\",\"prepare_time\":\"dinner\",\"active\":\"1\",\"image\":\"https:\\/\\/thumbs.dreamstime.com\\/b\\/dinner-icon-logo-modern-line-style-high-quality-black-outline-pictogram-web-site-design-mobile-apps-vector-83645599.jpg\"},{\"time_id\":\"6\",\"prepare_time\":\"all\",\"active\":\"1\",\"image\":\"https:\\/\\/d30y9cdsu7xlg0.cloudfront.net\\/png\\/146647-200.png\"}],\"status\":1}";
    public static final String DEFAULT_CUISINES = "{\"status\":\"1\",\"data\":[{\"id\":\"1\",\"name\":\"Bangla\",\"image\":\"https:\\/\\/i.ytimg.com\\/vi\\/OkcxdHwv8KI\\/maxresdefault.jpg\",\"active\":\"1\",\"food_category\":[{\"category_id\":\"69\",\"cuisine_id\":\"1\",\"name\":\"Biriyani\",\"image\":\"http:\\/\\/a2zproductreviews.com\\/wp-content\\/uploads\\/2016\\/11\\/food-drinks.jpg\"},{\"category_id\":\"70\",\"cuisine_id\":\"1\",\"name\":\"Teheri\",\"image\":\"http:\\/\\/a2zproductreviews.com\\/wp-content\\/uploads\\/2016\\/11\\/food-drinks.jpg\"},{\"category_id\":\"77\",\"cuisine_id\":\"1\",\"name\":\"Chap\",\"image\":\"https:\\/\\/kfoods.com\\/images1\\/newrecipeicon\\/Beef-Chap_3548.jpg\"}]},{\"id\":\"6\",\"name\":\"Chinese\",\"image\":\"https:\\/\\/images.askmen.com\\/1080x540\\/2017\\/01\\/25-043929-can_you_make_chinese_takeout_healthy.jpg\",\"active\":\"1\",\"food_category\":[]},{\"id\":\"5\",\"name\":\"Fast Food\",\"image\":\"https:\\/\\/i.ytimg.com\\/vi\\/V6Vd1E9OL-U\\/maxresdefault.jpg\",\"active\":\"1\",\"food_category\":[{\"category_id\":\"75\",\"cuisine_id\":\"5\",\"name\":\"Pizza\",\"image\":\"http:\\/\\/static.sites.yp.com\\/var\\/m_c\\/c6\\/c66\\/11153421\\/157417-27579652.jpg\"},{\"category_id\":\"76\",\"cuisine_id\":\"5\",\"name\":\"Burger\",\"image\":\"https:\\/\\/www.seriouseats.com\\/recipes\\/images\\/2015\\/07\\/20150728-homemade-whopper-food-lab-35-1500x1125.jpg\"},{\"category_id\":\"78\",\"cuisine_id\":\"5\",\"name\":\"Fries\",\"image\":\"http:\\/\\/a2zproductreviews.com\\/wp-content\\/uploads\\/2016\\/11\\/food-drinks.jpg\"}]},{\"id\":\"4\",\"name\":\"Frozen\",\"image\":\"https:\\/\\/truffle-assets.imgix.net\\/5f5c384a-111-icecreamconecupcakes-dishland2.jpg\",\"active\":\"1\",\"food_category\":[{\"category_id\":\"71\",\"cuisine_id\":\"4\",\"name\":\"Ice - Cream\",\"image\":\"http:\\/\\/a2zproductreviews.com\\/wp-content\\/uploads\\/2016\\/11\\/food-drinks.jpg\"},{\"category_id\":\"72\",\"cuisine_id\":\"4\",\"name\":\"Faluda\",\"image\":\"http:\\/\\/a2zproductreviews.com\\/wp-content\\/uploads\\/2016\\/11\\/food-drinks.jpg\"}]},{\"id\":\"3\",\"name\":\"Indian\",\"image\":\"https:\\/\\/www.rewardsnetwork.com\\/wp-content\\/uploads\\/2016\\/12\\/IndianFood_Main.jpg\",\"active\":\"1\",\"food_category\":[{\"category_id\":\"73\",\"cuisine_id\":\"3\",\"name\":\"Dosa\",\"image\":\"http:\\/\\/a2zproductreviews.com\\/wp-content\\/uploads\\/2016\\/11\\/food-drinks.jpg\"},{\"category_id\":\"74\",\"cuisine_id\":\"3\",\"name\":\"Hydrabadi Biriyani\",\"image\":\"http:\\/\\/a2zproductreviews.com\\/wp-content\\/uploads\\/2016\\/11\\/food-drinks.jpg\"},{\"category_id\":\"79\",\"cuisine_id\":\"3\",\"name\":\"South Indian\",\"image\":\"https:\\/\\/i.ndtvimg.com\\/i\\/2017-11\\/instant-dosa_620x330_61509951712.jpg\"}]},{\"id\":\"2\",\"name\":\"Lunch Box\",\"image\":\"http:\\/\\/www.kidseatsmart.ca\\/wp-content\\/uploads\\/2017\\/08\\/guide-to-the-right-lunch-box.jpg\",\"active\":\"1\",\"food_category\":[]}]}";
    public static final String DEFAULT_CITY_WITH_AREA = "{\"status\":\"1\",\"data\":[{\"country_id\":\"1\",\"name\":\"Dhaka\",\"status\":\"1\",\"area\":[{\"zone_id\":\"1\",\"name\":\"Dhanmondi\"},{\"zone_id\":\"2\",\"name\":\"Gulshan 1\"},{\"zone_id\":\"3\",\"name\":\"Banani\"},{\"zone_id\":\"4\",\"name\":\"Baridhara\"},{\"zone_id\":\"5\",\"name\":\"Niketon\"},{\"zone_id\":\"6\",\"name\":\"Gulshan 2\"},{\"zone_id\":\"7\",\"name\":\"Lalmatia\"},{\"zone_id\":\"8\",\"name\":\"Kolabagan\"},{\"zone_id\":\"9\",\"name\":\"Mohammadpur\"},{\"zone_id\":\"10\",\"name\":\"Mirpur\"},{\"zone_id\":\"11\",\"name\":\"Warri\"},{\"zone_id\":\"12\",\"name\":\"Segunbagicha\"},{\"zone_id\":\"13\",\"name\":\"Purana Paltan\"},{\"zone_id\":\"14\",\"name\":\"Khilgaon\"},{\"zone_id\":\"15\",\"name\":\"Motijeel\"},{\"zone_id\":\"18\",\"name\":\"Rampura\"}]},{\"country_id\":\"2\",\"name\":\"Chittagong\",\"status\":\"1\",\"area\":[{\"zone_id\":\"16\",\"name\":\"Hali sahar\"},{\"zone_id\":\"17\",\"name\":\"Pahar tali\"}]}]}";
}