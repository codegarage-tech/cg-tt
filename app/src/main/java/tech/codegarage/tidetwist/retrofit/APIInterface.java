package tech.codegarage.tidetwist.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import tech.codegarage.tidetwist.model.AppUser;
import tech.codegarage.tidetwist.model.City;
import tech.codegarage.tidetwist.model.Cuisine;
import tech.codegarage.tidetwist.model.DoOrder;
import tech.codegarage.tidetwist.model.DriverUser;
import tech.codegarage.tidetwist.model.FoodItem;
import tech.codegarage.tidetwist.model.Kitchen;
import tech.codegarage.tidetwist.model.KitchenTime;
import tech.codegarage.tidetwist.model.KitchenUser;
import tech.codegarage.tidetwist.model.Order;
import tech.codegarage.tidetwist.model.ParamAddFoodItem;
import tech.codegarage.tidetwist.model.ParamAppUser;
import tech.codegarage.tidetwist.model.ParamDoFavorite;
import tech.codegarage.tidetwist.model.ParamDoOrder;
import tech.codegarage.tidetwist.model.ParamDriverLocationUpdate;
import tech.codegarage.tidetwist.model.ParamDriverLogin;
import tech.codegarage.tidetwist.model.ParamDriverLogout;
import tech.codegarage.tidetwist.model.ParamDriverRegistration;
import tech.codegarage.tidetwist.model.ParamKitchenListByCuisine;
import tech.codegarage.tidetwist.model.ParamKitchenListByFastDelivery;
import tech.codegarage.tidetwist.model.ParamKitchenListByFavorite;
import tech.codegarage.tidetwist.model.ParamKitchenListByOffer;
import tech.codegarage.tidetwist.model.ParamKitchenListByPopular;
import tech.codegarage.tidetwist.model.ParamKitchenListBySearch;
import tech.codegarage.tidetwist.model.ParamKitchenListByTime;
import tech.codegarage.tidetwist.model.ParamKitchenLogin;
import tech.codegarage.tidetwist.model.ParamKitchenLogout;
import tech.codegarage.tidetwist.model.ParamKitchenRegistration;
import tech.codegarage.tidetwist.model.ParamPromoCode;
import tech.codegarage.tidetwist.model.ParamReviewItem;
import tech.codegarage.tidetwist.model.ParamSendVerificationCode;
import tech.codegarage.tidetwist.model.ParamUpdateOrderStatus;
import tech.codegarage.tidetwist.model.Review;

/**
 * @author Md. Rashadul Alam
 * Email: rashed.droid@gmail.com
 */
public interface APIInterface {

    @POST("user/sendVerificationCode")
    Call<APIResponse> apiSendVerificationCode(@Body ParamSendVerificationCode paramSendVerificationCode);

    @GET("cuisine/lists")
    Call<APIResponse<List<Cuisine>>> apiGetAllCuisines();

    @GET("locations/lists")
    Call<APIResponse<List<City>>> apiGetAllCitiesWithAreas();

    @GET("kitchen/timeLists")
    Call<APIResponse<List<KitchenTime>>> apiGetAllKitchenTimes();

    @GET("food_item/getReviewLists/{item_id}")
    Call<APIResponse<List<Review>>> apiGetReviewListByFoodItem(@Path("item_id") String item_id);

    @GET("order/listsByUser/{user_id}")
    Call<APIResponse<List<Order>>> apiGetOrderListsByUser(@Path("user_id") String user_id);

    @GET("order/listsByKitchen/{user_id}")
    Call<APIResponse<List<Order>>> apiGetOrderListsByKitchen(@Path("user_id") String user_id);

    @GET("drivers/ordersLists/{user_id}}")
    Call<APIResponse<List<Order>>> apiGetOrderListsByDriver(@Path("user_id") String user_id);

    @POST("user/add")
    Call<APIResponse<List<AppUser>>> apiCreateAppUser(@Body ParamAppUser paramAppUser);

    @POST("kitchen/login")
    Call<APIResponse<List<KitchenUser>>> apiKitchenLogin(@Body ParamKitchenLogin paramKitchenLogin);

    @POST("kitchen/signup")
    Call<APIResponse<List<KitchenUser>>> apiRequestForKitchenRegistration(@Body ParamKitchenRegistration paramKitchenRegistration);

    @POST("kitchen/getByAreaAndCusine")
    Call<APIResponse<List<Kitchen>>> apiGetAllKitchensBySearch(@Body ParamKitchenListBySearch paramKitchenListBySearch);

    @POST("kitchen/getByUserLatLngAndCusine")
    Call<APIResponse<List<Kitchen>>> apiGetAllKitchensByCuisine(@Body ParamKitchenListByCuisine paramKitchenListByCuisine);

    @POST("kitchen/getByUserLatLngAndTime")
    Call<APIResponse<List<Kitchen>>> apiGetAllKitchensByTime(@Body ParamKitchenListByTime paramKitchenListByTime);

    @POST("kitchen/getByUserLatLngAndOffer")
    Call<APIResponse<List<Kitchen>>> apiGetAllKitchensByOffer(@Body ParamKitchenListByOffer paramKitchenListByOffer);

    @POST("kitchen/getByUserLatLngAndPopular")
    Call<APIResponse<List<Kitchen>>> apiGetAllKitchensByPopular(@Body ParamKitchenListByPopular paramKitchenListByPopular);

    @POST("kitchen/getByUserLatLngAndDeliveryTime")
    Call<APIResponse<List<Kitchen>>> apiGetAllKitchensByDeliveryTime(@Body ParamKitchenListByFastDelivery paramKitchenListByFastDelivery);

    @POST("kitchen/getByUserFavorites")
    Call<APIResponse<List<Kitchen>>> apiGetAllKitchensByFavorite(@Body ParamKitchenListByFavorite paramKitchenListByFavorite);

    @POST("food_item/add_review")
    Call<APIResponse<List<Review>>> apiAddReview(@Body ParamReviewItem paramReviewItem);

    @POST("food_item/do_favourite")
    Call<APIResponse> apiDoFavouriteFoodItem(@Body ParamDoFavorite paramDoFavorite);

    @POST("food_item/add")
    Call<APIResponse<List<FoodItem>>> apiRequestForAddingFoodItemByKitchen(@Body ParamAddFoodItem paramAddFoodItem);

    @POST("order/add")
    Call<APIResponse<List<DoOrder>>> apiDoOrder(@Body ParamDoOrder order);

    @POST("coupon_code/checkCouponCode")
    Call<APIResponse> apiCheckPromoCode(@Body ParamPromoCode order);

    @POST("drivers/login")
    Call<APIResponse<List<DriverUser>>> apiDriverLogin(@Body ParamDriverLogin paramDriverLogin);

    @POST("drivers/add")
    Call<APIResponse<List<DriverUser>>> apiRequestForDriverRegistration(@Body ParamDriverRegistration paramDriverRegistration);

    @POST("drivers/updateOrderStatus")
    Call<APIResponse> apiUpdateOrderStatus(@Body ParamUpdateOrderStatus paramUpdateOrderStatus);

    @POST("kitchen/logout")
    Call<APIResponse> apiKitchenLogout(@Body ParamKitchenLogout paramKitchenLogout);

    @POST("drivers/logout")
    Call<APIResponse> apiDriverLogout(@Body ParamDriverLogout paramDriverLogout);

    @POST("drivers/updateLatLng")
    Call<APIResponse> apiUpdateDriverLocation(@Body ParamDriverLocationUpdate paramDriverLocationUpdate);
}